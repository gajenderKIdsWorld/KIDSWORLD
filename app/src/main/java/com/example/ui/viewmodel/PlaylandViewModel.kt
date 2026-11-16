package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.model.GameProgress
import com.example.data.model.StickerReward
import com.example.data.model.UserProfile
import com.example.data.repository.PlaylandRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PlaylandGameCatalogItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val ageTag: String,
    val bannerResId: Int,
    val accentHex: Long,
    val stickerId: String,
    val stickerTitle: String
)

data class StickerCatalogItem(
    val id: String,
    val title: String,
    val category: String,
    val unlockHint: String
)

sealed interface PlaylandUiState {
    data object Loading : PlaylandUiState
    data class Ready(
        val profile: UserProfile,
        val gamesMap: Map<String, GameProgress>,
        val unlockedStickers: List<StickerReward>
    ) : PlaylandUiState
    data class Error(val message: String) : PlaylandUiState
}

class PlaylandViewModel(
    private val repository: PlaylandRepository,
    private val currentUserId: String,
    initialDisplayName: String
) : ViewModel() {

    val gameCatalog: List<PlaylandGameCatalogItem> = listOf(
        PlaylandGameCatalogItem(
            id = "phonics",
            title = "ABC Cloud Hop",
            subtitle = "Match playful letters & cheerful words",
            category = "Letters",
            ageTag = "Ages 3-6",
            bannerResId = R.drawable.img_game_phonics_1794833803120,
            accentHex = 0xFF38BDF8,
            stickerId = "phonics_star",
            stickerTitle = "ABC Cloud Champ"
        ),
        PlaylandGameCatalogItem(
            id = "counting",
            title = "Sunny Berry Count",
            subtitle = "Count sweet berries & golden stars",
            category = "Numbers",
            ageTag = "Ages 3-7",
            bannerResId = R.drawable.img_game_counting_1794833801094,
            accentHex = 0xFFFB7185,
            stickerId = "counting_berry",
            stickerTitle = "Berry Counter"
        ),
        PlaylandGameCatalogItem(
            id = "shapes",
            title = "Bubble Shape Match",
            subtitle = "Spot bouncy stars, hearts & triangles",
            category = "Shapes",
            ageTag = "Ages 3-6",
            bannerResId = R.drawable.img_game_shapes_1794833801184,
            accentHex = 0xFF34D399,
            stickerId = "shapes_wiz",
            stickerTitle = "Shape Explorer"
        ),
        PlaylandGameCatalogItem(
            id = "music",
            title = "Dreamy Note Garden",
            subtitle = "Tap rainbow xylophone notes in rhythm",
            category = "Music",
            ageTag = "Ages 4-8",
            bannerResId = R.drawable.img_game_music_1794833801438,
            accentHex = 0xFFA78BFA,
            stickerId = "music_maestro",
            stickerTitle = "Rainbow Maestro"
        )
    )

    val stickerCatalog: List<StickerCatalogItem> = listOf(
        StickerCatalogItem(
            id = "phonics_star",
            title = "ABC Cloud Champ",
            category = "phonics",
            unlockHint = "Earn 2+ stars in ABC Cloud Hop"
        ),
        StickerCatalogItem(
            id = "counting_berry",
            title = "Berry Counter",
            category = "counting",
            unlockHint = "Earn 2+ stars in Sunny Berry Count"
        ),
        StickerCatalogItem(
            id = "shapes_wiz",
            title = "Shape Explorer",
            category = "shapes",
            unlockHint = "Earn 2+ stars in Bubble Shape Match"
        ),
        StickerCatalogItem(
            id = "music_maestro",
            title = "Rainbow Maestro",
            category = "music",
            unlockHint = "Earn 2+ stars in Dreamy Note Garden"
        )
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _isParentUnlocked = MutableStateFlow(false)
    val isParentUnlocked: StateFlow<Boolean> = _isParentUnlocked.asStateFlow()

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    init {
        viewModelScope.launch {
            val result = repository.ensureUserProfileInitialized(initialDisplayName)
            result.exceptionOrNull()?.let { err ->
                Log.w(TAG, "Profile initialization notice", err)
            }
        }
    }

    val uiState: StateFlow<PlaylandUiState> = combine(
        repository.observeUserProfile(currentUserId),
        repository.observeGameProgress(currentUserId),
        repository.observeStickers(currentUserId)
    ) { profile, progressList, stickers ->
        val safeProfile = profile ?: UserProfile(
            userId = currentUserId,
            learnerName = "Little Explorer"
        )
        PlaylandUiState.Ready(
            profile = safeProfile,
            gamesMap = progressList.associateBy { it.gameId },
            unlockedStickers = stickers
        ) as PlaylandUiState
    }
        .catch { error ->
            val firestoreError = generateSequence(error) { it.cause }
                .filterIsInstance<FirebaseFirestoreException>()
                .firstOrNull()
            if (firestoreError == null && error is CancellationException) throw error
            val rootError = firestoreError ?: error
            Log.w(TAG, "Error observing Playland state", rootError)
            emit(PlaylandUiState.Error(rootError.message ?: "Unable to sync your Playland data."))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = PlaylandUiState.Loading
        )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun unlockParentZone(enteredPin: String, expectedPin: String): Boolean {
        return if (enteredPin == expectedPin) {
            _isParentUnlocked.value = true
            true
        } else {
            false
        }
    }

    fun lockParentZone() {
        _isParentUnlocked.value = false
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }

    fun updateLearnerProfile(name: String, avatarId: String) {
        viewModelScope.launch {
            val result = repository.updateLearnerProfile(name, avatarId)
            _actionMessage.value = if (result.isSuccess) {
                "Explorer profile updated!"
            } else {
                "Could not update profile right now."
            }
        }
    }

    fun updateParentSettings(
        soundEnabled: Boolean,
        dailyTimerEnabled: Boolean,
        dailyLimitMinutes: Int,
        parentPin: String
    ) {
        viewModelScope.launch {
            val result = repository.updateParentSettings(
                soundEnabled = soundEnabled,
                dailyTimerEnabled = dailyTimerEnabled,
                dailyLimitMinutes = dailyLimitMinutes,
                parentPin = parentPin
            )
            _actionMessage.value = if (result.isSuccess) {
                "Parent preferences saved!"
            } else {
                "Could not save settings. Please try again."
            }
        }
    }

    fun recordGameResult(
        gameId: String,
        earnedStars: Int,
        score: Int,
        xpReward: Int
    ) {
        val gameMeta = gameCatalog.firstOrNull { it.id == gameId }
        viewModelScope.launch {
            val result = repository.recordGameCompletion(
                gameId = gameId,
                earnedStars = earnedStars,
                score = score,
                xpReward = xpReward,
                unlockStickerId = gameMeta?.stickerId,
                unlockStickerTitle = gameMeta?.stickerTitle
            )
            if (result.isFailure) {
                _actionMessage.value = "Progress saved locally; cloud sync retrying."
            }
        }
    }

    fun resetProgress() {
        viewModelScope.launch {
            val result = repository.resetAllProgress()
            _actionMessage.value = if (result.isSuccess) {
                "Progress reset for a fresh adventure!"
            } else {
                "Unable to reset progress right now."
            }
        }
    }

    private companion object {
        const val TAG = "PlaylandVM"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
