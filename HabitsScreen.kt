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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NoFood
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.HabitEntity
import com.example.ui.LifeTrackViewModel
import com.example.ui.components.LifeTrackCard
import com.example.ui.components.SimpleBarChart
import com.example.ui.theme.EmeraldPrimaryDark

@Composable
fun HabitsScreen(viewModel: LifeTrackViewModel) {
    val habits by viewModel.habits.collectAsState()
    val todayLogs by viewModel.todayHabitLogs.collectAsState()

    var selectedSubTab by remember { mutableIntStateOf(0) }
    val subTabs = listOf("Сьогодні", "Календар", "Цілі")

    var showAddHabitDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddHabitDialog = true },
                containerColor = EmeraldPrimaryDark,
                contentColor = Color.Black,
                modifier = Modifier.testTag("add_habit_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Додати звичку")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .testTag("habits_screen_content")
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Звички",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Sub-tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    subTabs.forEachIndexed { index, title ->
                        val isSelected = selectedSubTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
                                .clickable { selectedSubTab = index }
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

                // Week strip
                WeekCalendarStrip()

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Habits list
            items(habits, key = { it.id }) { habit ->
                val isCompleted = todayLogs.any { it.habitId == habit.id && it.completed }
                HabitItemRow(
                    habit = habit,
                    isCompleted = isCompleted,
                    onToggle = { viewModel.toggleHabit(habit.id) },
                    onDelete = { viewModel.deleteHabit(habit.id) }
                )
            }

            // Streak summary card
            item {
                Spacer(modifier = Modifier.height(16.dp))
                LifeTrackCard(backgroundColor = MaterialTheme.colorScheme.surfaceVariant) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Серія днів", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "12 днів", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text(text = "Твоя найкраща серія — 21 день", fontSize = 12.sp, color = EmeraldPrimaryDark)
                        }
                        Box(modifier = Modifier.width(130.dp)) {
                            SimpleBarChart(values = listOf(1f, 1f, 1f, 1f, 1f, 0.8f, 1f), height = 50.dp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (showAddHabitDialog) {
        AddHabitDialog(
            onDismiss = { showAddHabitDialog = false },
            onAdd = { title, target, unit, icon ->
                viewModel.addHabit(title, target, unit, icon)
                showAddHabitDialog = false
            }
        )
    }
}

@Composable
fun WeekCalendarStrip() {
    val days = listOf("Пн" to "27", "Вт" to "28", "Ср" to "29", "Чт" to "30", "Пт" to "31", "Сб" to "1", "Нд" to "2")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        days.forEachIndexed { index, (dayName, dateNum) ->
            val isToday = index == 0
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isToday) EmeraldPrimaryDark else MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = dayName,
                    fontSize = 11.sp,
                    color = if (isToday) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateNum,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isToday) Color.Black else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun HabitItemRow(
    habit: HabitEntity,
    isCompleted: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag("habit_${habit.id}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getHabitIcon(habit.icon),
                    contentDescription = null,
                    tint = EmeraldPrimaryDark,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = habit.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (habit.targetValue > 1) "${habit.targetValue} ${habit.unit}" else "Серія: ${habit.currentStreak} дн. (Рекорд: ${habit.bestStreak})",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) EmeraldPrimaryDark else MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onToggle() }
                    .testTag("toggle_habit_${habit.id}"),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Виконано", tint = Color.Black, modifier = Modifier.size(20.dp))
                }
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Видалити", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f), modifier = Modifier.size(16.dp))
            }
        }
    }
}

fun getHabitIcon(name: String): ImageVector {
    return when (name) {
        "water_drop" -> Icons.Default.WaterDrop
        "menu_book" -> Icons.Default.MenuBook
        "fitness_center" -> Icons.Default.FitnessCenter
        "no_food" -> Icons.Default.NoFood
        "alarm" -> Icons.Default.Alarm
        "self_improvement" -> Icons.Default.SelfImprovement
        else -> Icons.Default.Check
    }
}

@Composable
fun AddHabitDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, target: Int, unit: String, icon: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var targetText by remember { mutableStateOf("1") }
    var unit by remember { mutableStateOf("раз") }
    var selectedIcon by remember { mutableStateOf("check") }

    val icons = listOf(
        "check" to Icons.Default.Check,
        "water_drop" to Icons.Default.WaterDrop,
        "menu_book" to Icons.Default.MenuBook,
        "fitness_center" to Icons.Default.FitnessCenter,
        "self_improvement" to Icons.Default.SelfImprovement
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Нова корисна звичка", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Назва звички") },
                    modifier = Modifier.fillMaxWidth().testTag("habit_title_input")
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = targetText,
                        onValueChange = { targetText = it },
                        label = { Text("Кількість") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Одиниця") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("Іконка:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(modifier = Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.SpaceAround) {
                    icons.forEach { (key, vector) ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (selectedIcon == key) EmeraldPrimaryDark else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedIcon = key },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(vector, contentDescription = null, tint = if (selectedIcon == key) Color.Black else MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(title, targetText.toIntOrNull() ?: 1, unit, selectedIcon)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimaryDark),
                modifier = Modifier.testTag("save_habit_btn")
            ) {
                Text("Створити", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Скасувати") }
        }
    )
}
