package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PlayingCard
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.TextDark

@Composable
fun PlayingCardView(
    card: PlayingCard,
    modifier: Modifier = Modifier,
    isFaceUp: Boolean = true,
    isSelected: Boolean = false,
    cardWidth: Dp = 60.dp,
    cardHeight: Dp = 88.dp,
    cardBackSkin: String = "card_gold",
    onClick: (() -> Unit)? = null
) {
    val offsetY by animateFloatAsState(
        targetValue = if (isSelected) -14f else 0f,
        animationSpec = tween(durationMillis = 180),
        label = "card_offset"
    )

    val baseModifier = modifier
        .offset(y = offsetY.dp)
        .width(cardWidth)
        .height(cardHeight)
        .clip(RoundedCornerShape(8.dp))

    val finalModifier = if (onClick != null) {
        baseModifier.clickable { onClick() }
    } else {
        baseModifier
    }

    Box(
        modifier = finalModifier
    ) {
        if (isFaceUp) {
            val suitColor = if (card.suit.isRed) CrimsonRed else TextDark
            val borderColor = if (isSelected) GoldPrimary else Color(0x33000000)
            val borderThick = if (isSelected) 3.dp else 1.dp

            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(borderThick, borderColor),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                ) {
                    if (card.isPrintedJoker) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("★", fontSize = 18.sp, color = CrimsonRed, fontWeight = FontWeight.Bold)
                            Text("JOKER", fontSize = 9.sp, color = CrimsonRed, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // Top Left rank & suit
                        Column(
                            modifier = Modifier.align(Alignment.TopStart),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = card.rank.code,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = suitColor,
                                lineHeight = 12.sp
                            )
                            Text(
                                text = card.suit.symbol,
                                fontSize = 11.sp,
                                color = suitColor,
                                lineHeight = 11.sp
                            )
                        }

                        // Center Suit Icon
                        Text(
                            text = card.suit.symbol,
                            fontSize = 24.sp,
                            color = suitColor,
                            modifier = Modifier.align(Alignment.Center)
                        )

                        // Bottom Right rank & suit
                        Column(
                            modifier = Modifier.align(Alignment.BottomEnd),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = card.suit.symbol,
                                fontSize = 11.sp,
                                color = suitColor,
                                lineHeight = 11.sp
                            )
                            Text(
                                text = card.rank.code,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = suitColor,
                                lineHeight = 12.sp
                            )
                        }
                    }

                    // Wild Joker Banner Badge
                    if (card.isWildJoker) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .background(GoldPrimary, shape = RoundedCornerShape(4.dp))
                                .padding(horizontal = 3.dp, vertical = 1.dp)
                        ) {
                            Text("JKR", fontSize = 7.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                        }
                    }
                }
            }
        } else {
            // Card Back View
            val backBgColor = when (cardBackSkin) {
                "card_cyber" -> Color(0xFF0A2239)
                "card_velvet" -> Color(0xFF4A0E17)
                else -> Color(0xFF1B3B2B) // Royal Gold / Green
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backBgColor, RoundedCornerShape(8.dp))
                    .border(BorderStroke(1.5.dp, GoldPrimary), RoundedCornerShape(8.dp))
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(BorderStroke(1.dp, Color(0x66FFD700)), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "♦ ♣\n♥ ♠",
                        fontSize = 11.sp,
                        color = GoldPrimary,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
