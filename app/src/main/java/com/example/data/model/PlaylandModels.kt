package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val learnerName: String = "Little Explorer",
    val avatarId: String = "star",
    val xp: Long = 0L,
    val level: Long = 1L,
    val totalStars: Long = 0L,
    val soundEnabled: Boolean = true,
    val dailyTimerEnabled: Boolean = false,
    val dailyLimitMinutes: Long = 30L,
    val parentPin: String = "1234",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class GameProgress(
    @DocumentId val id: String = "",
    val userId: String = "",
    val gameId: String = "",
    val stars: Long = 0L,
    val bestScore: Long = 0L,
    val playsCount: Long = 0L,
    val completed: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class StickerReward(
    @DocumentId val id: String = "",
    val userId: String = "",
    val stickerId: String = "",
    val title: String = "",
    val category: String = "milestone",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
