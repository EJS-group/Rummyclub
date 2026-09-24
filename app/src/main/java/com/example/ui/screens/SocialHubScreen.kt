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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Friend
import com.example.model.NotificationItem
import com.example.ui.theme.CasinoGreenFelt
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkVelvet
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonCyan
import com.example.viewmodel.AppScreen
import com.example.viewmodel.RummyViewModel

@Composable
fun SocialHubScreen(viewModel: RummyViewModel) {
    val friends by viewModel.friends.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    val tabs = listOf("Friends (${friends.size})", "Push Notifications", "Share Profile")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkVelvet)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { viewModel.navigateTo(AppScreen.HOME) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "SOCIAL HUB & CHAT",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldPrimary
                )
            }

            IconButton(onClick = { viewModel.showToast("Searching for players...") }) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add Friend", tint = GoldPrimary)
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurface,
            contentColor = GoldPrimary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) GoldPrimary else Color.Gray
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> FriendsListSection(friends = friends, onInvite = { friend -> viewModel.showToast("Match invite sent to ${friend.name}!") })
            1 -> NotificationsSection(notifications = notifications)
            2 -> ShareProfileSection(onShare = { platform -> viewModel.showToast("Shared profile card to $platform!") })
        }
    }
}

@Composable
fun FriendsListSection(friends: List<Friend>, onInvite: (Friend) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(friends) { friend ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, Color(0x11FFFFFF))
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
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(CasinoGreenFelt)
                                .border(1.dp, if (friend.isOnline) Color(0xFF4CAF50) else Color.Gray, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(friend.name.take(2).uppercase(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                        }

                        Column {
                            Text(friend.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(friend.status, fontSize = 11.sp, color = if (friend.isOnline) NeonCyan else Color.Gray)
                        }
                    }

                    if (friend.isOnline) {
                        Button(
                            onClick = { onInvite(friend) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CasinoGreenFelt)
                        ) {
                            Text("INVITE", fontSize = 11.sp, color = GoldPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationsSection(notifications: List<NotificationItem>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(notifications) { notice ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, Color(0x22FFFFFF))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(CasinoGreenFelt),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notice", tint = GoldPrimary, modifier = Modifier.size(20.dp))
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(notice.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(notice.timeAgo, fontSize = 10.sp, color = Color.Gray)
                        }
                        Text(notice.message, fontSize = 11.sp, color = Color.LightGray)
                    }
                }
            }
        }
    }
}

@Composable
fun ShareProfileSection(onShare: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CasinoGreenFelt),
            border = BorderStroke(2.dp, GoldPrimary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("RUMMY CLUB CARD SHARK", fontSize = 14.sp, fontWeight = FontWeight.Black, color = GoldPrimary)
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = GoldPrimary)
                }

                Text("Join my private 2 to 8 player table with code #RUMMY8 and claim 15,000 free chips!", fontSize = 13.sp, color = Color.White)

                Text("REFERRED BY: @RoyalPlayer", fontSize = 11.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
            }
        }

        Text("SHARE CARD TO SOCIAL MEDIA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("WhatsApp", "Twitter / X", "Facebook", "Instagram").forEach { platform ->
                Button(
                    onClick = { onShare(platform) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurface)
                ) {
                    Text(platform, fontSize = 10.sp, color = GoldPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
