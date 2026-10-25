package com.cleanstreak.app.data

/**
 * Everything the UI and widget need to render one habit row, precomputed.
 */
data class HabitWithStatus(
    val habit: Habit,
    val isCleanToday: Boolean,
    val currentStreak: Int,
    val bestStreak: Int,
    val totalCleanDays: Int,
    val color: HabitColor
)

/** Maps raw database rows to [HabitWithStatus] for a given today (epoch day). */
fun buildStatuses(habits: List<Habit>, records: List<CleanRecord>, today: Long): List<HabitWithStatus> {
    val daysByHabit = records.groupBy({ it.habitId }, { it.epochDay })
    return habits.map { habit ->
        val days = daysByHabit[habit.id].orEmpty().toSet()
        HabitWithStatus(
            habit = habit,
            isCleanToday = today in days,
            currentStreak = StreakCalculator.currentStreak(days, today),
            bestStreak = StreakCalculator.bestStreak(days),
            totalCleanDays = StreakCalculator.totalClean(days),
            color = HabitColor.from(habit.colorIndex)
        )
    }
}
