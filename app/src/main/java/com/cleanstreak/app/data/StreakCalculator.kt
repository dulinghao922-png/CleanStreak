package com.cleanstreak.app.data

/**
 * Pure streak math over a set of clean epoch-days. Kept free of Android
 * dependencies so it is trivially testable.
 */
object StreakCalculator {

    /**
     * Current streak. Today is allowed to be pending: if today is not yet marked,
     * the streak counts up to yesterday. It only breaks when a past day is missing.
     */
    fun currentStreak(cleanDays: Set<Long>, today: Long): Int {
        val start = if (today in cleanDays) today else today - 1
        if (start !in cleanDays) return 0
        var streak = 0
        var day = start
        while (day in cleanDays) {
            streak++
            day--
        }
        return streak
    }

    /** Longest run of consecutive clean days ever recorded. */
    fun bestStreak(cleanDays: Set<Long>): Int {
        if (cleanDays.isEmpty()) return 0
        val sorted = cleanDays.sorted()
        var best = 1
        var run = 1
        for (i in 1 until sorted.size) {
            run = if (sorted[i] == sorted[i - 1] + 1) run + 1 else 1
            if (run > best) best = run
        }
        return best
    }

    /** Total number of clean days ever recorded. */
    fun totalClean(cleanDays: Set<Long>): Int = cleanDays.size
}
