package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.GameProgress
import com.example.data.model.StickerReward
import com.example.data.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlin.math.max

class PlaylandRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
        val docRef = db.collection("users").document(userId)
        emitAll(
            docRef.snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(
                            UserProfile::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )
                    } else {
                        null
                    }
                }
                .catch { error ->
                    logFlowError(error, OperationType.GET, docRef.path, userId)
                    throw error
                }
        )
    }

    fun observeGameProgress(userId: String): Flow<List<GameProgress>> = flow {
        val collectionRef = db.collection("users").document(userId).collection("gameProgress")
        emitAll(
            collectionRef.snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        GameProgress::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    logFlowError(error, OperationType.LIST, collectionRef.path, userId)
                    throw error
                }
        )
    }

    fun observeStickers(userId: String): Flow<List<StickerReward>> = flow {
        val collectionRef = db.collection("users").document(userId).collection("stickers")
        emitAll(
            collectionRef.snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        StickerReward::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    logFlowError(error, OperationType.LIST, collectionRef.path, userId)
                    throw error
                }
        )
    }

    suspend fun ensureUserProfileInitialized(defaultName: String = "Little Explorer"): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        return try {
            val snap = docRef.get().await()
            if (!snap.exists()) {
                val safeName = defaultName.trim().ifBlank { "Little Explorer" }.take(30)
                val payload = mapOf(
                    "userId" to uid,
                    "learnerName" to safeName,
                    "avatarId" to "star",
                    "xp" to 0L,
                    "level" to 1L,
                    "totalStars" to 0L,
                    "soundEnabled" to true,
                    "dailyTimerEnabled" to false,
                    "dailyLimitMinutes" to 30L,
                    "parentPin" to "1234",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path, auth)
            Result.failure(e)
        }
    }

    suspend fun updateLearnerProfile(learnerName: String, avatarId: String): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val safeName = learnerName.trim().ifBlank { "Little Explorer" }.take(30)
        return try {
            docRef.update(
                mapOf(
                    "learnerName" to safeName,
                    "avatarId" to avatarId,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path, auth)
            Result.failure(e)
        }
    }

    suspend fun updateParentSettings(
        soundEnabled: Boolean,
        dailyTimerEnabled: Boolean,
        dailyLimitMinutes: Int,
        parentPin: String
    ): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val clampedLimit = dailyLimitMinutes.coerceIn(10, 120)
        val sanitizedPin = parentPin.filter { it.isDigit() }.take(4).padStart(4, '0')
        return try {
            docRef.update(
                mapOf(
                    "soundEnabled" to soundEnabled,
                    "dailyTimerEnabled" to dailyTimerEnabled,
                    "dailyLimitMinutes" to clampedLimit.toLong(),
                    "parentPin" to sanitizedPin,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path, auth)
            Result.failure(e)
        }
    }

    suspend fun recordGameCompletion(
        gameId: String,
        earnedStars: Int,
        score: Int,
        xpReward: Int,
        unlockStickerId: String? = null,
        unlockStickerTitle: String? = null
    ): Result<Unit> {
        val uid = requireUserId()
        val userRef = db.collection("users").document(uid)
        val gameRef = userRef.collection("gameProgress").document(gameId)
        val clampedStars = earnedStars.coerceIn(0, 3).toLong()

        return try {
            val userSnap = userRef.get().await()
            if (!userSnap.exists()) {
                ensureUserProfileInitialized().getOrThrow()
            }

            val gameSnap = gameRef.get().await()
            val oldStars = if (gameSnap.exists()) (gameSnap.getLong("stars") ?: 0L) else 0L
            val oldBest = if (gameSnap.exists()) (gameSnap.getLong("bestScore") ?: 0L) else 0L
            val oldPlays = if (gameSnap.exists()) (gameSnap.getLong("playsCount") ?: 0L) else 0L

            val newStars = max(oldStars, clampedStars)
            val newBest = max(oldBest, score.toLong().coerceAtLeast(0L))
            val newPlays = oldPlays + 1L
            val completed = newStars > 0L

            if (!gameSnap.exists()) {
                gameRef.set(
                    mapOf(
                        "userId" to uid,
                        "gameId" to gameId,
                        "stars" to newStars,
                        "bestScore" to newBest,
                        "playsCount" to newPlays,
                        "completed" to completed,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                gameRef.update(
                    mapOf(
                        "stars" to newStars,
                        "bestScore" to newBest,
                        "playsCount" to newPlays,
                        "completed" to completed,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }

            // Recompute total stars from all gameProgress docs
            val allGames = userRef.collection("gameProgress").get().await()
            val totalStars = allGames.documents.sumOf { it.getLong("stars") ?: 0L }
            val currentUserSnap = userRef.get().await()
            val currentXp = currentUserSnap.getLong("xp") ?: 0L
            val newXp = currentXp + xpReward.toLong().coerceAtLeast(0L)
            val newLevel = (newXp / 100L) + 1L

            userRef.update(
                mapOf(
                    "xp" to newXp,
                    "level" to newLevel,
                    "totalStars" to totalStars,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            if (!unlockStickerId.isNullOrBlank() && !unlockStickerTitle.isNullOrBlank() && clampedStars >= 2L) {
                val stickerRef = userRef.collection("stickers").document(unlockStickerId)
                val stickerSnap = stickerRef.get().await()
                if (!stickerSnap.exists()) {
                    stickerRef.set(
                        mapOf(
                            "userId" to uid,
                            "stickerId" to unlockStickerId,
                            "title" to unlockStickerTitle.take(40),
                            "category" to gameId,
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
            }

            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, gameRef.path, auth)
            Result.failure(e)
        }
    }

    suspend fun resetAllProgress(): Result<Unit> {
        val uid = requireUserId()
        val userRef = db.collection("users").document(uid)
        return try {
            val games = userRef.collection("gameProgress").get().await()
            for (doc in games.documents) {
                doc.reference.delete().await()
            }
            val stickers = userRef.collection("stickers").get().await()
            for (doc in stickers.documents) {
                doc.reference.delete().await()
            }
            userRef.update(
                mapOf(
                    "xp" to 0L,
                    "level" to 1L,
                    "totalStars" to 0L,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, userRef.path, auth)
            Result.failure(e)
        }
    }

    private fun logFlowError(
        error: Throwable,
        operationType: OperationType,
        path: String,
        uid: String
    ) {
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) return
        val exceptionToLog = firestoreError ?: (error as? Exception)
        if (exceptionToLog != null && auth.currentUser?.uid == uid) {
            handleFirestoreError(exceptionToLog, operationType, path, auth)
        }
    }
}
