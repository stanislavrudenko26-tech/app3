import Foundation

struct FinanceCalculator {
    static func calculateSummary(
        transactions: [Transaction],
        todayDate: String,
        monthlyBudgetLimit: Double = 25000.0,
        baseOpeningBalance: Double = 38200.0
    ) -> FinanceSummary {
        var totalIncome: Double = 0.0
        var totalExpense: Double = 0.0
        var todayIncome: Double = 0.0
        var todayExpense: Double = 0.0
        var categoryMap: [String: Double] = [:]
        
        for t in transactions {
            let amount = max(0.0, t.amount)
            if t.type.caseInsensitiveCompare("income") == .orderedSame {
                totalIncome += amount
                if t.date == todayDate {
                    todayIncome += amount
                }
            } else if t.type.caseInsensitiveCompare("expense") == .orderedSame {
                totalExpense += amount
                if t.date == todayDate {
                    todayExpense += amount
                }
                categoryMap[t.category, default: 0.0] += amount
            }
        }
        
        let totalBalance = baseOpeningBalance + totalIncome - totalExpense
        let todayNet = todayIncome - todayExpense
        
        var breakdown: [CategoryExpense] = []
        if totalExpense > 0 {
            for (cat, amt) in categoryMap {
                let pct = Int(round((amt / totalExpense) * 100.0))
                breakdown.append(CategoryExpense(category: cat, amount: amt, percentage: pct))
            }
            breakdown.sort { $0.amount > $1.amount }
        }
        
        let budgetRemaining = max(0.0, monthlyBudgetLimit - totalExpense)
        let budgetProgress = monthlyBudgetLimit > 0 ? Float(min(1.0, max(0.0, totalExpense / monthlyBudgetLimit))) : 0.0
        
        return FinanceSummary(
            totalBalance: totalBalance,
            totalIncome: totalIncome,
            totalExpense: totalExpense,
            todayNet: todayNet,
            todayIncome: todayIncome,
            todayExpense: todayExpense,
            budgetLimit: monthlyBudgetLimit,
            budgetRemaining: budgetRemaining,
            budgetProgress: budgetProgress,
            categoryBreakdown: breakdown
        )
    }
}
