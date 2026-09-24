package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Player
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonCyan
import android.content.res.Configuration

@Composable
fun PlayerSeatView(
    player: Player,
    isCurrentTurn: Boolean,
    turnSecondsLeft: Int,
    modifier: Modifier = Modifier,
    isSimpleMode: Boolean = false
) {
    if (isSimpleMode) {
        SimplePlayerSeat(player, isCurrentTurn, turnSecondsLeft, modifier)
    } else {
        DetailedPlayerSeat(player, isCurrentTurn, turnSecondsLeft, modifier)
    }
}

@Composable
private fun SimplePlayerSeat(
    player: Player,
    isCurrentTurn: Boolean,
    turnSecondsLeft: Int,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isCurrentTurn) GoldPrimary else Color(0x66FFFFFF)

    Column(
        modifier = modifier.width(70.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Circular Avatar
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(Color(0xFF1A1A1A))
                .border(2.dp, borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = player.name.take(2).uppercase(),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GoldPrimary
            )

            // Voice Active Badge
            if (player.isVoiceConnected) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(NeonCyan)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Active",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(10.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }

        // Name / Status Pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                    when {
                        !player.isOnline -> Color.Red
                        isCurrentTurn -> GoldPrimary
                        else -> Color.Black.copy(alpha = 0.7f)
                    }
                )
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = when {
                    !player.isOnline -> "OFFLINE"
                    isCurrentTurn -> "${turnSecondsLeft}s"
                    player.isHuman -> "YOU"
                    else -> player.name
                },
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCurrentTurn && player.isOnline) Color.Black else Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DetailedPlayerSeat(
    player: Player,
    isCurrentTurn: Boolean,
    turnSecondsLeft: Int,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isCurrentTurn) GoldPrimary else Color(0x33FFFFFF)
    val borderWidth = if (isCurrentTurn) 2.5.dp else 1.dp

    Card(
        modifier = modifier
            .width(90.dp)
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface.copy(alpha = 0.9f)),
        border = BorderStroke(borderWidth, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Avatar Circle
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2A3630))
                    .border(1.5.dp, if (player.isWinner) GoldPrimary else Color(0x66FFFFFF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.name.take(2).uppercase(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )

                // Voice Speaking Indicator
                if (player.isVoiceConnected) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(NeonCyan)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Active",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(8.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }

            // Player Name
            Text(
                text = player.name,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Chips / Status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Stars,
                    contentDescription = "Chips",
                    tint = GoldPrimary,
                    modifier = Modifier.size(10.dp)
                )
                Text(
                    text = "${player.chips / 1000}k",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )
            }

            // Cards count / Turn Timer / Offline Status
            if (!player.isOnline) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Red)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "OFFLINE",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            } else if (isCurrentTurn) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(GoldPrimary)
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "${turnSecondsLeft}s",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }
            } else {
                Text(
                    text = "${player.handCards.size} cards",
                    fontSize = 8.sp,
                    color = Color.LightGray
                )
            }
        }
    }
}
