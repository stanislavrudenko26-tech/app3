package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val type: String, // "income" or "expense"
    val amount: Double,
    val category: String,
    val note: String = "",
    val date: String, // YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey val id: String,
    val name: String,
    val calories: Int,
    val mealType: String, // "Сніданок", "Обід", "Вечеря", "Перекус"
    val time: String, // e.g. "09:15"
    val date: String, // YYYY-MM-DD
    val protein: Double = 0.0,
    val fat: Double = 0.0,
    val carbs: Double = 0.0
)

@Entity(tableName = "daily_health")
data class DailyHealthEntity(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val waterMl: Int = 1500,
    val waterGoalMl: Int = 2500,
    val steps: Int = 8420,
    val workoutMinutes: Int = 45,
    val caloriesBurned: Int = 320,
    val sleepHours: Double = 7.5,
    val weightKg: Double = 66.8
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String,
    val title: String,
    val targetValue: Int = 1,
    val unit: String = "раз",
    val frequency: String = "Щодня",
    val icon: String = "check",
    val currentStreak: Int = 0,
    val bestStreak: Int = 0
)

@Entity(tableName = "habit_logs")
data class HabitLogEntity(
    @PrimaryKey val id: String,
    val habitId: String,
    val date: String, // YYYY-MM-DD
    val completed: Boolean,
    val value: Double = 1.0
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String, // "Здоров'я", "Фінанси", "Продуктивність", "Навчання"
    val targetValue: Double,
    val currentValue: Double,
    val unit: String,
    val deadline: String = "",
    val status: String = "in_progress" // "in_progress", "completed"
)

@Entity(tableName = "journal_entries")
data class JournalEntity(
    @PrimaryKey val id: String,
    val date: String, // YYYY-MM-DD
    val mood: String, // "Чудово", "Добре", "Нормально", "Втомлено"
    val content: String,
    val highlight: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val userId: String = "local_user",
    val displayName: String = "Станіслав Руденко",
    val email: String = "stanislav@gmail.com",
    val currency: String = "UAH",
    val currencySymbol: String = "₴",
    val themeMode: String = "dark",
    val dailyCalorieGoal: Int = 2500,
    val dailyWaterGoal: Int = 2500,
    val monthlyBudgetLimit: Double = 25000.0,
    val notificationsEnabled: Boolean = true
)
