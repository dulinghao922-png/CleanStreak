package com.cleanstreak.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.cleanstreak.app.CleanStreakApplication
import com.cleanstreak.app.R
import com.cleanstreak.app.data.HabitWithStatus
import com.cleanstreak.app.data.buildStatuses
import java.time.LocalDate
import kotlinx.coroutines.flow.first

/**
 * Home-screen widget: one row per habit, tap a row to toggle today's clean state.
 * Reads the same repository as the app, so both views are always in sync.
 */
class CleanStreakWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = (context.applicationContext as CleanStreakApplication).repository
        val statuses = buildStatuses(
            habits = repository.habits.first(),
            records = repository.records.first(),
            today = LocalDate.now().toEpochDay()
        )
        val emptyText = context.getString(R.string.widget_empty)
        val colors = WidgetColors.forContext(context)
        provideContent { WidgetContent(statuses, emptyText, colors) }
    }

    @Composable
    private fun WidgetContent(
        statuses: List<HabitWithStatus>,
        emptyText: String,
        colors: WidgetColors
    ) {
        GlanceTheme {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(GlanceTheme.colors.widgetBackground)
                    .padding(12.dp)
            ) {
                if (statuses.isEmpty()) {
                    Box(
                        modifier = GlanceModifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = emptyText,
                            style = TextStyle(color = GlanceTheme.colors.onSurface)
                        )
                    }
                } else {
                    LazyColumn {
                        statuses.forEach { status ->
                            item { WidgetRow(status, colors) }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun WidgetRow(status: HabitWithStatus, colors: WidgetColors) {
        val isChecked = status.isCleanToday
        val color = Color(status.color.argb)
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .cornerRadius(12.dp)
                .background(if (isChecked) color.copy(alpha = 0.85f) else colors.rowUnchecked)
                .clickable(
                    actionRunCallback<ToggleAction>(
                        actionParametersOf(HabitIdParam to status.habit.id)
                    )
                )
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // The color block: white block when lit, habit color when not.
            Box(
                modifier = GlanceModifier
                    .size(18.dp)
                    .cornerRadius(5.dp)
                    .background(if ( isChecked ) Color.White else color)
            ) {}
            Column(
                modifier = GlanceModifier
                    .defaultWeight()
                    .padding(start = 10.dp)
            ) {
                Text(
                    text = status.habit.name,
                    style = TextStyle(
                        color = ColorProvider(if (isChecked) colors.checkedText else colors.textPrimary),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1
                )
                Text(
                    text = "${status.currentStreak}d streak",
                    style = TextStyle(
                        color = ColorProvider(if ( isChecked ) colors.checkedText else colors.textSecondary),
                        fontSize = 11.sp
                    )
                )
            }
            if (isChecked) {
                Text(
                    text = "✓",
                    style = TextStyle(
                        color = ColorProvider(colors.checkedText),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }

    companion object {
        val HabitIdParam = ActionParameters.Key<Long>("habit_id")
    }
}

/** Receiver declared in the manifest; hosts the Glance widget. */
class CleanStreakWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: CleanStreakWidget = CleanStreakWidget()
}
