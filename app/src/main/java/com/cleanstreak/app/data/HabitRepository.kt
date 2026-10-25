package com.cleanstreak.app.data

import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for habit data. Used by both the app UI and the widget,
 * so a tap in either place produces the exact same state change.
 */
class HabitRepository(private val dao: HabitDao) {

    val habits: Flow<List<Habit>> = dao.observeHabits()
    val records: Flow<List<CleanRecord>> = dao.observeRecords()

    suspend fun addHabit(name: String, colorIndex: Int) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        dao.insertHabit(
            Habit(
                name = trimmed,
                colorIndex = colorIndex,
                position = dao.maxPosition() + 1,
                createdAtEpochDay = LocalDate.now().toEpochDay()
            )
        )
    }

    suspend fun deleteHabit(habit: Habit) {
        dao.deleteHabitWithHistory(habit.id)
    }

    /**
     * Tap-to-light behaviour: insert today's clean record, or remove it if already clean
     * (second tap = undo). Returns the new clean state for today.
     */
    suspend fun toggleToday(habitId: Long): Boolean {
        val today = LocalDate.now().toEpochDay()
        return if (dao.isClean(habitId, today)) {
            dao.deleteRecord(habitId, today)
            false
        } else {
            dao.insertRecord(CleanRecord(habitId = habitId, epochDay = today))
            true
        }
    }
}
