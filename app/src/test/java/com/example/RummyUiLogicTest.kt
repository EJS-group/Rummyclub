package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.model.Player
import com.example.ui.components.PlayerSeatView
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RummyUiLogicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `PlayerSeatView in Simple Mode hides chips and status`() {
        val player = Player(
            id = "p1",
            name = "TestPlayer",
            seatIndex = 0,
            chips = 88000L,
            handCards = listOf()
        )

        composeTestRule.setContent {
            PlayerSeatView(
                player = player,
                isCurrentTurn = false,
                turnSecondsLeft = 30,
                isSimpleMode = true
            )
        }

        // Name should be visible
        composeTestRule.onNodeWithText("TestPlayer").assertIsDisplayed()
        
        // Chips (e.g. "88k") and Status (e.g. "0 cards") should NOT be visible
        composeTestRule.onNodeWithText("88k").assertDoesNotExist()
        composeTestRule.onNodeWithText("0 cards").assertDoesNotExist()
    }

    @Test
    fun `PlayerSeatView in Detailed Mode shows chips and status`() {
        val player = Player(
            id = "p1",
            name = "TestPlayer",
            seatIndex = 0,
            chips = 88000L
        )

        composeTestRule.setContent {
            PlayerSeatView(
                player = player,
                isCurrentTurn = false,
                turnSecondsLeft = 30,
                isSimpleMode = false
            )
        }

        // Both name and chips should be visible
        composeTestRule.onNodeWithText("TestPlayer").assertIsDisplayed()
        composeTestRule.onNodeWithText("88k").assertIsDisplayed()
    }
}
