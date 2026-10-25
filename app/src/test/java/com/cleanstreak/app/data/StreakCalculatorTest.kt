package com.cleanstreak.app.data

import org.junit.Assert.assertEquals
import org.junit.Test

class StreakCalculatorTest {

    private val today = 20_000L // arbitrary epoch day

    @Test
    fun `no records gives zero streaks`() {
        assertEquals(0, StreakCalculator.currentStreak(emptySet(), today))
        assertEquals(0, StreakCalculator.bestStreak(emptySet()))
        assertEquals(0, StreakCalculator.totalClean(emptySet()))
    }

    @Test
    fun `today marked clean counts`() {
        val days = setOf(today)
        assertEquals(1, StreakCalculator.currentStreak(days, today))
    }

    @Test
    fun `pending today does not break streak from yesterday`() {
        val days = setOf(today - 2, today - 1) // clean two days ago and yesterday
        assertEquals(2, StreakCalculator.currentStreak(days, today))
    }

    @Test
    fun `gap in the past breaks streak`() {
        val days = setOf(today, today - 1, today - 3)
        assertEquals(2, StreakCalculator.currentStreak(days, today))
    }

    @Test
    fun `long run counted correctly`() {
        val days = (today - 9..today).toSet() // 10 consecutive days
        assertEquals(10, StreakCalculator.currentStreak(days, today))
        assertEquals(10, StreakCalculator.bestStreak(days))
        assertEquals(10, StreakCalculator.totalClean(days))
    }

    @Test
    fun `best streak finds longest historical run`() {
        // 5-day run long ago, 3-day recent run
        val oldRun = (today - 100 until today - 95).toSet()
        val newRun = (today - 3..today - 1).toSet()
        val days = oldRun + newRun
        assertEquals(5, StreakCalculator.bestStreak(days))
        assertEquals(3, StreakCalculator.currentStreak(days, today))
    }
}
