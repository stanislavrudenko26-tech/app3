package com.example

import com.example.domain.HabitStreakCalculator
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class HabitStreakCalculatorTest {

    private val today = LocalDate.of(2026, 10, 21)

    @Test
    fun testConsecutiveDaysStreak() {
        val dates = setOf(
            "2026-10-21",
            "2026-10-20",
            "2026-10-19",
            "2026-10-18"
        )
        val (current, best) = HabitStreakCalculator.calculateStreaks(dates, today)
        assertEquals(4, current)
        assertEquals(4, best)
    }

    @Test
    fun testStreakActiveIfDoneYesterdayOnly() {
        val dates = setOf(
            "2026-10-20",
            "2026-10-19"
        )
        val (current, best) = HabitStreakCalculator.calculateStreaks(dates, today)
        assertEquals(2, current)
        assertEquals(2, best)
    }

    @Test
    fun testStreakResetOnMissedDay() {
        val dates = setOf(
            "2026-10-18",
            "2026-10-17",
            "2026-10-16"
        )
        // Missed yesterday (2026-10-20) and 2026-10-19
        val (current, best) = HabitStreakCalculator.calculateStreaks(dates, today)
        assertEquals(0, current)
        assertEquals(3, best)
    }

    @Test
    fun testEmptyDates() {
        val (current, best) = HabitStreakCalculator.calculateStreaks(emptySet(), today)
        assertEquals(0, current)
        assertEquals(0, best)
    }
}
