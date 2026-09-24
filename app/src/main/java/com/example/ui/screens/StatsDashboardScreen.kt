package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.model.MatchHistoryEntry
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkVelvet
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonCyan
import com.example.viewmodel.AppScreen
import com.example.viewmodel.RummyViewModel

@Composable
fun StatsDashboardScreen(viewModel: RummyViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    val matchHistory by viewModel.matchHistoryList.collectAsState()

    val winRate = if (userProfile.matchesPlayed > 0) {
        (userProfile.matchesWon.toFloat() / userProfile.matchesPlayed * 100).toInt()
    } else 0

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
                text = "PLAYER DASHBOARD & STATS",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = GoldPrimary
            )
        }

        // Stats Summary Cards Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatMetricBox(
                title = "WIN RATE",
                value = "$winRate%",
                sub = "${userProfile.matchesWon} / ${userProfile.matchesPlayed} Matches",
                color = GoldPrimary,
                modifier = Modifier.weight(1f)
            )

            StatMetricBox(
                title = "PURE SEQ %",
                value = "88%",
                sub = "Meld Rate",
                color = NeonCyan,
                modifier = Modifier.weight(1f)
            )

            StatMetricBox(
                title = "MMR RANK",
                value = "${userProfile.rankPoints}",
                sub = userProfile.rankTier,
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
        }

        // Rank Progression Bar Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, Color(0x22FFFFFF))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("XP PROGRESS (LEVEL ${userProfile.level})", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("${userProfile.xp} / 5000 XP", fontSize = 12.sp, color = GoldPrimary, fontWeight = FontWeight.Bold)
                }

                LinearProgressIndicator(
                    progress = { (userProfile.xp.toFloat() / 5000f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = GoldPrimary,
                    trackColor = DarkVelvet,
                )
            }
        }

        // Match History Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(Icons.Default.History, contentDescription = "History", tint = GoldPrimary, modifier = Modifier.size(18.dp))
            Text(
                text = "MATCH HISTORY & CHIP RESULTS",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(matchHistory) { entry ->
                MatchHistoryRow(entry = entry)
            }
        }
    }
}

@Composable
fun StatMetricBox(
    title: String,
    value: String,
    sub: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(90.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 20.sp, color = color, fontWeight = FontWeight.Black)
            Text(sub, fontSize = 9.sp, color = Color.LightGray)
        }
    }
}

@Composable
fun MatchHistoryRow(entry: MatchHistoryEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
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
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (entry.isWin) Color(0x334CAF50) else Color(0x33D32F2F)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (entry.isWin) "WIN" else "LOSS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (entry.isWin) Color(0xFF4CAF50) else Color.Red
                    )
                }

                Column {
                    Text(
                        text = "${entry.modeName} (${entry.playerCount} Players)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = entry.dateString,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (entry.isWin) "+${entry.chipsDelta / 1000}K Chips" else "-10K Chips",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (entry.isWin) GoldPrimary else Color.Red
                )
                Text(
                    text = entry.rankChange,
                    fontSize = 10.sp,
                    color = NeonCyan
                )
            }
        }
    }
}
