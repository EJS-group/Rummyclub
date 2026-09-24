package com.example.model

enum class GameMode(val displayName: String, val description: String) {
    POINTS_RUMMY("Points Rummy", "Points have cash/chip value. Fast & thrilling."),
    POOL_101("101 Pool", "Eliminated if cumulative score reaches 101."),
    POOL_201("201 Pool", "Eliminated if cumulative score reaches 201."),
    BEST_OF_3("Deals Rummy", "3 Deals played. Lowest score wins total pot.")
}

data class HouseRules(
    val maxPlayers: Int = 4, // Supported 2 to 8 players!
    val gameMode: GameMode = GameMode.POINTS_RUMMY,
    val cardsPerPlayer: Int = 13, // 10, 13, 14, 21
    val allowPrintedJokers: Boolean = true,
    val allowWildJokers: Boolean = true,
    val turnTimeSeconds: Int = 30,
    val entryFeeChips: Long = 1000L,
    val pointValueMultiplier: Int = 10,
    val dropPenaltyFirst: Int = 20,
    val dropPenaltyMiddle: Int = 40,
    val fullCountPenalty: Int = 80,
    val isPrivateRoom: Boolean = false,
    val roomCode: String = "RUMMY8"
)
