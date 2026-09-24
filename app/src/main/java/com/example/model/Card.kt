package com.example.model

import java.util.UUID

enum class Suit(val symbol: String, val isRed: Boolean, val displayName: String) {
    CLUBS("♣", false, "Clubs"),
    DIAMONDS("♦", true, "Diamonds"),
    HEARTS("♥", true, "Hearts"),
    SPADES("♠", false, "Spades")
}

enum class Rank(val value: Int, val code: String, val points: Int) {
    ACE(1, "A", 10),
    TWO(2, "2", 2),
    THREE(3, "3", 3),
    FOUR(4, "4", 4),
    FIVE(5, "5", 5),
    SIX(6, "6", 6),
    SEVEN(7, "7", 7),
    EIGHT(8, "8", 8),
    NINE(9, "9", 9),
    TEN(10, "10", 10),
    JACK(11, "J", 10),
    QUEEN(12, "Q", 10),
    KING(13, "K", 10)
}

data class PlayingCard(
    val id: String = UUID.randomUUID().toString(),
    val suit: Suit,
    val rank: Rank,
    val isWildJoker: Boolean = false,
    val isPrintedJoker: Boolean = false,
    val isSelected: Boolean = false
) {
    val displayValue: String
        get() = if (isPrintedJoker) "★ JOKER" else "${rank.code}${suit.symbol}"
}

enum class MeldType(val label: String, val isValid: Boolean) {
    PURE_SEQUENCE("Pure Sequence", true),
    IMPURE_SEQUENCE("Impure Sequence", true),
    SET("Valid Set", true),
    INVALID("Invalid Meld", false)
}

data class CardMeld(
    val id: String = UUID.randomUUID().toString(),
    val cards: List<PlayingCard>,
    val type: MeldType = MeldType.INVALID,
    val deadwoodPoints: Int = 0
)
