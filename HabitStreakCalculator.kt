package com.example.domain

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object HabitStreakCalculator {
    /**
     * Calculates current streak and best streak from a set of completed date strings (YYYY-MM-DD).
     */
    fun calculateStreaks(
        completedDates: Set<String>,
        today: LocalDate = LocalDate.now()
    ): Pair<Int, Int> {
        if (completedDates.isEmpty()) return Pair(0, 0)

        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        val parsedDates = completedDates.mapNotNull {
            try {
                LocalDate.parse(it, formatter)
            } catch (e: Exception) {
                null
            }
        }.sortedDescending()

        if (parsedDates.isEmpty()) return Pair(0, 0)

        // Determine current streak
        var currentStreak = 0
        val isCompletedToday = parsedDates.contains(today)
        val isCompletedYesterday = parsedDates.contains(today.minusDays(1))

        if (isCompletedToday || isCompletedYesterday) {
            var checkDate = if (isCompletedToday) today else today.minusDays(1)
            for (date in parsedDates) {
                if (date == checkDate) {
                    currentStreak++
                    checkDate = checkDate.minusDays(1)
                } else if (date.isBefore(checkDate)) {
                    break
                }
            }
        }

        // Determine best streak across all history
        val ascendingDates = parsedDates.sorted()
        var maxStreak = 0
        var tempStreak = 0
        var previousDate: LocalDate? = null

        for (date in ascendingDates) {
            if (previousDate == null) {
                tempStreak = 1
            } else {
                val daysDiff = ChronoUnit.DAYS.between(previousDate, date)
                if (daysDiff == 1L) {
                    tempStreak++
                } else if (daysDiff > 1L) {
                    tempStreak = 1
                }
            }
            if (tempStreak > maxStreak) {
                maxStreak = tempStreak
            }
            previousDate = date
        }

        return Pair(currentStreak, maxOf(currentStreak, maxStreak))
    }
}
