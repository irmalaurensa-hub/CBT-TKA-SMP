package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CbtAnswered
import com.example.ui.theme.CbtCorrect
import com.example.ui.theme.CbtDoubtful
import com.example.ui.theme.CbtUnanswered
import com.example.ui.theme.CbtWrong

@Composable
fun CbtCountdownTimer(
    remainingSeconds: Int,
    modifier: Modifier = Modifier
) {
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeFormatted = "%02d:%02d".format(minutes, seconds)

    val isUrgent = remainingSeconds in 1..300 // Under 5 minutes
    val timerBgColor by animateColorAsState(
        targetValue = if (isUrgent) Color(0xFFFEE2E2) else Color(0xFFEFF6FF),
        label = "timerBg"
    )
    val timerContentColor by animateColorAsState(
        targetValue = if (isUrgent) Color(0xFFDC2626) else Color(0xFF1D4ED8),
        label = "timerContent"
    )

    Surface(
        modifier = modifier.testTag("cbt_timer"),
        shape = RoundedCornerShape(20.dp),
        color = timerBgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, timerContentColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = "Sisa Waktu",
                tint = timerContentColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = timeFormatted,
                color = timerContentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun QuestionOptionItem(
    optionIndex: Int,
    optionText: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val optionLetters = listOf("A", "B", "C", "D", "E")
    val letter = optionLetters.getOrElse(optionIndex) { "?" }

    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFE2E8F0)
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
    val chipBg = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFF1F5F9)
    val chipTextColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color(0xFF334155)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onSelect() }
            .testTag("option_${letter.lowercase()}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(chipBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = chipTextColor
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = optionText,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuestionNumberChip(
    number: Int,
    status: QuestionNavStatus,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    val (bgColor, textColor) = when (status) {
        QuestionNavStatus.ANSWERED -> Pair(CbtAnswered, Color.White)
        QuestionNavStatus.DOUBTFUL -> Pair(CbtDoubtful, Color.White)
        QuestionNavStatus.UNANSWERED -> Pair(Color(0xFFF1F5F9), Color(0xFF475569))
    }

    val borderWidth = if (isCurrent) 3.dp else 1.dp
    val borderColor = if (isCurrent) MaterialTheme.colorScheme.primary else Color(0xFFCBD5E1)

    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(borderWidth, borderColor, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .testTag("nav_question_$number"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$number",
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
    }
}

enum class QuestionNavStatus {
    ANSWERED,
    DOUBTFUL,
    UNANSWERED
}

@Composable
fun AesSecurityBadge(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFECFDF5),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "AES-256",
                tint = Color(0xFF059669),
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Terenkripsi AES-256",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF065F46)
            )
        }
    }
}

@Composable
fun ScoreBadge(score: Double, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when {
        score >= 85.0 -> Pair(Color(0xFFDCFCE7), Color(0xFF166534))
        score >= 70.0 -> Pair(Color(0xFFDBEAFE), Color(0xFF1E40AF))
        score >= 55.0 -> Pair(Color(0xFFFEF3C7), Color(0xFF92400E))
        else -> Pair(Color(0xFFFEE2E2), Color(0xFF991B1B))
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        Text(
            text = "%.1f".format(score),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
