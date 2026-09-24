package com.example.model

enum class PlayerStatus {
    IDLE,
    THINKING,
    DRAWN,
    DECLARED,
    DROPPED,
    ELIMINATED
}

data class Player(
    val id: String,
    val name: String,
    val avatarRes: String = "avatar_default",
    val seatIndex: Int,
    val chips: Long = 50000L,
    val pointsScore: Int = 0,
    val isHuman: Boolean = false,
    val isHost: Boolean = false,
    val isReady: Boolean = true,
    val isBot: Boolean = true,
    val isSpeaking: Boolean = false,
    val isMicMuted: Boolean = false,
    val isVoiceConnected: Boolean = true,
    val isOnline: Boolean = true,
    val status: PlayerStatus = PlayerStatus.IDLE,
    val handCards: List<PlayingCard> = emptyList(),
    val melds: List<CardMeld> = emptyList(),
    val isWinner: Boolean = false,
    val rankTitle: String = "Card Shark"
)
