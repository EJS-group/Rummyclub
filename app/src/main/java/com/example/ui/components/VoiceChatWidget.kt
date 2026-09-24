package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NeonCyan

@Composable
fun VoiceChatWidget(
    isMicMuted: Boolean,
    isSpeakerMuted: Boolean,
    waveLevels: List<Float>,
    onToggleMic: () -> Unit,
    onToggleSpeaker: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurface)
            .border(1.dp, Color(0x33FFD700), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Mic Toggle
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isMicMuted) Color(0x44D32F2F) else Color(0x4400E5FF))
                .clickable { onToggleMic() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                contentDescription = "Microphone",
                tint = if (isMicMuted) Color.Red else NeonCyan,
                modifier = Modifier.size(18.dp)
            )
        }

        // Speaker Toggle
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isSpeakerMuted) Color(0x44D32F2F) else Color(0x33FFFFFF))
                .clickable { onToggleSpeaker() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSpeakerMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                contentDescription = "Speaker",
                tint = if (isSpeakerMuted) Color.Red else Color.White,
                modifier = Modifier.size(18.dp)
            )
        }

        // Voice Waveform Bars
        Row(
            modifier = Modifier.height(18.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            waveLevels.forEach { level ->
                val barHeight by animateFloatAsState(targetValue = if (isMicMuted) 3f else (level * 18f).coerceAtLeast(3f))
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(barHeight.dp)
                        .background(if (isMicMuted) Color.Gray else NeonCyan, CircleShape)
                )
            }
        }

        Text(
            text = if (isMicMuted) "Muted" else "RTC Live",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isMicMuted) Color.Gray else NeonCyan
        )
    }
}
