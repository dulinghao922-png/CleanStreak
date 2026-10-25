package com.cleanstreak.app.data

import androidx.compose.ui.graphics.Color

/**
 * Palette shared by the app UI and the home-screen widget, so a habit always
 * shows the same color everywhere.
 */
enum class HabitColor(val argb: Long) {
    GREEN(0xFF2E7D32),
    TEAL(0xFF00796B),
    BLUE(0xFF1565C0),
    PURPLE(0xFF6A1B9A),
    ORANGE(0xFFEF6C00),
    PINK(0xFFAD1457);

    fun toColor(): Color = Color(argb)

    companion object {
        fun from(index: Int): HabitColor = entries.getOrElse(index) { GREEN }
    }
}
