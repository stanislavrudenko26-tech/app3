package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.DailyHealthEntity
import com.example.data.local.HabitEntity
import com.example.data.local.LifeTrackDatabase
import com.example.data.local.MealEntity
import com.example.data.local.TransactionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RoomDatabaseIntegrationTest {

    private lateinit var db: LifeTrackDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, LifeTrackDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testTransactionInsertAndQuery() = runBlocking {
        val tx = TransactionEntity(
            id = "test_tx_1",
            type = "expense",
            amount = 450.0,
            category = "Їжа",
            note = "Обід у кафе",
            date = "2026-10-21"
        )
        db.transactionDao().insertTransaction(tx)

        val list = db.transactionDao().getAllTransactions().first()
        assertEquals(1, list.size)
        assertEquals("Їжа", list[0].category)
        assertEquals(450.0, list[0].amount, 0.01)

        db.transactionDao().deleteTransaction("test_tx_1")
        val emptyList = db.transactionDao().getAllTransactions().first()
        assertEquals(0, emptyList.size)
    }

    @Test
    fun testDailyHealthInsertAndQuery() = runBlocking {
        val health = DailyHealthEntity(
            date = "2026-10-21",
            waterMl = 1750,
            waterGoalMl = 2500,
            steps = 9200,
            workoutMinutes = 50,
            caloriesBurned = 380,
            sleepHours = 8.0,
            weightKg = 66.5
        )
        db.dailyHealthDao().insertOrUpdateHealth(health)

        val retrieved = db.dailyHealthDao().getHealthByDateOnce("2026-10-21")
        assertNotNull(retrieved)
        assertEquals(1750, retrieved?.waterMl)
        assertEquals(9200, retrieved?.steps)
        assertEquals(66.5, retrieved?.weightKg ?: 0.0, 0.01)
    }

    @Test
    fun testMealInsertAndQuery() = runBlocking {
        val meal = MealEntity(
            id = "m1",
            name = "Вівсянка з бананом",
            calories = 350,
            mealType = "Сніданок",
            time = "08:30",
            date = "2026-10-21",
            protein = 12.0,
            fat = 6.0,
            carbs = 62.0
        )
        db.mealDao().insertMeal(meal)

        val mealsList = db.mealDao().getMealsByDate("2026-10-21").first()
        assertEquals(1, mealsList.size)
        assertEquals(350, mealsList[0].calories)
    }

    @Test
    fun testHabitInsertAndStreak() = runBlocking {
        val habit = HabitEntity(
            id = "h1",
            title = "Читання 30 хв",
            targetValue = 30,
            unit = "хв",
            currentStreak = 5,
            bestStreak = 10
        )
        db.habitDao().insertHabit(habit)

        val retrieved = db.habitDao().getHabitById("h1")
        assertNotNull(retrieved)
        assertEquals(5, retrieved?.currentStreak)
    }
}
