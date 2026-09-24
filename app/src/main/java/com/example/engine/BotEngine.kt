package com.example.engine

import com.example.model.CardMeld
import com.example.model.MeldType
import com.example.model.PlayingCard
import com.example.model.Player
import kotlin.random.Random

object BotEngine {

    /**
     * AI Bot logic for drawing card
     * Decides whether to draw from Open Discard Pile or Closed Stock Deck
     */
    fun shouldDrawFromDiscard(botHand: List<PlayingCard>, topDiscard: PlayingCard?): Boolean {
        if (topDiscard == null) return false
        if (topDiscard.isPrintedJoker || topDiscard.isWildJoker) return true

        // Check if topDiscard completes or advances a pair/sequence in bot hand
        val matchCount = botHand.count { 
            it.suit == topDiscard.suit || it.rank == topDiscard.rank || (it.suit == topDiscard.suit && Math.abs(it.rank.value - topDiscard.rank.value) == 1)
        }
        return matchCount >= 2
    }

    /**
     * AI Bot logic for discarding a card
     * Discards highest deadwood card not in a pure or impure sequence
     */
    fun chooseCardToDiscard(botHand: List<PlayingCard>): PlayingCard {
        if (botHand.isEmpty()) throw IllegalStateException("Hand cannot be empty")
        
        // Exclude Jokers
        val nonJokers = botHand.filter { !it.isPrintedJoker && !it.isWildJoker }
        if (nonJokers.isEmpty()) return botHand.random()

        // Group non-jokers into melds and pick highest point unmelded card
        val sortedByPoints = nonJokers.sortedByDescending { it.rank.points }
        return sortedByPoints.firstOrNull() ?: botHand.random()
    }

    /**
     * AI Bot checks if hand can declare
     */
    fun canBotDeclare(botHand: List<PlayingCard>): List<CardMeld>? {
        // Group bot hand into 3-4 card melds
        val chunks = botHand.chunked(3)
        val melds = chunks.map { RummyEvaluator.evaluateMeld(it) }
        val (isValid, _) = RummyEvaluator.validateDeclaration(melds)
        return if (isValid) melds else null
    }
}
