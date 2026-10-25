package com.cleanstreak.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cleanstreak.app.R
import com.cleanstreak.app.data.HabitWithStatus

/**
 * One habit: the tap-to-light color block on the left, name + streak stats in
 * the middle, delete on the right. Tapping anywhere on the card toggles today.
 */
@Composable
fun HabitCard(
    status: HabitWithStatus,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onToggle,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BlockIndicator(status = status)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = status.habit.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(
                        R.string.streak_pattern,
                        status.currentStreak,
                        status.bestStreak
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/** The color block: lit (solid + check) when clean today, dimmed outline when not. */
@Composable
private fun BlockIndicator(status: HabitWithStatus) {
    val color = status.color.toColor()
    Box(
        modifier = Modifier
            .size(52.dp)
            .background(
                color = if (status.isCleanToday) color else color.copy(alpha = 0.12f),
                shape = RoundedCornerShape(14.dp)
            )
            .border(
                width = if (status.isCleanToday) 0.dp else 2.dp,
                color = if (status.isCleanToday) androidx.compose.ui.graphics.Color.Transparent
                else color.copy(alpha = 0.45f),
                shape = RoundedCornerShape(14.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (status.isCleanToday) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
