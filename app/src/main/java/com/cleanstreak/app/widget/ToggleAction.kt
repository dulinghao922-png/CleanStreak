package com.cleanstreak.app.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import com.cleanstreak.app.CleanStreakApplication

/**
 * Runs when the user taps a habit row on the home-screen widget: toggles today's
 * clean state in the shared repository, then refreshes the widget UI.
 */
class ToggleAction : ActionCallback {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val habitId = parameters[CleanStreakWidget.HabitIdParam]
        if (habitId != null) {
            val repository = (context.applicationContext as CleanStreakApplication).repository
            repository.toggleToday(habitId)
        }
        CleanStreakWidget().updateAll(context)
    }
}
