package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.ads.AdMobBannerView
import com.example.ads.PostGameAdMobOverlay
import com.example.engine.RummyEvaluator

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CardMeld
import com.example.model.MeldType
import com.example.model.PlayingCard
import com.example.ui.components.AntiCheatStatusBadge
import com.example.ui.components.PlayerSeatView
import com.example.ui.components.PlayingCardView
import com.example.ui.components.VoiceChatWidget
import com.example.ui.theme.CasinoGreenFelt
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkVelvet
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonCyan
import com.example.viewmodel.AppScreen
import com.example.viewmodel.RummyViewModel

@Composable
fun GameTableScreen(viewModel: RummyViewModel) {
    val players by viewModel.players.collectAsState()
    val stockDeck by viewModel.stockDeck.collectAsState()
    val discardDeck by viewModel.discardDeck.collectAsState()
    val wildJoker by viewModel.wildJoker.collectAsState()
    val currentTurnSeatIndex by viewModel.currentTurnSeatIndex.collectAsState()
    val turnTimeRemaining by viewModel.turnTimeRemaining.collectAsState()
    val humanHand by viewModel.humanHand.collectAsState()
    val humanMelds by viewModel.humanMelds.collectAsState()
    val selectedCards by viewModel.selectedCards.collectAsState()
    val hasDrawnCardThisTurn by viewModel.hasDrawnCardThisTurn.collectAsState()
    val isMicMuted by viewModel.isMicMuted.collectAsState()
    val isSpeakerMuted by viewModel.isSpeakerMuted.collectAsState()
    val voiceWaveLevels by viewModel.voiceWaveLevels.collectAsState()
    val antiCheatStatus by viewModel.antiCheatStatus.collectAsState()
    val showDeclarationModal by viewModel.showDeclarationModal.collectAsState()
    val declarationResult by viewModel.declarationResult.collectAsState()
    val showPostGameAd by viewModel.showPostGameAd.collectAsState()
    val isGameWin by viewModel.isGameWin.collectAsState()
    val earnedChips by viewModel.earnedChips.collectAsState()

    val isHumanTurn = currentTurnSeatIndex == 0

    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.toggleMic()
        } else {
            viewModel.showToast("Microphone permission required for RTC Voice Chat")
        }
    }

    val onMicToggleClicked = {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission || !isMicMuted) {
            viewModel.toggleMic()
        } else {
            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkVelvet)
            .verticalScroll(scrollState)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Action Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconButton(onClick = { viewModel.navigateTo(AppScreen.HOME) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Exit Match",
                        tint = Color.White
                    )
                }

                AntiCheatStatusBadge(status = antiCheatStatus)
            }

            VoiceChatWidget(
                isMicMuted = isMicMuted,
                isSpeakerMuted = isSpeakerMuted,
                waveLevels = voiceWaveLevels,
                onToggleMic = onMicToggleClicked,
                onToggleSpeaker = { viewModel.toggleSpeaker() }
            )
        }

        // Opponents Seats Row (All players included, simple mode)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp) // Reduced height for simple mode
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(players) { player ->
                PlayerSeatView(
                    player = player,
                    isCurrentTurn = currentTurnSeatIndex == player.seatIndex,
                    turnSecondsLeft = turnTimeRemaining,
                    isSimpleMode = true
                )
            }
        }

        // Casino Felt Card Table Center Area
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CasinoGreenFelt),
            border = BorderStroke(3.dp, GoldPrimary)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // Table Label
                Text(
                    text = "RUMMY TABLE • WILD JOKER ACTIVE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0x66FFFFFF),
                    modifier = Modifier.align(Alignment.TopCenter)
                )

                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Closed Stock Pile (Click to Draw)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isHumanTurn && !hasDrawnCardThisTurn) "TAP TO DRAW" else "CLOSED DECK",
                            fontSize = 9.sp,
                            color = if (isHumanTurn && !hasDrawnCardThisTurn) GoldPrimary else Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            if (stockDeck.isNotEmpty()) {
                                PlayingCardView(
                                    card = stockDeck.first(),
                                    isFaceUp = false,
                                    cardWidth = 54.dp,
                                    cardHeight = 78.dp,
                                    onClick = { viewModel.drawFromStock() }
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .background(GoldPrimary, CircleShape)
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${stockDeck.size}",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp, 78.dp)
                                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                        .clickable { viewModel.drawFromStock() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("EMPTY", fontSize = 8.sp, color = Color.Gray)
                                }
                            }
                        }
                    }

                    // Wild Joker Indicator
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("WILD JOKER", fontSize = 9.sp, color = GoldPrimary, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        wildJoker?.let { jokerCard ->
                            PlayingCardView(
                                card = jokerCard,
                                isFaceUp = true,
                                cardWidth = 54.dp,
                                cardHeight = 78.dp
                            )
                        }
                    }

                    // Open Discard Pile (Click to Draw or Discard)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isHumanTurn && hasDrawnCardThisTurn && selectedCards.isNotEmpty()) "TAP TO DISCARD" else "DISCARD DECK",
                            fontSize = 9.sp,
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        val topDiscard = discardDeck.lastOrNull()
                        val onDiscardClick: () -> Unit = {
                            if (hasDrawnCardThisTurn && selectedCards.isNotEmpty()) {
                                val selectedCard = humanHand.firstOrNull { selectedCards.contains(it.id) }
                                selectedCard?.let { viewModel.discardSelectedCard(it) }
                            } else {
                                viewModel.drawFromDiscard()
                            }
                        }

                        if (topDiscard != null) {
                            PlayingCardView(
                                card = topDiscard,
                                isFaceUp = true,
                                cardWidth = 54.dp,
                                cardHeight = 78.dp,
                                onClick = onDiscardClick
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(54.dp, 78.dp)
                                    .border(1.dp, NeonCyan, RoundedCornerShape(8.dp))
                                    .clickable { onDiscardClick() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("EMPTY", fontSize = 8.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }

        // Action Toolbar & Sorting
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Group Selected Cards
                Button(
                    onClick = { viewModel.groupSelectedCards() },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (selectedCards.size >= 2) GoldPrimary else CasinoGreenFelt),
                    modifier = Modifier.height(34.dp).weight(1.2f)
                ) {
                    Icon(Icons.Default.Layers, contentDescription = "Group", modifier = Modifier.size(13.dp), tint = if (selectedCards.size >= 2) Color.Black else GoldPrimary)
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = "GROUP (${selectedCards.size})",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedCards.size >= 2) Color.Black else Color.White
                    )
                }

                // Ungroup
                OutlinedButton(
                    onClick = { viewModel.ungroupSelectedCards() },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(34.dp).weight(1f)
                ) {
                    Text("UNGROUP", fontSize = 9.sp, color = Color.LightGray)
                }

                // Sort by Suit
                OutlinedButton(
                    onClick = { viewModel.autoSortHandBySuit() },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(34.dp).weight(0.8f)
                ) {
                    Text("SUIT", fontSize = 9.sp, color = NeonCyan)
                }

                // Sort by Rank
                OutlinedButton(
                    onClick = { viewModel.autoSortHandByRank() },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(34.dp).weight(0.8f)
                ) {
                    Text("RANK", fontSize = 9.sp, color = NeonCyan)
                }
            }

            // Declare / Finish Button - Visible only when turn and drawn
            if (isHumanTurn && hasDrawnCardThisTurn) {
                Button(
                    onClick = { viewModel.declareHand() },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    modifier = Modifier.fillMaxWidth().height(38.dp)
                ) {
                    Text("DECLARE HAND", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                }
            }
        }

        // Single Row Hand Container displaying cards organized in Groups
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, if (isHumanTurn) GoldPrimary else Color(0x33FFFFFF))
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when {
                            !isHumanTurn -> "OPPONENT'S TURN"
                            !hasDrawnCardThisTurn -> "YOUR TURN • TAP A DECK TO DRAW"
                            else -> "CARD DRAWN • TAP A CARD TO DISCARD"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isHumanTurn) GoldPrimary else Color.Gray
                    )

                    // Discard Selected Card Action
                    if (isHumanTurn && hasDrawnCardThisTurn && selectedCards.isNotEmpty()) {
                        val selectedCard = humanHand.firstOrNull { selectedCards.contains(it.id) }
                        selectedCard?.let { card ->
                            Button(
                                onClick = { viewModel.discardSelectedCard(card) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("DISCARD ${card.displayValue}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    } else if (isHumanTurn && !hasDrawnCardThisTurn) {
                        Button(
                            onClick = { viewModel.drawFromStock() },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("DRAW CARD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }

                // Unified Single Row for Hand Groups
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val activeGroups = if (humanMelds.isNotEmpty()) humanMelds else listOf(RummyEvaluator.evaluateMeld(humanHand))
                    items(activeGroups) { meld ->
                        val groupBorderColor = when (meld.type) {
                            MeldType.PURE_SEQUENCE -> Color(0xFF4CAF50)
                            MeldType.IMPURE_SEQUENCE -> NeonCyan
                            MeldType.SET -> GoldPrimary
                            MeldType.INVALID -> Color(0x66FFFFFF)
                        }

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B221E)),
                            border = BorderStroke(1.5.dp, groupBorderColor)
                        ) {
                            Column(
                                modifier = Modifier.padding(6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // Group Header Label
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(groupBorderColor.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = meld.type.label,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = groupBorderColor
                                    )
                                }

                                // Cards in this group row
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    meld.cards.forEach { card ->
                                        PlayingCardView(
                                            card = card,
                                            isFaceUp = true,
                                            isSelected = selectedCards.contains(card.id),
                                            cardWidth = 52.dp,
                                            cardHeight = 76.dp,
                                            onClick = { viewModel.toggleCardSelection(card.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Declaration Evaluation Modal Dialog
    if (showDeclarationModal && declarationResult != null) {
        val (isValid, msg) = declarationResult!!
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeclarationModal() },
            containerColor = DarkSurface,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isValid) Icons.Default.CheckCircle else Icons.Default.Error,
                        contentDescription = "Result",
                        tint = if (isValid) Color(0xFF4CAF50) else Color.Red,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = if (isValid) "DECLARATION SUCCESSFUL!" else "INVALID DECLARATION",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isValid) GoldPrimary else Color.Red
                    )
                }
            },
            text = {
                Text(
                    text = msg,
                    fontSize = 14.sp,
                    color = Color.White
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dismissDeclarationModal()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("OK", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Post Game AdMob Interstitial Overlay
    if (showPostGameAd) {
        PostGameAdMobOverlay(
            isWin = isGameWin,
            chipsEarned = earnedChips,
            onDismiss = { viewModel.dismissPostGameAd() }
        )
    }
}
