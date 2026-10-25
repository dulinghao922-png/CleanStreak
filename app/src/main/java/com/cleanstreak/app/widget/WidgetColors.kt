package com.cleanstreak.app.widget

import android.content.Context
import android.content.res.Configuration
import androidx.compose.ui.graphics.Color

/**
 * Widget color set that adapts to light/dark home-screen themes.
 *
 * Glance widgets render as RemoteViews, so Compose's [isSystemInDarkTheme]-style
 * composition locals cannot be relied on. Instead the day/night mode is resolved
 * from the configuration when [CleanStreakWidget.provideGlance] runs and passed
 * down as plain colors.
 *
 * Checked rows always use the habit color as background (all palette entries are
 * dark 700-tones), so checked-state text stays white in both themes.
 */
data class WidgetColors(
    /** Background of an unmarked (not clean today) row. */
    val rowUnchecked: Color,
    /** Habit name text on an unmarked row. */
    val textPrimary: Color,
    /** Streak caption text on an unmarked row. */
    val textSecondary: Color,
    /** All text on a marked row (sits on the habit color). */
    val checkedText: Color
) {
    companion object {
        val LIGHT = WidgetColors(
            rowUnchecked = Color(0x14000000),   // subtle ink tint on a light surface
            textPrimary = Color(0xFF1C1B1F),
            textSecondary = Color(0xFF49454F),
            checkedText = Color.White
        )

        val DARK = WidgetColors(
            rowUnchecked = Color(0x22FFFFFF),   // subtle white tint on a dark surface
            textPrimary = Color.White,
            textSecondary = Color(0xB3FFFFFF),
            checkedText = Color.White
        )

        fun forContext(context: Context): WidgetColors {
            val nightMode = context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
            return if (nightMode == Configuration.UI_MODE_NIGHT_YES) DARK else LIGHT
        }
    }
}
