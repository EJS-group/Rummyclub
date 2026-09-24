package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameMode
import com.example.model.Player
import com.example.ui.components.PlayerSeatView
import com.example.ui.theme.CasinoGreenFelt
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkVelvet
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonCyan
import com.example.viewmodel.AppScreen
import com.example.viewmodel.RummyViewModel

@Composable
fun LobbyScreen(viewModel: RummyViewModel) {
    val houseRules by viewModel.houseRules.collectAsState()
    val players by viewModel.players.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkVelvet)
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { viewModel.navigateTo(AppScreen.HOME) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "MATCH LOBBY",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = GoldPrimary
            )

            // Room Invite Code Pill
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, GoldPrimary)
            ) {
                Row(
                    modifier = Modifier
                        .clickable { viewModel.showToast("Room Code ${houseRules.roomCode} copied!") }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "CODE: ${houseRules.roomCode}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NeonCyan
                    )
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Code",
                        tint = NeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // Seats Header: 2 to 8 players!
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CasinoGreenFelt)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TABLE SEATS (${players.size} / ${houseRules.maxPlayers} PLAYERS)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )

                    // 2 to 8 Player Selector Pills
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.width(180.dp)
                    ) {
                        items((2..8).toList()) { count ->
                            val isSelected = houseRules.maxPlayers == count
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) GoldPrimary else DarkSurface)
                                    .clickable {
                                        viewModel.setPlayerCount(count)
                                        viewModel.createLobby(houseRules.isPrivateRoom)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${count}P",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }

                // Flexible Grid of Seats (2 to 8 seats)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    players.chunked(4).forEach { rowPlayers ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            rowPlayers.forEach { player ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { viewModel.togglePlayerOnlineStatus(player.id) }
                                ) {
                                    PlayerSeatView(
                                        player = player,
                                        isCurrentTurn = false,
                                        turnSecondsLeft = houseRules.turnTimeSeconds,
                                        isSimpleMode = true
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // House Rules Customizer Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, Color(0x33FFFFFF))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "House Rules",
                        tint = GoldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "CUSTOM HOUSE RULES",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Game Mode Selector
                Text("Select Game Mode", fontSize = 12.sp, color = Color.Gray)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    GameMode.values().forEach { mode ->
                        val isSelected = houseRules.gameMode == mode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) CasinoGreenFelt else DarkVelvet)
                                .border(1.dp, if (isSelected) GoldPrimary else Color.Transparent, RoundedCornerShape(10.dp))
                                .clickable { viewModel.setGameMode(mode) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mode.displayName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) GoldPrimary else Color.LightGray
                            )
                        }
                    }
                }

                // Turn Timer Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Turn Timer", fontSize = 12.sp, color = Color.White)
                        Text("${houseRules.turnTimeSeconds}s per turn", fontSize = 12.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = houseRules.turnTimeSeconds.toFloat(),
                        onValueChange = { viewModel.updateHouseRules(houseRules.copy(turnTimeSeconds = it.toInt())) },
                        valueRange = 15f..60f,
                        steps = 2,
                        colors = SliderDefaults.colors(thumbColor = GoldPrimary, activeTrackColor = GoldPrimary)
                    )
                }

                // Toggles for Jokers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Allow Printed Jokers in Deck", fontSize = 12.sp, color = Color.White)
                    Switch(
                        checked = houseRules.allowPrintedJokers,
                        onCheckedChange = { viewModel.updateHouseRules(houseRules.copy(allowPrintedJokers = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldPrimary, checkedTrackColor = CasinoGreenFelt)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Wild Joker Card Enabled", fontSize = 12.sp, color = Color.White)
                    Switch(
                        checked = houseRules.allowWildJokers,
                        onCheckedChange = { viewModel.updateHouseRules(houseRules.copy(allowWildJokers = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldPrimary, checkedTrackColor = CasinoGreenFelt)
                    )
                }
            }
        }

        // Lobby Readiness & Sync Status Indicator
        val onlinePlayersCount = players.count { it.isOnline }
        val offlinePlayersCount = players.size - onlinePlayersCount
        val allPlayersReady = onlinePlayersCount == players.size && players.size >= 2

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (allPlayersReady) Color(0xFF1B3828) else Color(0xFF38241B)
            ),
            border = BorderStroke(1.dp, if (allPlayersReady) Color(0xFF4CAF50) else GoldPrimary)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (allPlayersReady) "🟢" else "⚠️",
                        fontSize = 16.sp
                    )
                    Column {
                        Text(
                            text = if (allPlayersReady) "ALL USERS CONNECTED & READY" else "LOBBY WAITING FOR PLAYERS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (allPlayersReady) Color(0xFF81C784) else GoldPrimary
                        )
                        Text(
                            text = if (allPlayersReady)
                                "Real-time sync active • ${players.size}/${houseRules.maxPlayers} players online"
                            else
                                "${onlinePlayersCount} online, ${offlinePlayersCount} offline (Tap seat to test online/offline state)",
                            fontSize = 10.sp,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }

        // Start Match Button
        Button(
            onClick = { viewModel.startGame() },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start Game",
                    tint = Color.Black
                )
                Text(
                    text = if (allPlayersReady) "START RUMMY MATCH" else "START MATCH (${players.size}/${houseRules.maxPlayers} PLAYERS)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
        }
    }
}
