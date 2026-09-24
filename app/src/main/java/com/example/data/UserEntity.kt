package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey val id: String = "local_user",
    val username: String = "RoyalPlayer",
    val avatarRes: String = "avatar_gold",
    val chips: Long = 100000L,
    val level: Int = 18,
    val xp: Int = 3400,
    val rankTier: String = "Diamond II",
    val rankPoints: Int = 2450,
    val matchesPlayed: Int = 42,
    val matchesWon: Int = 28,
    val equippedCardBack: String = "card_skin_gold",
    val equippedFelt: String = "felt_emerald",
    val equippedFrame: String = "frame_crown",
    val lastRewardClaimTime: Long = 0L
)

@Entity(tableName = "match_history")
data class MatchEntity(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val modeName: String,
    val playerCount: Int,
    val pointsScore: Int,
    val chipsDelta: Long,
    val isWin: Boolean
)
