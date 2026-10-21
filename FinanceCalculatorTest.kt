package com.example

import com.example.data.local.TransactionEntity
import com.example.domain.FinanceCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FinanceCalculatorTest {

    private val today = "2026-10-21"
    private val yesterday = "2026-10-20"

    @Test
    fun testBasicFinanceSummaryCalculations() {
        val transactions = listOf(
            TransactionEntity(id = "1", type = "income", amount = 10000.0, category = "Зарплата", date = today),
            TransactionEntity(id = "2", type = "income", amount = 2000.0, category = "Бонус", date = yesterday),
            TransactionEntity(id = "3", type = "expense", amount = 3000.0, category = "Їжа", date = today),
            TransactionEntity(id = "4", type = "expense", amount = 1000.0, category = "Транспорт", date = yesterday)
        )

        val summary = FinanceCalculator.calculateSummary(
            transactions = transactions,
            todayDate = today,
            monthlyBudgetLimit = 20000.0,
            baseOpeningBalance = 0.0
        )

        // Total Income = 10000 + 2000 = 12000
        assertEquals(12000.0, summary.totalIncome, 0.01)

        // Total Expense = 3000 + 1000 = 4000
        assertEquals(4000.0, summary.totalExpense, 0.01)

        // Total Balance = 0 + 12000 - 4000 = 8000
        assertEquals(8000.0, summary.totalBalance, 0.01)

        // Today Income = 10000, Today Expense = 3000 -> Today Net = +7000
        assertEquals(10000.0, summary.todayIncome, 0.01)
        assertEquals(3000.0, summary.todayExpense, 0.01)
        assertEquals(7000.0, summary.todayNet, 0.01)

        // Budget remaining = 20000 - 4000 = 16000
        assertEquals(16000.0, summary.budgetRemaining, 0.01)
        assertEquals(0.20f, summary.budgetProgress, 0.01f)
    }

    @Test
    fun testCategoryBreakdownPercentages() {
        val transactions = listOf(
            TransactionEntity(id = "1", type = "expense", amount = 5000.0, category = "Їжа", date = today),
            TransactionEntity(id = "2", type = "expense", amount = 3000.0, category = "Житло", date = today),
            TransactionEntity(id = "3", type = "expense", amount = 2000.0, category = "Транспорт", date = today)
        )

        val summary = FinanceCalculator.calculateSummary(
            transactions = transactions,
            todayDate = today
        )

        assertEquals(3, summary.categoryBreakdown.size)
        val food = summary.categoryBreakdown.first { it.category == "Їжа" }
        val housing = summary.categoryBreakdown.first { it.category == "Житло" }
        val transport = summary.categoryBreakdown.first { it.category == "Транспорт" }

        assertEquals(50, food.percentage)
        assertEquals(30, housing.percentage)
        assertEquals(20, transport.percentage)
    }

    @Test
    fun testZeroExpenseEdgeCase() {
        val transactions = listOf(
            TransactionEntity(id = "1", type = "income", amount = 5000.0, category = "Зарплата", date = today)
        )

        val summary = FinanceCalculator.calculateSummary(
            transactions = transactions,
            todayDate = today,
            monthlyBudgetLimit = 10000.0,
            baseOpeningBalance = 1000.0
        )

        assertEquals(0.0, summary.totalExpense, 0.01)
        assertEquals(6000.0, summary.totalBalance, 0.01)
        assertEquals(10000.0, summary.budgetRemaining, 0.01)
        assertEquals(0.0f, summary.budgetProgress, 0.01f)
        assertTrue(summary.categoryBreakdown.isEmpty())
    }

    @Test
    fun testBudgetExceededClamping() {
        val transactions = listOf(
            TransactionEntity(id = "1", type = "expense", amount = 30000.0, category = "Житло", date = today)
        )

        val summary = FinanceCalculator.calculateSummary(
            transactions = transactions,
            todayDate = today,
            monthlyBudgetLimit = 25000.0
        )

        // Budget remaining should not be negative
        assertEquals(0.0, summary.budgetRemaining, 0.01)
        // Progress clamped to 1.0f
        assertEquals(1.0f, summary.budgetProgress, 0.01f)
    }
}
