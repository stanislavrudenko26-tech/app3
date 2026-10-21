import Foundation
import Combine

@MainActor
class LifeTrackViewModel: ObservableObject {
    @Published var userProfile: UserProfile = UserProfile()
    @Published var transactions: [Transaction] = []
    @Published var meals: [Meal] = []
    @Published var dailyHealth: DailyHealth = DailyHealth(date: "2026-10-21")
    @Published var habits: [Habit] = []
    @Published var todayHabitLogs: [HabitLog] = []
    @Published var goals: [Goal] = []
    @Published var journalEntries: [JournalEntry] = []
    
    @Published var aiAdvice: String = ""
    @Published var aiSources: [String] = []
    @Published var isAiLoading: Bool = false
    
    var todayDate: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter.string(from: Date())
    }
    
    var financeSummary: FinanceSummary {
        FinanceCalculator.calculateSummary(
            transactions: transactions,
            todayDate: todayDate,
            monthlyBudgetLimit: userProfile.monthlyBudgetLimit
        )
    }
    
    init() {
        loadInitialData()
        requestAiAdvice(question: "Оціни мій прогрес за сьогодні")
    }
    
    private func loadInitialData() {
        // Initial Transactions
        transactions = [
            Transaction(type: "income", amount: 18000.0, category: "Дохід", note: "Зарплата / Основний дохід", date: todayDate),
            Transaction(type: "income", amount: 2150.0, category: "Дохід", note: "Фріланс проєкт", date: todayDate),
            Transaction(type: "expense", amount: 4420.0, category: "Їжа", note: "Супермаркет та продукти", date: todayDate),
            Transaction(type: "expense", amount: 3050.0, category: "Житло", note: "Оренда та комунальні", date: todayDate),
            Transaction(type: "expense", amount: 1660.0, category: "Транспорт", note: "Пальне та проїзд", date: todayDate),
            Transaction(type: "expense", amount: 1380.0, category: "Розваги", note: "Кіно та вечеря", date: todayDate),
            Transaction(type: "expense", amount: 1110.0, category: "Покупки", note: "Одяг та аксесуари", date: todayDate),
            Transaction(type: "expense", amount: 830.0, category: "Здоров'я", note: "Аптека та вітаміни", date: todayDate)
        ]
        
        // Initial Meals
        meals = [
            Meal(name: "Вівсянка з ягодами та горіхами", calories: 420, mealType: "Сніданок", time: "09:15", date: todayDate, protein: 18.0, fat: 12.0, carbs: 58.0),
            Meal(name: "Лосось на грилі з кіноа та салатом", calories: 560, mealType: "Обід", time: "13:30", date: todayDate, protein: 42.0, fat: 20.0, carbs: 44.0),
            Meal(name: "Куряче філе з броколі на пару", calories: 260, mealType: "Вечеря", time: "19:45", date: todayDate, protein: 34.0, fat: 5.0, carbs: 12.0)
        ]
        
        // Initial Habits
        let h1 = Habit(title: "Пити 2 л води", targetValue: 2000, unit: "мл", icon: "drop.fill", currentStreak: 12, bestStreak: 21)
        let h2 = Habit(title: "Читати книгу", targetValue: 30, unit: "хв", icon: "book.fill", currentStreak: 12, bestStreak: 18)
        let h3 = Habit(title: "Тренування", targetValue: 45, unit: "хв", icon: "figure.run", currentStreak: 8, bestStreak: 15)
        let h4 = Habit(title: "Без солодкого", targetValue: 1, unit: "день", icon: "fork.knife", currentStreak: 4, bestStreak: 14)
        let h5 = Habit(title: "Ранній підйом", targetValue: 1, unit: "день", icon: "alarm.fill", currentStreak: 3, bestStreak: 7)
        let h6 = Habit(title: "Медитація", targetValue: 15, unit: "хв", icon: "sparkles", currentStreak: 0, bestStreak: 10)
        
        habits = [h1, h2, h3, h4, h5, h6]
        
        todayHabitLogs = [
            HabitLog(habitId: h1.id, date: todayDate, completed: true),
            HabitLog(habitId: h2.id, date: todayDate, completed: true),
            HabitLog(habitId: h3.id, date: todayDate, completed: true),
            HabitLog(habitId: h4.id, date: todayDate, completed: true)
        ]
        
        // Initial Goals
        goals = [
            Goal(title: "Схуднути на 5 кг", category: "Здоров'я", targetValue: 5.0, currentValue: 3.2, unit: "кг", deadline: "1 груд. 2026"),
            Goal(title: "Накопичити 100 000 ₴", category: "Фінанси", targetValue: 100000.0, currentValue: 25000.0, unit: "₴", deadline: "31 груд. 2026"),
            Goal(title: "Бути продуктивнішим", category: "Продуктивність", targetValue: 7.0, currentValue: 5.0, unit: "днів/тиж", deadline: "Щотижня")
        ]
        
        // Initial Journal
        journalEntries = [
            JournalEntry(
                date: todayDate,
                mood: "Чудово",
                content: "Продуктивний день: чудове тренування зранку, закрив важливий фінансовий етап та провів вечір із корисною книгою.",
                highlight: "12-й день щоденної серії звичок!"
            )
        ]
    }
    
    // --- Actions ---
    
    func addTransaction(type: String, amount: Double, category: String, note: String) {
        let t = Transaction(type: type, amount: amount, category: category, note: note, date: todayDate)
        transactions.insert(t, at: 0)
    }
    
    func deleteTransaction(id: String) {
        transactions.removeAll { $0.id == id }
    }
    
    func addMeal(name: String, calories: Int, mealType: String, time: String, protein: Double, fat: Double, carbs: Double) {
        let m = Meal(name: name, calories: calories, mealType: mealType, time: time, date: todayDate, protein: protein, fat: fat, carbs: carbs)
        meals.append(m)
    }
    
    func deleteMeal(id: String) {
        meals.removeAll { $0.id == id }
    }
    
    func addWater(amountMl: Int) {
        dailyHealth.waterMl = max(0, dailyHealth.waterMl + amountMl)
    }
    
    func toggleHabit(habitId: String) {
        if let idx = todayHabitLogs.firstIndex(where: { $0.habitId == habitId && $0.completed }) {
            todayHabitLogs.remove(at: idx)
            if let hIdx = habits.firstIndex(where: { $0.id == habitId }) {
                habits[hIdx].currentStreak = max(0, habits[hIdx].currentStreak - 1)
            }
        } else {
            todayHabitLogs.append(HabitLog(habitId: habitId, date: todayDate, completed: true))
            if let hIdx = habits.firstIndex(where: { $0.id == habitId }) {
                habits[hIdx].currentStreak += 1
                habits[hIdx].bestStreak = max(habits[hIdx].currentStreak, habits[hIdx].bestStreak)
            }
        }
    }
    
    func addHabit(title: String, target: Int, unit: String, icon: String) {
        let h = Habit(title: title, targetValue: target, unit: unit, icon: icon)
        habits.append(h)
    }
    
    func deleteHabit(id: String) {
        habits.removeAll { $0.id == id }
    }
    
    func addGoal(title: String, category: String, target: Double, current: Double, unit: String, deadline: String) {
        let g = Goal(title: title, category: category, targetValue: target, currentValue: current, unit: unit, deadline: deadline)
        goals.append(g)
    }
    
    func updateGoalProgress(id: String, delta: Double) {
        if let idx = goals.firstIndex(where: { $0.id == id }) {
            goals[idx].currentValue += delta
            if goals[idx].currentValue >= goals[idx].targetValue {
                goals[idx].status = "completed"
            }
        }
    }
    
    func deleteGoal(id: String) {
        goals.removeAll { $0.id == id }
    }
    
    func addJournalEntry(mood: String, content: String, highlight: String) {
        let j = JournalEntry(date: todayDate, mood: mood, content: content, highlight: highlight)
        journalEntries.insert(j, at: 0)
    }
    
    func deleteJournalEntry(id: String) {
        journalEntries.removeAll { $0.id == id }
    }
    
    func updateProfile(name: String, email: String, currency: String, budget: Double) {
        userProfile.displayName = name
        userProfile.email = email
        userProfile.currency = currency
        userProfile.currencySymbol = currency == "USD" ? "$" : (currency == "EUR" ? "€" : "₴")
        userProfile.monthlyBudgetLimit = budget
    }
    
    func requestAiAdvice(question: String) {
        Task {
            isAiLoading = true
            let context = "Фінанси: Баланс = \(financeSummary.totalBalance) ₴, Дохід = \(financeSummary.totalIncome) ₴, Витрати = \(financeSummary.totalExpense) ₴. Здоров'я: Вода = \(dailyHealth.waterMl) мл, Кроки = \(dailyHealth.steps). Звички: \(todayHabitLogs.count) з \(habits.count) виконано."
            let (ans, srcs) = await GeminiAiService.shared.getPersonalizedAdvice(userContext: context, userQuestion: question)
            self.aiAdvice = ans
            self.aiSources = srcs
            self.isAiLoading = false
        }
    }
}
