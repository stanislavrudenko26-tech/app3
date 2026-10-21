package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@Database(
    entities = [
        TransactionEntity::class,
        MealEntity::class,
        DailyHealthEntity::class,
        HabitEntity::class,
        HabitLogEntity::class,
        GoalEntity::class,
        JournalEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LifeTrackDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun mealDao(): MealDao
    abstract fun dailyHealthDao(): DailyHealthDao
    abstract fun habitDao(): HabitDao
    abstract fun habitLogDao(): HabitLogDao
    abstract fun goalDao(): GoalDao
    abstract fun journalDao(): JournalDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: LifeTrackDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): LifeTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifeTrackDatabase::class.java,
                    "lifetrack_database"
                )
                    .addCallback(LifeTrackDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class LifeTrackDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(db: LifeTrackDatabase) {
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            val yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)

            // 1. Initial User Profile
            db.userProfileDao().insertOrUpdateProfile(
                UserProfileEntity(
                    userId = "stanislav_user",
                    displayName = "Станіслав Руденко",
                    email = "stanislav@gmail.com",
                    currency = "UAH",
                    currencySymbol = "₴",
                    themeMode = "dark",
                    dailyCalorieGoal = 2500,
                    dailyWaterGoal = 2500,
                    monthlyBudgetLimit = 25000.0,
                    notificationsEnabled = true
                )
            )

            // 2. Initial Transactions (matching screenshot: 42 350 ₴ balance, income, expense categories)
            val initialTransactions = listOf(
                TransactionEntity(UUID.randomUUID().toString(), "income", 18000.0, "Дохід", "Зарплата / Основний дохід", today),
                TransactionEntity(UUID.randomUUID().toString(), "income", 2150.0, "Дохід", "Фріланс проєкт", today),
                TransactionEntity(UUID.randomUUID().toString(), "expense", 4420.0, "Їжа", "Супермаркет та продукти", today),
                TransactionEntity(UUID.randomUUID().toString(), "expense", 3050.0, "Житло", "Оренда та комунальні", today),
                TransactionEntity(UUID.randomUUID().toString(), "expense", 1660.0, "Транспорт", "Пальне та проїзд", today),
                TransactionEntity(UUID.randomUUID().toString(), "expense", 1380.0, "Розваги", "Кіно та вечеря", today),
                TransactionEntity(UUID.randomUUID().toString(), "expense", 1110.0, "Покупки", "Одяг та аксесуари", today),
                TransactionEntity(UUID.randomUUID().toString(), "expense", 830.0, "Здоров'я", "Аптека та вітаміни", today),
                TransactionEntity(UUID.randomUUID().toString(), "expense", 1400.0, "Інше", "Побутові витрати", yesterday)
            )
            for (t in initialTransactions) {
                db.transactionDao().insertTransaction(t)
            }

            // 3. Initial Meals
            val initialMeals = listOf(
                MealEntity(UUID.randomUUID().toString(), "Вівсянка з ягодами та горіхами", 420, "Сніданок", "09:15", today, 18.0, 12.0, 58.0),
                MealEntity(UUID.randomUUID().toString(), "Лосось на грилі з кіноа та салатом", 560, "Обід", "13:30", today, 42.0, 20.0, 44.0),
                MealEntity(UUID.randomUUID().toString(), "Куряче філе з броколі на пару", 260, "Вечеря", "19:45", today, 34.0, 5.0, 12.0)
            )
            for (m in initialMeals) {
                db.mealDao().insertMeal(m)
            }

            // 4. Initial Daily Health
            db.dailyHealthDao().insertOrUpdateHealth(
                DailyHealthEntity(
                    date = today,
                    waterMl = 1500,
                    waterGoalMl = 2500,
                    steps = 8420,
                    workoutMinutes = 45,
                    caloriesBurned = 320,
                    sleepHours = 7.5,
                    weightKg = 66.8
                )
            )

            // 5. Initial Habits
            val initialHabits = listOf(
                HabitEntity("h_water", "Пити 2 л води", 2000, "мл", "Щодня", "water_drop", currentStreak = 12, bestStreak = 21),
                HabitEntity("h_read", "Читати книгу", 30, "хв", "Щодня", "menu_book", currentStreak = 12, bestStreak = 18),
                HabitEntity("h_workout", "Тренування", 45, "хв", "Щодня", "fitness_center", currentStreak = 8, bestStreak = 15),
                HabitEntity("h_nosugar", "Без солодкого", 1, "день", "Щодня", "no_food", currentStreak = 4, bestStreak = 14),
                HabitEntity("h_wakeup", "Ранній підйом", 1, "день", "Щодня", "alarm", currentStreak = 3, bestStreak = 7),
                HabitEntity("h_meditation", "Медитація", 15, "хв", "Щодня", "self_improvement", currentStreak = 0, bestStreak = 10)
            )
            for (h in initialHabits) {
                db.habitDao().insertHabit(h)
            }

            // 6. Habit logs for today (5 of 7 done)
            db.habitLogDao().insertLog(HabitLogEntity(UUID.randomUUID().toString(), "h_water", today, true))
            db.habitLogDao().insertLog(HabitLogEntity(UUID.randomUUID().toString(), "h_read", today, true))
            db.habitLogDao().insertLog(HabitLogEntity(UUID.randomUUID().toString(), "h_workout", today, true))
            db.habitLogDao().insertLog(HabitLogEntity(UUID.randomUUID().toString(), "h_nosugar", today, true))

            // 7. Initial Goals
            val initialGoals = listOf(
                GoalEntity(UUID.randomUUID().toString(), "Схуднути на 5 кг", "Здоров'я", 5.0, 3.2, "кг", "1 груд. 2026", "in_progress"),
                GoalEntity(UUID.randomUUID().toString(), "Накопичити 100 000 ₴", "Фінанси", 100000.0, 25000.0, "₴", "31 груд. 2026", "in_progress"),
                GoalEntity(UUID.randomUUID().toString(), "Бути продуктивнішим", "Продуктивність", 7.0, 5.0, "днів/тиж", "Щотижня", "in_progress")
            )
            for (g in initialGoals) {
                db.goalDao().insertGoal(g)
            }

            // 8. Initial Journal Entry
            db.journalDao().insertEntry(
                JournalEntity(
                    id = UUID.randomUUID().toString(),
                    date = today,
                    mood = "Чудово",
                    content = "Продуктивний день: чудове тренування зранку, закрив важливий фінансовий етап та провів вечір із корисною книгою.",
                    highlight = "12-й день щоденної серії звичок!"
                )
            )
        }
    }
}
