package com.cleanstreak.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.glance.appwidget.updateAll
import com.cleanstreak.app.data.Habit
import com.cleanstreak.app.data.HabitWithStatus
import com.cleanstreak.app.data.buildStatuses
import com.cleanstreak.app.widget.CleanStreakWidget
import java.time.LocalDate
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as CleanStreakApplication).repository

    /** Habits with today's status and streak stats, recomputed on every change. */
    val uiState: StateFlow<List<HabitWithStatus>> =
        combine(repository.habits, repository.records) { habits, records ->
            buildStatuses(habits, records, LocalDate.now().toEpochDay())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun toggleToday(habitId: Long) = viewModelScope.launch {
        repository.toggleToday(habitId)
        refreshWidget()
    }

    fun addHabit(name: String, colorIndex: Int) = viewModelScope.launch {
        repository.addHabit(name, colorIndex)
        refreshWidget()
    }

    fun deleteHabit(habit: Habit) = viewModelScope.launch {
        repository.deleteHabit(habit)
        refreshWidget()
    }

    /** Keeps the home-screen widget in sync after any in-app change. */
    private suspend fun refreshWidget() {
        CleanStreakWidget().updateAll(getApplication())
    }
}
