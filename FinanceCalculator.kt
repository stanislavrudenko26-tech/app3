package com.example.domain

import com.example.data.local.TransactionEntity
import kotlin.math.roundToInt

data class FinanceSummary(
    val totalBalance: Double,
    val totalIncome: Double,
    val totalExpense: Double,
    val todayNet: Double,
    val todayIncome: Double,
    val todayExpense: Double,
    val budgetLimit: Double,
    val budgetRemaining: Double,
    val budgetProgress: Float,
    val categoryBreakdown: List<CategoryExpense>
)

data class CategoryExpense(
    val category: String,
    val amount: Double,
    val percentage: Int
)

object FinanceCalculator {
    fun calculateSummary(
        transactions: List<TransactionEntity>,
        todayDate: String,
        monthlyBudgetLimit: Double = 25000.0,
        baseOpeningBalance: Double = 38200.0
    ): FinanceSummary {
        var totalIncome = 0.0
        var totalExpense = 0.0
        var todayIncome = 0.0
        var todayExpense = 0.0

        val categoryExpenseMap = mutableMapOf<String, Double>()

        for (t in transactions) {
            val amount = if (t.amount > 0) t.amount else 0.0
            if (t.type.equals("income", ignoreCase = true)) {
                totalIncome += amount
                if (t.date == todayDate) {
                    todayIncome += amount
                }
            } else if (t.type.equals("expense", ignoreCase = true)) {
                totalExpense += amount
                if (t.date == todayDate) {
                    todayExpense += amount
                }
                categoryExpenseMap[t.category] = (categoryExpenseMap[t.category] ?: 0.0) + amount
            }
        }

        val totalBalance = baseOpeningBalance + totalIncome - totalExpense
        val todayNet = todayIncome - todayExpense

        val categoryBreakdown = if (totalExpense > 0) {
            categoryExpenseMap.map { (cat, amt) ->
                val pct = ((amt / totalExpense) * 100.0).roundToInt()
                CategoryExpense(category = cat, amount = amt, percentage = pct)
            }.sortedByDescending { it.amount }
        } else {
            emptyList()
        }

        val budgetRemaining = (monthlyBudgetLimit - totalExpense).coerceAtLeast(0.0)
        val budgetProgress = if (monthlyBudgetLimit > 0) {
            (totalExpense / monthlyBudgetLimit).coerceIn(0.0, 1.0).toFloat()
        } else {
            0f
        }

        return FinanceSummary(
            totalBalance = totalBalance,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            todayNet = todayNet,
            todayIncome = todayIncome,
            todayExpense = todayExpense,
            budgetLimit = monthlyBudgetLimit,
            budgetRemaining = budgetRemaining,
            budgetProgress = budgetProgress,
            categoryBreakdown = categoryBreakdown
        )
    }
}
