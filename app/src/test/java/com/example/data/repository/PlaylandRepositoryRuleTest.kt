package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaylandRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun ensureProfileAndRecordGameCompletion_persistsProfileGamesAndSticker() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = PlaylandRepository(firestore, auth)

        val initResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.ensureUserProfileInitialized("Mia")
        }
        assertTrue(initResult.isSuccess)

        val recordResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.recordGameCompletion(
                gameId = "phonics",
                earnedStars = 3,
                score = 120,
                xpReward = 60,
                unlockStickerId = "phonics_star",
                unlockStickerTitle = "ABC Cloud Champ"
            )
        }
        assertTrue(recordResult.isSuccess)

        val profile = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeUserProfile(uid).first { it != null && it.totalStars >= 3L }
        }
        assertNotNull(profile)
        assertEquals(3L, profile!!.totalStars)
        assertTrue(profile.xp >= 60L)

        val games = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeGameProgress(uid).first { list -> list.any { it.gameId == "phonics" } }
        }
        assertEquals(3L, games.first { it.gameId == "phonics" }.stars)

        val stickers = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeStickers(uid).first { list -> list.any { it.stickerId == "phonics_star" } }
        }
        assertEquals("ABC Cloud Champ", stickers.first { it.stickerId == "phonics_star" }.title)
    }

    @Test
    fun updateParentSettings_validInput_succeeds() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = PlaylandRepository(firestore, auth)
        withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.ensureUserProfileInitialized("Leo").getOrThrow()
            repository.updateParentSettings(
                soundEnabled = false,
                dailyTimerEnabled = true,
                dailyLimitMinutes = 45,
                parentPin = "5678"
            ).getOrThrow()
        }

        val profile = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeUserProfile(uid).first { it != null && it.parentPin == "5678" }
        }
        assertEquals(false, profile!!.soundEnabled)
        assertEquals(true, profile.dailyTimerEnabled)
        assertEquals(45L, profile.dailyLimitMinutes)
    }

    @Test
    fun observeUserProfile_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = PlaylandRepository(firestore, auth)
        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.ensureUserProfileInitialized("Alice").getOrThrow()
        }

        signInTestUser(BOB_EMAIL)
        val bobRepo = PlaylandRepository(firestore, auth)
        val exception = runCatching {
            withTimeout(FLOW_TIMEOUT_MS) {
                bobRepo.observeUserProfile(aliceUid).first()
            }
        }.exceptionOrNull()

        assertNotNull(exception)
        val firestoreException = checkNotNull(exception!!.unwrapFirestoreException()) {
            "Expected nested FirebaseFirestoreException, got $exception"
        }
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
    }

    @Test
    fun observeGameProgress_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        auth.signOut()
        val repository = PlaylandRepository(firestore, auth)

        val exception = runCatching {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeGameProgress(uid).first()
            }
        }.exceptionOrNull()

        assertNotNull(exception)
        val firestoreException = checkNotNull(exception!!.unwrapFirestoreException()) {
            "Expected nested FirebaseFirestoreException, got $exception"
        }
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
    }

    private companion object {
        const val ALICE_EMAIL = "alice_playland@test.com"
        const val BOB_EMAIL = "bob_playland@test.com"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
