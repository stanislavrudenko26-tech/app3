import Foundation

struct Transaction: Identifiable, Codable {
    var id: String = UUID().uuidString
    var type: String // "income" or "expense"
    var amount: Double
    var category: String
    var note: String = ""
    var date: String // YYYY-MM-DD
    var timestamp: Date = Date()
}

struct Meal: Identifiable, Codable {
    var id: String = UUID().uuidString
    var name: String
    var calories: Int
    var mealType: String // "Сніданок", "Обід", "Вечеря", "Перекус"
    var time: String // e.g. "09:15"
    var date: String // YYYY-MM-DD
    var protein: Double = 0.0
    var fat: Double = 0.0
    var carbs: Double = 0.0
}

struct DailyHealth: Codable {
    var date: String // YYYY-MM-DD
    var waterMl: Int = 1500
    var waterGoalMl: Int = 2500
    var steps: Int = 8420
    var workoutMinutes: Int = 45
    var caloriesBurned: Int = 320
    var sleepHours: Double = 7.5
    var weightKg: Double = 66.8
}

struct Habit: Identifiable, Codable {
    var id: String = UUID().uuidString
    var title: String
    var targetValue: Int = 1
    var unit: String = "раз"
    var frequency: String = "Щодня"
    var icon: String = "checkmark.circle"
    var currentStreak: Int = 0
    var bestStreak: Int = 0
}

struct HabitLog: Identifiable, Codable {
    var id: String = UUID().uuidString
    var habitId: String
    var date: String // YYYY-MM-DD
    var completed: Bool
    var value: Double = 1.0
}

struct Goal: Identifiable, Codable {
    var id: String = UUID().uuidString
    var title: String
    var category: String
    var targetValue: Double
    var currentValue: Double
    var unit: String
    var deadline: String = ""
    var status: String = "in_progress"
}

struct JournalEntry: Identifiable, Codable {
    var id: String = UUID().uuidString
    var date: String // YYYY-MM-DD
    var mood: String
    var content: String
    var highlight: String = ""
    var timestamp: Date = Date()
}

struct UserProfile: Codable {
    var userId: String = "local_user"
    var displayName: String = "Станіслав Руденко"
    var email: String = "stanislav@gmail.com"
    var currency: String = "UAH"
    var currencySymbol: String = "₴"
    var themeMode: String = "dark"
    var dailyCalorieGoal: Int = 2500
    var dailyWaterGoal: Int = 2500
    var monthlyBudgetLimit: Double = 25000.0
    var notificationsEnabled: Bool = true
}

struct CategoryExpense: Identifiable {
    var id: String { category }
    let category: String
    let amount: Double
    let percentage: Int
}

struct FinanceSummary {
    let totalBalance: Double
    let totalIncome: Double
    let totalExpense: Double
    let todayNet: Double
    let todayIncome: Double
    let todayExpense: Double
    let budgetLimit: Double
    let budgetRemaining: Double
    let budgetProgress: Float
    let categoryBreakdown: [CategoryExpense]
}
