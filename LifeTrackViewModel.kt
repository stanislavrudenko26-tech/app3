package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiAiService
import com.example.data.firebase.LifeTrackFirestoreRepository
import com.example.data.local.DailyHealthEntity
import com.example.data.local.GoalEntity
import com.example.data.local.HabitEntity
import com.example.data.local.HabitLogEntity
import com.example.data.local.JournalEntity
import com.example.data.local.LifeTrackDatabase
import com.example.data.local.MealEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.UserProfileEntity
import com.example.domain.FinanceCalculator
import com.example.domain.FinanceSummary
import com.example.domain.HabitStreakCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

class LifeTrackViewModel(application: Application) : AndroidViewModel(application) {
    private val database = LifeTrackDatabase.getDatabase(application, viewModelScope)
    private val transactionDao = database.transactionDao()
    private val mealDao = database.mealDao()
    private val healthDao = database.dailyHealthDao()
    private val habitDao = database.habitDao()
    private val habitLogDao = database.habitLogDao()
    private val goalDao = database.goalDao()
    private val journalDao = database.journalDao()
    private val userProfileDao = database.userProfileDao()
    private val firestoreRepo = LifeTrackFirestoreRepository.create(application)

    val todayDate: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    val userProfile: StateFlow<UserProfileEntity?> = userProfileDao.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val transactions: StateFlow<List<TransactionEntity>> = transactionDao.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val financeSummary: StateFlow<FinanceSummary> = combine(transactions, userProfile) { txs, profile ->
        val budget = profile?.monthlyBudgetLimit ?: 25000.0
        FinanceCalculator.calculateSummary(txs, todayDate, budget)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        FinanceCalculator.calculateSummary(emptyList(), todayDate)
    )

    val meals: StateFlow<List<MealEntity>> = mealDao.getMealsByDate(todayDate)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMeals: StateFlow<List<MealEntity>> = mealDao.getAllMeals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyHealth: StateFlow<DailyHealthEntity?> = healthDao.getHealthByDate(todayDate)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val habits: StateFlow<List<HabitEntity>> = habitDao.getAllHabits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayHabitLogs: StateFlow<List<HabitLogEntity>> = habitLogDao.getLogsByDate(todayDate)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goals: StateFlow<List<GoalEntity>> = goalDao.getAllGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val journalEntries: StateFlow<List<JournalEntity>> = journalDao.getAllEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _aiAdvice = MutableStateFlow<String>("")
    val aiAdvice: StateFlow<String> = _aiAdvice.asStateFlow()

    private val _aiSources = MutableStateFlow<List<String>>(emptyList())
    val aiSources: StateFlow<List<String>> = _aiSources.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    init {
        // Trigger initial AI coaching advice
        requestAiAdvice("Надай комплексний аналіз мого прогресу за сьогодні та поради")
    }

    // --- Actions ---

    fun addTransaction(type: String, amount: Double, category: String, note: String, date: String = todayDate) {
        viewModelScope.launch {
            val entity = TransactionEntity(
                id = UUID.randomUUID().toString(),
                type = type,
                amount = amount,
                category = category,
                note = note,
                date = date
            )
            transactionDao.insertTransaction(entity)
            firestoreRepo.syncTransactionToCloud(entity)
        }
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            transactionDao.deleteTransaction(id)
        }
    }

    fun addMeal(name: String, calories: Int, mealType: String, time: String, protein: Double, fat: Double, carbs: Double) {
        viewModelScope.launch {
            val entity = MealEntity(
                id = UUID.randomUUID().toString(),
                name = name,
                calories = calories,
                mealType = mealType,
                time = time,
                date = todayDate,
                protein = protein,
                fat = fat,
                carbs = carbs
            )
            mealDao.insertMeal(entity)
        }
    }

    fun deleteMeal(id: String) {
        viewModelScope.launch {
            mealDao.deleteMeal(id)
        }
    }

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            val current = healthDao.getHealthByDateOnce(todayDate) ?: DailyHealthEntity(date = todayDate)
            val updated = current.copy(waterMl = (current.waterMl + amountMl).coerceAtLeast(0))
            healthDao.insertOrUpdateHealth(updated)
        }
    }

    fun updateHealthMetrics(steps: Int? = null, workoutMinutes: Int? = null, weightKg: Double? = null, sleepHours: Double? = null) {
        viewModelScope.launch {
            val current = healthDao.getHealthByDateOnce(todayDate) ?: DailyHealthEntity(date = todayDate)
            val updated = current.copy(
                steps = steps ?: current.steps,
                workoutMinutes = workoutMinutes ?: current.workoutMinutes,
                weightKg = weightKg ?: current.weightKg,
                sleepHours = sleepHours ?: current.sleepHours
            )
            healthDao.insertOrUpdateHealth(updated)
        }
    }

    fun toggleHabit(habitId: String) {
        viewModelScope.launch {
            val existing = habitLogDao.getLog(habitId, todayDate)
            if (existing != null && existing.completed) {
                habitLogDao.deleteLog(habitId, todayDate)
            } else {
                habitLogDao.insertLog(HabitLogEntity(UUID.randomUUID().toString(), habitId, todayDate, true))
            }
            // Update habit streak count
            val habit = habitDao.getHabitById(habitId)
            if (habit != null) {
                val newStreak = if (existing != null && existing.completed) (habit.currentStreak - 1).coerceAtLeast(0) else habit.currentStreak + 1
                val newBest = maxOf(newStreak, habit.bestStreak)
                habitDao.updateHabit(habit.copy(currentStreak = newStreak, bestStreak = newBest))
            }
        }
    }

    fun addHabit(title: String, targetValue: Int, unit: String, icon: String = "check") {
        viewModelScope.launch {
            val habit = HabitEntity(
                id = UUID.randomUUID().toString(),
                title = title,
                targetValue = targetValue,
                unit = unit,
                frequency = "Щодня",
                icon = icon,
                currentStreak = 0,
                bestStreak = 0
            )
            habitDao.insertHabit(habit)
        }
    }

    fun deleteHabit(id: String) {
        viewModelScope.launch {
            habitDao.deleteHabit(id)
        }
    }

    fun addGoal(title: String, category: String, targetValue: Double, currentValue: Double, unit: String, deadline: String) {
        viewModelScope.launch {
            val goal = GoalEntity(
                id = UUID.randomUUID().toString(),
                title = title,
                category = category,
                targetValue = targetValue,
                currentValue = currentValue,
                unit = unit,
                deadline = deadline,
                status = "in_progress"
            )
            goalDao.insertGoal(goal)
        }
    }

    fun updateGoalProgress(id: String, newValue: Double) {
        viewModelScope.launch {
            val all = goals.value
            val target = all.firstOrNull { it.id == id } ?: return@launch
            val status = if (newValue >= target.targetValue) "completed" else "in_progress"
            goalDao.updateGoal(target.copy(currentValue = newValue, status = status))
        }
    }

    fun deleteGoal(id: String) {
        viewModelScope.launch {
            goalDao.deleteGoal(id)
        }
    }

    fun addJournalEntry(mood: String, content: String, highlight: String) {
        viewModelScope.launch {
            val entry = JournalEntity(
                id = UUID.randomUUID().toString(),
                date = todayDate,
                mood = mood,
                content = content,
                highlight = highlight
            )
            journalDao.insertEntry(entry)
        }
    }

    fun deleteJournalEntry(id: String) {
        viewModelScope.launch {
            journalDao.deleteEntry(id)
        }
    }

    fun updateProfile(displayName: String, email: String, currency: String, themeMode: String, monthlyBudget: Double) {
        viewModelScope.launch {
            val symbol = when (currency) {
                "USD" -> "$"
                "EUR" -> "€"
                else -> "₴"
            }
            val current = userProfile.value ?: UserProfileEntity()
            val updated = current.copy(
                displayName = displayName,
                email = email,
                currency = currency,
                currencySymbol = symbol,
                themeMode = themeMode,
                monthlyBudgetLimit = monthlyBudget
            )
            userProfileDao.insertOrUpdateProfile(updated)
            firestoreRepo.syncUserProfileToCloud(displayName, email, currency, themeMode)
        }
    }

    fun requestAiAdvice(question: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val summary = financeSummary.value
            val health = dailyHealth.value
            val habitsList = habits.value
            val doneHabitsCount = todayHabitLogs.value.count { it.completed }

            val userContext = buildString {
                append("Фінанси: Баланс = ${summary.totalBalance} ₴, Дохід = ${summary.totalIncome} ₴, Витрати = ${summary.totalExpense} ₴, Ліміт бюджету = ${summary.budgetLimit} ₴.\n")
                append("Здоров'я: Вода = ${health?.waterMl ?: 1500} мл із ${health?.waterGoalMl ?: 2500} мл, Кроки = ${health?.steps ?: 8420}, Тренування = ${health?.workoutMinutes ?: 45} хв, Вага = ${health?.weightKg ?: 66.8} кг.\n")
                append("Звички: Виконано сьогодні ${doneHabitsCount} із ${habitsList.size} звичок.\n")
            }

            val (result, sources) = GeminiAiService.getPersonalizedAdvice(userContext, question)
            _aiAdvice.value = result
            _aiSources.value = sources
            _isAiLoading.value = false
        }
    }
}
