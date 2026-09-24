package com.example

import com.example.engine.RummyEvaluator
import com.example.model.MeldType
import com.example.model.PlayingCard
import com.example.model.Rank
import com.example.model.Suit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RummyEvaluatorTest {

    @Test
    fun `isPureSequence identifies valid sequence`() {
        val cards = listOf(
            PlayingCard(suit = Suit.CLUBS, rank = Rank.TWO),
            PlayingCard(suit = Suit.CLUBS, rank = Rank.THREE),
            PlayingCard(suit = Suit.CLUBS, rank = Rank.FOUR)
        )
        assertTrue(RummyEvaluator.isPureSequence(cards))
    }

    @Test
    fun `isPureSequence identifies valid ace high sequence`() {
        val cards = listOf(
            PlayingCard(suit = Suit.HEARTS, rank = Rank.QUEEN),
            PlayingCard(suit = Suit.HEARTS, rank = Rank.KING),
            PlayingCard(suit = Suit.HEARTS, rank = Rank.ACE)
        )
        assertTrue(RummyEvaluator.isPureSequence(cards))
    }

    @Test
    fun `isPureSequence rejects sequence with wild joker`() {
        val cards = listOf(
            PlayingCard(suit = Suit.SPADES, rank = Rank.FIVE),
            PlayingCard(suit = Suit.SPADES, rank = Rank.SIX, isWildJoker = true),
            PlayingCard(suit = Suit.SPADES, rank = Rank.SEVEN)
        )
        assertFalse(RummyEvaluator.isPureSequence(cards))
    }

    @Test
    fun `isImpureSequence identifies valid sequence with joker`() {
        val cards = listOf(
            PlayingCard(suit = Suit.DIAMONDS, rank = Rank.EIGHT),
            PlayingCard(suit = Suit.DIAMONDS, rank = Rank.NINE, isWildJoker = true),
            PlayingCard(suit = Suit.DIAMONDS, rank = Rank.TEN)
        )
        assertTrue(RummyEvaluator.isImpureSequence(cards))
    }

    @Test
    fun `isValidSet identifies valid set`() {
        val cards = listOf(
            PlayingCard(suit = Suit.CLUBS, rank = Rank.JACK),
            PlayingCard(suit = Suit.DIAMONDS, rank = Rank.JACK),
            PlayingCard(suit = Suit.HEARTS, rank = Rank.JACK)
        )
        assertTrue(RummyEvaluator.isValidSet(cards))
    }

    @Test
    fun `validateDeclaration requires pure sequence`() {
        val melds = listOf(
            RummyEvaluator.evaluateMeld(listOf(
                PlayingCard(suit = Suit.CLUBS, rank = Rank.TWO),
                PlayingCard(suit = Suit.CLUBS, rank = Rank.THREE, isWildJoker = true),
                PlayingCard(suit = Suit.CLUBS, rank = Rank.FOUR)
            )), // Impure
            RummyEvaluator.evaluateMeld(listOf(
                PlayingCard(suit = Suit.HEARTS, rank = Rank.FIVE),
                PlayingCard(suit = Suit.HEARTS, rank = Rank.SIX),
                PlayingCard(suit = Suit.HEARTS, rank = Rank.SEVEN)
            )) // Pure
        )
        // This should be valid because there's 1 pure and 2 sequences total
        assertTrue(RummyEvaluator.validateDeclaration(melds).first)
    }

    @Test
    fun `validateDeclaration fails without pure sequence`() {
        val melds = listOf(
            RummyEvaluator.evaluateMeld(listOf(
                PlayingCard(suit = Suit.CLUBS, rank = Rank.TWO),
                PlayingCard(suit = Suit.CLUBS, rank = Rank.THREE, isWildJoker = true),
                PlayingCard(suit = Suit.CLUBS, rank = Rank.FOUR)
            )),
            RummyEvaluator.evaluateMeld(listOf(
                PlayingCard(suit = Suit.HEARTS, rank = Rank.FIVE),
                PlayingCard(suit = Suit.HEARTS, rank = Rank.SIX, isWildJoker = true),
                PlayingCard(suit = Suit.HEARTS, rank = Rank.SEVEN)
            ))
        )
        assertFalse(RummyEvaluator.validateDeclaration(melds).first)
    }
}
