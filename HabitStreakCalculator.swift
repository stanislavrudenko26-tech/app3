import Foundation

struct HabitStreakCalculator {
    static func calculateStreaks(
        completedDates: Set<String>,
        today: Date = Date()
    ) -> (current: Int, best: Int) {
        if completedDates.isEmpty { return (0, 0) }
        
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        formatter.locale = Locale(identifier: "en_US_POSIX")
        
        let calendar = Calendar.current
        let todayDate = calendar.startOfDay(for: today)
        guard let yesterdayDate = calendar.date(byAdding: .day, value: -1, to: todayDate) else {
            return (0, 0)
        }
        
        let parsedDates = completedDates.compactMap { formatter.date(from: $0) }
            .map { calendar.startOfDay(for: $0) }
            .sorted(by: >)
        
        if parsedDates.isEmpty { return (0, 0) }
        
        var currentStreak = 0
        let isDoneToday = parsedDates.contains(todayDate)
        let isDoneYesterday = parsedDates.contains(yesterdayDate)
        
        if isDoneToday || isDoneYesterday {
            var check = isDoneToday ? todayDate : yesterdayDate
            for d in parsedDates {
                if d == check {
                    currentStreak += 1
                    guard let prev = calendar.date(byAdding: .day, value: -1, to: check) else { break }
                    check = prev
                } else if d < check {
                    break
                }
            }
        }
        
        // Best streak calculation
        let ascending = parsedDates.sorted()
        var maxStreak = 0
        var tempStreak = 0
        var prevDate: Date? = nil
        
        for d in ascending {
            if let prev = prevDate {
                let diff = calendar.dateComponents([.day], from: prev, to: d).day ?? 0
                if diff == 1 {
                    tempStreak += 1
                } else if diff > 1 {
                    tempStreak = 1
                }
            } else {
                tempStreak = 1
            }
            if tempStreak > maxStreak {
                maxStreak = tempStreak
            }
            prevDate = d
        }
        
        return (currentStreak, max(currentStreak, maxStreak))
    }
}
