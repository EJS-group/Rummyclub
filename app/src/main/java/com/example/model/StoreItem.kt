package com.example.model

enum class CosmeticType {
    CARD_BACK,
    TABLE_FELT,
    AVATAR_FRAME,
    VICTORY_EFFECT,
    CHIP_BUNDLE
}

data class StoreItem(
    val id: String,
    val title: String,
    val description: String,
    val priceChips: Long,
    val type: CosmeticType,
    val iconName: String,
    val primaryColorHex: String,
    val isOwned: Boolean = false,
    val isEquipped: Boolean = false
)
