package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CasinoGreenFelt
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldPrimary
import com.example.viewmodel.RummyViewModel

@Composable
fun DailyRewardDialog(viewModel: RummyViewModel) {
    val isDailyRewardClaimedToday by viewModel.isDailyRewardClaimedToday.collectAsState()

    AlertDialog(
        onDismissRequest = { viewModel.closeDailyRewardModal() },
        containerColor = DarkSurface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = "Daily Reward",
                    tint = GoldPrimary,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "DAILY REWARD WHEEL",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isDailyRewardClaimedToday) "You have already claimed your daily bonus. Come back tomorrow!" else "Welcome back! Here is your daily login streak bonus.",
                    fontSize = 13.sp,
                    color = Color.White
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CasinoGreenFelt),
                    border = BorderStroke(2.dp, if (isDailyRewardClaimedToday) Color.Gray else GoldPrimary)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = "Reward Chips",
                            tint = if (isDailyRewardClaimedToday) Color.Gray else GoldPrimary,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "+15,000 CHIPS",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isDailyRewardClaimedToday) Color.Gray else GoldPrimary
                        )
                        Text(
                            text = if (isDailyRewardClaimedToday) "Next reward available tomorrow" else "Daily Login Bonus • Streak #4",
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (!isDailyRewardClaimedToday) viewModel.claimDailyReward() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isDailyRewardClaimedToday,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = if (isDailyRewardClaimedToday) "ALREADY CLAIMED" else "CLAIM BONUS",
                    color = if (isDailyRewardClaimedToday) Color.White.copy(alpha = 0.5f) else Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}
