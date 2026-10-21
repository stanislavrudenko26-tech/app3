package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MealEntity
import com.example.ui.LifeTrackViewModel
import com.example.ui.components.CircularProgressBadge
import com.example.ui.components.LifeTrackCard
import com.example.ui.components.MacroProgressBar
import com.example.ui.theme.EmeraldPrimaryDark

@Composable
fun NutritionHealthScreen(viewModel: LifeTrackViewModel) {
    val meals by viewModel.meals.collectAsState()
    val health by viewModel.dailyHealth.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Харчування", "Активність", "Сон", "Вага")

    var showAddMealDialog by remember { mutableStateOf(false) }
    var showUpdateMetricDialog by remember { mutableStateOf(false) }

    val calorieGoal = profile?.dailyCalorieGoal ?: 2500
    val totalCaloriesConsumed = meals.sumOf { it.calories }
    val totalProtein = meals.sumOf { it.protein }
    val totalFat = meals.sumOf { it.fat }
    val totalCarbs = meals.sumOf { it.carbs }

    val waterMl = health?.waterMl ?: 1500
    val waterGoalMl = health?.waterGoalMl ?: 2500

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .testTag("nutrition_health_screen_content")
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Харчування та здоров'я",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Segmented Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        val isSelected = selectedTabIndex == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
                                .clickable { selectedTabIndex = index }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) EmeraldPrimaryDark else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Date selector header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp), tint = EmeraldPrimaryDark)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Сьогодні, 27 жовтня",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            when (selectedTabIndex) {
                0 -> { // --- Харчування (Nutrition) ---
                    item {
                        // Calories and Macros Card
                        LifeTrackCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Калорії", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(text = "Ціль: $calorieGoal ккал", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressBadge(
                                    progress = (totalCaloriesConsumed.toFloat() / calorieGoal).coerceIn(0f, 1f),
                                    percentageText = "$totalCaloriesConsumed",
                                    size = 80.dp,
                                    strokeWidth = 8.dp,
                                    progressColor = EmeraldPrimaryDark
                                )
                                Spacer(modifier = Modifier.width(20.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    MacroProgressBar(label = "Білки", current = totalProtein.toInt(), target = 150, color = Color(0xFF38BDF8))
                                    MacroProgressBar(label = "Жири", current = totalFat.toInt(), target = 80, color = Color(0xFFFBBF24))
                                    MacroProgressBar(label = "Вуглеводи", current = totalCarbs.toInt(), target = 300, color = Color(0xFF34D399))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Water Intake Card
                        LifeTrackCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.WaterDrop, contentDescription = "Вода", tint = Color(0xFF38BDF8), modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(text = "Випита вода", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text(text = "${String.format("%.1f", waterMl / 1000.0)} л / ${String.format("%.1f", waterGoalMl / 1000.0)} л", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Button(
                                    onClick = { viewModel.addWater(250) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                    modifier = Modifier.testTag("add_water_button")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ 250 мл", color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Meals Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Прийоми їжі", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Button(
                                onClick = { showAddMealDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimaryDark),
                                modifier = Modifier.testTag("add_meal_dialog_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Додати", color = Color.Black, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    items(meals, key = { it.id }) { meal ->
                        MealItemRow(meal = meal, onDelete = { viewModel.deleteMeal(meal.id) })
                    }
                }
                1 -> { // --- Активність (Activity) ---
                    item {
                        LifeTrackCard {
                            Text(text = "Активність за день", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(14.dp))
                            MetricEditRow(
                                title = "Кроки",
                                value = "${health?.steps ?: 8420}",
                                unit = "кроків",
                                icon = Icons.Default.DirectionsRun,
                                onIncrement = { viewModel.updateHealthMetrics(steps = (health?.steps ?: 8420) + 500) }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            MetricEditRow(
                                title = "Тренування",
                                value = "${health?.workoutMinutes ?: 45}",
                                unit = "хв",
                                icon = Icons.Default.FitnessCenter,
                                onIncrement = { viewModel.updateHealthMetrics(workoutMinutes = (health?.workoutMinutes ?: 45) + 15) }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            MetricEditRow(
                                title = "Спалені калорії",
                                value = "${health?.caloriesBurned ?: 320}",
                                unit = "ккал",
                                icon = Icons.Default.FitnessCenter,
                                onIncrement = { }
                            )
                        }
                    }
                }
                2 -> { // --- Сон (Sleep) ---
                    item {
                        LifeTrackCard {
                            Text(text = "Якість та тривалість сну", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bedtime, contentDescription = null, tint = Color(0xFF818CF8), modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(text = "${health?.sleepHours ?: 7.5} годин", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "Норма: 7-9 годин на добу", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { viewModel.updateHealthMetrics(sleepHours = ((health?.sleepHours ?: 7.5) + 0.5).coerceAtMost(14.0)) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Text("+0.5 год", color = MaterialTheme.colorScheme.onSurface)
                                }
                                Button(
                                    onClick = { viewModel.updateHealthMetrics(sleepHours = ((health?.sleepHours ?: 7.5) - 0.5).coerceAtLeast(3.0)) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Text("-0.5 год", color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                }
                3 -> { // --- Вага (Weight) ---
                    item {
                        LifeTrackCard {
                            Text(text = "Контроль ваги", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MonitorWeight, contentDescription = null, tint = EmeraldPrimaryDark, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(text = "${health?.weightKg ?: 66.8} кг", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "Динаміка: -2.3 кг за останній місяць", fontSize = 13.sp, color = EmeraldPrimaryDark)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { viewModel.updateHealthMetrics(weightKg = ((health?.weightKg ?: 66.8) - 0.2)) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Text("-0.2 кг", color = MaterialTheme.colorScheme.onSurface)
                                }
                                Button(
                                    onClick = { viewModel.updateHealthMetrics(weightKg = ((health?.weightKg ?: 66.8) + 0.2)) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Text("+0.2 кг", color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (showAddMealDialog) {
        AddMealDialog(
            onDismiss = { showAddMealDialog = false },
            onAdd = { name, calories, mealType, time, protein, fat, carbs ->
                viewModel.addMeal(name, calories, mealType, time, protein, fat, carbs)
                showAddMealDialog = false
            }
        )
    }
}

@Composable
fun MealItemRow(meal: MealEntity, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Restaurant, contentDescription = null, tint = EmeraldPrimaryDark, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = meal.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    text = "${meal.mealType} • ${meal.time} • Б: ${meal.protein.toInt()}г, Ж: ${meal.fat.toInt()}г, В: ${meal.carbs.toInt()}г",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${meal.calories} ккал",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Видалити", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun MetricEditRow(
    title: String,
    value: String,
    unit: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onIncrement: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = title, tint = EmeraldPrimaryDark)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "$title: $value $unit", fontWeight = FontWeight.Medium, fontSize = 14.sp)
        }
        Button(
            onClick = onIncrement,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.height(32.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
        ) {
            Text("+ Додати", color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 11.sp)
        }
    }
}

@Composable
fun AddMealDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, calories: Int, mealType: String, time: String, protein: Double, fat: Double, carbs: Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var caloriesText by remember { mutableStateOf("") }
    var mealType by remember { mutableStateOf("Обід") }
    var proteinText by remember { mutableStateOf("25") }
    var fatText by remember { mutableStateOf("15") }
    var carbsText by remember { mutableStateOf("40") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Додати прийом їжі", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Назва страви") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = caloriesText,
                    onValueChange = { caloriesText = it },
                    label = { Text("Калорійність (ккал)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("Сніданок", "Обід", "Вечеря", "Перекус").forEach { type ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (mealType == type) EmeraldPrimaryDark else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { mealType = type }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(text = type, fontSize = 11.sp, color = if (mealType == type) Color.Black else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = proteinText,
                        onValueChange = { proteinText = it },
                        label = { Text("Білки") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = fatText,
                        onValueChange = { fatText = it },
                        label = { Text("Жири") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = carbsText,
                        onValueChange = { carbsText = it },
                        label = { Text("Вугл.") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cal = caloriesText.toIntOrNull() ?: 0
                    if (name.isNotBlank() && cal > 0) {
                        onAdd(
                            name,
                            cal,
                            mealType,
                            "14:00",
                            proteinText.toDoubleOrNull() ?: 0.0,
                            fatText.toDoubleOrNull() ?: 0.0,
                            carbsText.toDoubleOrNull() ?: 0.0
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimaryDark)
            ) {
                Text("Зберегти", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Скасувати")
            }
        }
    )
}
