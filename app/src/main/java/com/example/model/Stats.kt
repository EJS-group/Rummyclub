package com.example.model

data class UserStats(
    val matchesPlayed: Int = 42,
    val matchesWon: Int = 28,
    val totalChipsWon: Long = 185000L,
    val pureSequenceRatePct: Int = 88,
    val lowestPointsAvg: Float = 14.2f,
    val currentRankTier: String = "Diamond II",
    val rankPoints: Int = 2450,
    val level: Int = 18,
    val xpCurrent: Int = 3400,
    val xpNextLevel: Int = 5000,
    val totalDeclarations: Int = 31,
    val totalDrops: Int = 6
)

data class MatchHistoryEntry(
    val id: String,
    val dateString: String,
    val modeName: String,
    val playerCount: Int,
    val rankChange: String,
    val chipsDelta: Long,
    val isWin: Boolean
)
