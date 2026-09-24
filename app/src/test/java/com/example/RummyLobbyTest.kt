package com.example

import com.example.model.Player
import org.junit.Assert.assertEquals
import org.junit.Test

class RummyLobbyTest {

    @Test
    fun `chunked logic splits players into rows of 4`() {
        val players = (1..8).map { i -> 
            Player(id = "p$i", name = "Player $i", seatIndex = i - 1) 
        }
        
        val chunked = players.chunked(4)
        
        assertEquals(2, chunked.size)
        assertEquals(4, chunked[0].size)
        assertEquals(4, chunked[1].size)
        assertEquals("Player 1", chunked[0][0].name)
        assertEquals("Player 5", chunked[1][0].name)
    }

    @Test
    fun `chunked logic handles less than 4 players in a row`() {
        val players = (1..6).map { i -> 
            Player(id = "p$i", name = "Player $i", seatIndex = i - 1) 
        }
        
        val chunked = players.chunked(4)
        
        assertEquals(2, chunked.size)
        assertEquals(4, chunked[0].size)
        assertEquals(2, chunked[1].size)
    }
}
