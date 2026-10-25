package com.cleanstreak.app

import android.app.Application
import com.cleanstreak.app.data.CleanStreakDatabase
import com.cleanstreak.app.data.HabitRepository
import com.cleanstreak.app.widget.WidgetSyncWorker

/**
 * Owns the single database/repository instance shared by the app UI and the
 * home-screen widget, so both always read and write the same data.
 */
class CleanStreakApplication : Application() {

    val database by lazy { CleanStreakDatabase.get(this) }

    val repository by lazy { HabitRepository(database.habitDao()) }

    override fun onCreate() {
        super.onCreate()
        // Keep the widget's "today" fresh across midnight without opening the app.
        WidgetSyncWorker.enqueue(this)
    }
}
