package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ads.AdMobBannerView
import com.example.ads.AdMobConfig
import com.example.ui.theme.CasinoGreenFelt
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkVelvet
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.NeonCyan
import com.example.ads.PostGameAdMobOverlay
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.viewmodel.AppScreen
import com.example.viewmodel.RummyViewModel

@Composable
fun HomeScreen(viewModel: RummyViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isDailyRewardClaimedToday by viewModel.isDailyRewardClaimedToday.collectAsState()
    val showPostGameAd by viewModel.showPostGameAd.collectAsState()
    val isGameWin by viewModel.isGameWin.collectAsState()
    val earnedChips by viewModel.earnedChips.collectAsState()
    val scrollState = rememberScrollState()

    var showJoinRoomDialog by remember { mutableStateOf(false) }
    var roomCodeInput by remember { mutableStateOf("") }
    var showEditNameDialog by remember { mutableStateOf(false) }
    var newNameInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkVelvet)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header: User Profile & Chips Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, Color(0x33FFD700))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(CasinoGreenFelt)
                            .border(2.dp, GoldPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userProfile.username.take(2).uppercase(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = userProfile.username,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            IconButton(
                                onClick = {
                                    newNameInput = userProfile.username
                                    showEditNameDialog = true
                                },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Name",
                                    tint = GoldPrimary.copy(alpha = 0.8f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Text(
                            text = "${userProfile.rankTier} • Lvl ${userProfile.level}",
                            fontSize = 12.sp,
                            color = NeonCyan
                        )
                    }
                }

                // Chips Counter Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.horizontalGradient(listOf(GoldSecondary, GoldPrimary)))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = "Chips",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${userProfile.chips / 1000}K",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        // Hero Banner Card with Generated Art
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.5.dp, GoldPrimary)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.img_rummy_banner_1785245495727),
                    contentDescription = "Rummy Club Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark Gradient Overlay for text contrast
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xDD000000))
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "RUMMY MASTER CLUB",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldPrimary
                    )
                    Text(
                        text = "2 to 8-Player Matches • Custom House Rules • Low Latency RTC",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }

        // Daily Free Reward Wheel Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !isDailyRewardClaimedToday) { viewModel.openDailyRewardModal() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CasinoGreenFelt),
            border = BorderStroke(1.dp, GoldPrimary)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CardGiftcard,
                        contentDescription = "Daily Reward",
                        tint = if (isDailyRewardClaimedToday) Color.Gray else GoldPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            text = if (isDailyRewardClaimedToday) "Bonus Claimed Today" else "Daily Wheel & Bonus Chips",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDailyRewardClaimedToday) Color.Gray else Color.White
                        )
                        Text(
                            text = if (isDailyRewardClaimedToday) "Come back tomorrow for more chips!" else "Claim your 15,000 Free Chips daily bonus",
                            fontSize = 11.sp,
                            color = if (isDailyRewardClaimedToday) Color.DarkGray else Color.LightGray
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDailyRewardClaimedToday) Color.Gray.copy(alpha = 0.5f) else GoldPrimary)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isDailyRewardClaimedToday) "CLAIMED" else "CLAIM",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDailyRewardClaimedToday) Color.White.copy(alpha = 0.7f) else Color.Black
                    )
                }
            }
        }

        // Core Game Launch Actions
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Quick Match Button
                Button(
                    onClick = {
                        viewModel.createLobby(isPrivate = false)
                        viewModel.startGame()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(58.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Quick Match",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text("QUICK MATCH", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color.Black)
                            Text("Instant 4P Match", fontSize = 10.sp, color = Color.DarkGray)
                        }
                    }
                }

                // Create Private Lobby (2 - 8 Players)
                Button(
                    onClick = { viewModel.createLobby(isPrivate = true) },
                    modifier = Modifier
                        .weight(1f)
                        .height(58.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CasinoGreenFelt)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = "Create Lobby",
                            tint = GoldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text("CREATE ROOM", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("2-8 Players Custom", fontSize = 10.sp, color = NeonCyan)
                        }
                    }
                }
            }

            // Join Existing Room Button
            OutlinedButton(
                onClick = { showJoinRoomDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, NeonCyan)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MeetingRoom,
                        contentDescription = "Join Room",
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Text("JOIN ROOM WITH CODE", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                }
            }
        }

        // Feature Navigation Grid
        Text(
            text = "EXPLORE FEATURES",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HomeNavCard(
                title = "Store",
                subtitle = "Skins & Decks",
                icon = Icons.Default.ShoppingBag,
                color = NeonCyan,
                modifier = Modifier.weight(1f)
            ) { viewModel.navigateTo(AppScreen.STORE) }

            HomeNavCard(
                title = "Rankings",
                subtitle = "Leaderboard",
                icon = Icons.Default.Leaderboard,
                color = GoldPrimary,
                modifier = Modifier.weight(1f)
            ) { viewModel.navigateTo(AppScreen.LEADERBOARD) }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HomeNavCard(
                title = "Statistics",
                subtitle = "Win Rates & History",
                icon = Icons.Default.TrendingUp,
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            ) { viewModel.navigateTo(AppScreen.STATS_DASHBOARD) }

            HomeNavCard(
                title = "Social Hub",
                subtitle = "Friends & Chat",
                icon = Icons.Default.Share,
                color = Color(0xFFFF9800),
                modifier = Modifier.weight(1f)
            ) { viewModel.navigateTo(AppScreen.SOCIAL_HUB) }
        }

        // Active AdMob Banner View
        AdMobBannerView()
    }

    if (showPostGameAd) {
        PostGameAdMobOverlay(
            isWin = isGameWin,
            chipsEarned = earnedChips,
            onDismiss = { viewModel.dismissPostGameAd() }
        )
    }

    if (showJoinRoomDialog) {
        AlertDialog(
            onDismissRequest = { showJoinRoomDialog = false },
            containerColor = DarkSurface,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MeetingRoom,
                        contentDescription = "Join Room",
                        tint = NeonCyan
                    )
                    Text(
                        text = "JOIN RUMMY ROOM",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Enter 6-character room code to join an active multiplayer lobby.",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                    OutlinedTextField(
                        value = roomCodeInput,
                        onValueChange = { roomCodeInput = it.uppercase().take(10) },
                        label = { Text("Room Code (e.g. RM-8921)", color = Color.Gray) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = NeonCyan,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (roomCodeInput.isNotBlank()) {
                            viewModel.joinRoom(roomCodeInput)
                            showJoinRoomDialog = false
                            roomCodeInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("JOIN ROOM", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showJoinRoomDialog = false }) {
                    Text("CANCEL", color = Color.White)
                }
            }
        )
    }

    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            containerColor = DarkSurface,
            title = {
                Text(
                    text = "EDIT PROFILE NAME",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Enter a new username for your profile. This name will be visible to other players.",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                    OutlinedTextField(
                        value = newNameInput,
                        onValueChange = { newNameInput = it.take(20) },
                        label = { Text("New Username", color = Color.Gray) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = NeonCyan,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newNameInput.isNotBlank()) {
                            viewModel.updateUsername(newNameInput)
                            showEditNameDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("SAVE", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEditNameDialog = false }) {
                    Text("CANCEL", color = Color.White)
                }
            }
        )
    }
}

@Composable
fun HomeNavCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(84.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, Color(0x22FFFFFF))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
