package com.example.engine

import com.example.model.CardMeld
import com.example.model.MeldType
import com.example.model.PlayingCard
import com.example.model.Rank
import com.example.model.Suit

object RummyEvaluator {

    /**
     * Checks if a group of cards forms a Pure Sequence (consecutive ranks, same suit, no jokers used as wildcards)
     */
    fun isPureSequence(cards: List<PlayingCard>): Boolean {
        if (cards.size < 3) return false
        val suit = cards.first().suit
        if (cards.any { it.suit != suit || it.isPrintedJoker || it.isWildJoker }) return false

        val sortedRanks = cards.map { it.rank.value }.sorted()
        
        // Normal consecutive check
        var isConsecutive = true
        for (i in 0 until sortedRanks.size - 1) {
            if (sortedRanks[i + 1] != sortedRanks[i] + 1) {
                isConsecutive = false
                break
            }
        }
        if (isConsecutive) return true

        // Ace low/high wrap check (e.g. Q, K, A or A, 2, 3)
        // A, 2, 3 -> Ace is 1, so already checked by normal consecutive.
        // Q, K, A -> Ace value 1 converted to 14
        if (sortedRanks.contains(1) && sortedRanks.contains(12) && sortedRanks.contains(13)) {
            val aceHighRanks = sortedRanks.map { if (it == 1) 14 else it }.sorted()
            for (i in 0 until aceHighRanks.size - 1) {
                if (aceHighRanks[i + 1] != aceHighRanks[i] + 1) return false
            }
            return true
        }

        return false
    }

    /**
     * Checks if cards form an Impure Sequence (consecutive ranks with Jokers permitted)
     */
    fun isImpureSequence(cards: List<PlayingCard>): Boolean {
        if (cards.size < 3) return false
        val jokersCount = cards.count { it.isPrintedJoker || it.isWildJoker }
        val naturalCards = cards.filter { !it.isPrintedJoker && !it.isWildJoker }
        
        if (naturalCards.isEmpty()) return true // All jokers sequence
        
        val suit = naturalCards.first().suit
        if (naturalCards.any { it.suit != suit }) return false

        val sortedRanks = naturalCards.map { it.rank.value }.sorted()
        
        // Calculate gaps between natural ranks
        var gapsNeeded = 0
        for (i in 0 until sortedRanks.size - 1) {
            val diff = sortedRanks[i + 1] - sortedRanks[i]
            if (diff == 0) return false // Duplicate rank in sequence
            gapsNeeded += (diff - 1)
        }

        return gapsNeeded <= jokersCount
    }

    /**
     * Checks if cards form a valid Set (3 or 4 cards of same rank, different suits; Jokers allowed)
     */
    fun isValidSet(cards: List<PlayingCard>): Boolean {
        if (cards.size < 3 || cards.size > 4) return false
        val naturalCards = cards.filter { !it.isPrintedJoker && !it.isWildJoker }
        if (naturalCards.isEmpty()) return true

        val targetRank = naturalCards.first().rank
        if (naturalCards.any { it.rank != targetRank }) return false

        // Suits must be distinct among natural cards
        val suits = naturalCards.map { it.suit }
        return suits.distinct().size == suits.size
    }

    /**
     * Evaluates a single meld group
     */
    fun evaluateMeld(cards: List<PlayingCard>): CardMeld {
        if (cards.size < 3) {
            val pts = cards.sumOf { if (it.isPrintedJoker || it.isWildJoker) 0 else it.rank.points }
            return CardMeld(cards = cards, type = MeldType.INVALID, deadwoodPoints = pts)
        }

        return when {
            isPureSequence(cards) -> CardMeld(cards = cards, type = MeldType.PURE_SEQUENCE, deadwoodPoints = 0)
            isImpureSequence(cards) -> CardMeld(cards = cards, type = MeldType.IMPURE_SEQUENCE, deadwoodPoints = 0)
            isValidSet(cards) -> CardMeld(cards = cards, type = MeldType.SET, deadwoodPoints = 0)
            else -> {
                val pts = cards.sumOf { if (it.isPrintedJoker || it.isWildJoker) 0 else it.rank.points }
                CardMeld(cards = cards, type = MeldType.INVALID, deadwoodPoints = pts)
            }
        }
    }

    /**
     * Validates an entire full hand declaration
     * Requirements:
     * - At least 1 Pure Sequence
     * - At least 2 Sequences total (1 pure + 1 pure/impure)
     * - All remaining cards in valid melds
     */
    fun validateDeclaration(melds: List<CardMeld>): Pair<Boolean, String> {
        val pureCount = melds.count { it.type == MeldType.PURE_SEQUENCE }
        val seqCount = melds.count { it.type == MeldType.PURE_SEQUENCE || it.type == MeldType.IMPURE_SEQUENCE }
        val invalidMelds = melds.filter { !it.type.isValid }

        return when {
            pureCount < 1 -> Pair(false, "Declaration Invalid: Requires at least 1 Pure Sequence (no Jokers)!")
            seqCount < 2 -> Pair(false, "Declaration Invalid: Requires at least 2 Sequences (Pure + Second Sequence)!")
            invalidMelds.isNotEmpty() -> Pair(false, "Declaration Invalid: Contains ${invalidMelds.size} invalid meld group(s).")
            else -> Pair(true, "Valid Rummy Declaration! Pure Sequence verified.")
        }
    }

    /**
     * Auto-sorts cards by Suit or by Rank for easy gameplay
     */
    fun autoSortBySuit(cards: List<PlayingCard>): List<PlayingCard> {
        return cards.sortedWith(compareBy({ it.suit.ordinal }, { it.rank.value }))
    }

    fun autoSortByRank(cards: List<PlayingCard>): List<PlayingCard> {
        return cards.sortedWith(compareBy({ it.rank.value }, { it.suit.ordinal }))
    }
}
