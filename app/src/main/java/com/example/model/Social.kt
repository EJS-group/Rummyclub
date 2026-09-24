package com.example.model

data class Friend(
    val id: String,
    val name: String,
    val avatar: String,
    val status: String, // "Online - In Lobby", "In Game (4/6)", "Offline"
    val rank: String,
    val chips: Long,
    val isOnline: Boolean
)

data class ChatMessage(
    val id: String,
    val senderName: String,
    val senderAvatar: String,
    val text: String,
    val timestamp: String,
    val isSystem: Boolean = false,
    val isVoiceClip: Boolean = false
)

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timeAgo: String,
    val isRead: Boolean = false,
    val actionType: String = "LOBBY_INVITE"
)

data class LeaderboardUser(
    val rankPosition: Int,
    val name: String,
    val avatar: String,
    val rankPoints: Int,
    val tierName: String,
    val winRatePct: Int,
    val isCurrentUser: Boolean = false
)
