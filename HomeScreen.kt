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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LifeTrackViewModel
import com.example.ui.components.CircularProgressBadge
import com.example.ui.components.LifeTrackCard
import com.example.ui.components.MacroProgressBar
import com.example.ui.theme.EmeraldPrimaryDark

@Composable
fun HomeScreen(
    viewModel: LifeTrackViewModel,
    onNavigateToTab: (Int) -> Unit
) {
    val profile by viewModel.userProfile.collectAsState()
    val financeSummary by viewModel.financeSummary.collectAsState()
    val health by viewModel.dailyHealth.collectAsState()
    val habits by viewModel.habits.collectAsState()
    val todayLogs by viewModel.todayHabitLogs.collectAsState()
    val goals by viewModel.goals.collectAsState()

    val completedHabitsCount = todayLogs.count { it.completed }
    val totalHabitsCount = habits.size.coerceAtLeast(1)
    val dayProgress = (completedHabitsCount.toFloat() / totalHabitsCount).coerceIn(0f, 1f)
    val dayPercentage = (dayProgress * 100).toInt()

    val currency = profile?.currencySymbol ?: "₴"
    val userName = profile?.displayName?.split(" ")?.firstOrNull() ?: "Станіслав"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("home_screen_content")
    ) {
        // --- Top Bar ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Привіт, $userName 👋",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Понеділок, 27 жовтня 2026",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { /* Notifications */ },
                    modifier = Modifier.testTag("notification_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Сповіщення",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable { onNavigateToTab(4) }
                        .testTag("profile_avatar"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Профіль",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- Card "Мій день" (Daily Progress) ---
        LifeTrackCard(
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            onClick = { onNavigateToTab(3) }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressBadge(
                        progress = dayProgress,
                        percentageText = "$dayPercentage%",
                        size = 68.dp,
                        strokeWidth = 7.dp,
                        progressColor = EmeraldPrimaryDark
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Мій день",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Виконано $completedHabitsCount з $totalHabitsCount цілей",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Деталі",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Category Shortcuts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickNavPill(icon = Icons.Outlined.AccountBalanceWallet, label = "Фінанси") { onNavigateToTab(1) }
                QuickNavPill(icon = Icons.Outlined.Restaurant, label = "Харчування") { onNavigateToTab(2) }
                QuickNavPill(icon = Icons.Outlined.DirectionsRun, label = "Активність") { onNavigateToTab(2) }
                QuickNavPill(icon = Icons.Outlined.TaskAlt, label = "Звички") { onNavigateToTab(3) }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Card "Фінанси" (Finances Summary) ---
        LifeTrackCard(
            onClick = { onNavigateToTab(1) }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.AccountBalanceWallet,
                        contentDescription = "Фінанси",
                        tint = EmeraldPrimaryDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Фінанси",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Відкрити фінанси",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Баланс",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "${financeSummary.totalBalance.toInt()} $currency",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                val sign = if (financeSummary.todayNet >= 0) "+" else ""
                Text(
                    text = "$sign${financeSummary.todayNet.toInt()} $currency за сьогодні",
                    fontSize = 12.sp,
                    color = if (financeSummary.todayNet >= 0) EmeraldPrimaryDark else Color(0xFFEF4444),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Дохід", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "${financeSummary.totalIncome.toInt()} $currency", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                }
                Column {
                    Text(text = "Витрати", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "${financeSummary.totalExpense.toInt()} $currency", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                }
                Column {
                    Text(text = "Залишок", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "${financeSummary.budgetRemaining.toInt()} $currency", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = EmeraldPrimaryDark)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Card "Здоров'я" (Health & Nutrition) ---
        LifeTrackCard(
            onClick = { onNavigateToTab(2) }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = "Здоров'я",
                        tint = EmeraldPrimaryDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Здоров'я та харчування",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Відкрити здоров'я",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressBadge(
                    progress = 1240f / 2500f,
                    percentageText = "1 240",
                    size = 76.dp,
                    strokeWidth = 7.dp,
                    progressColor = EmeraldPrimaryDark
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    MacroProgressBar(label = "Білки", current = 78, target = 150, color = Color(0xFF38BDF8))
                    MacroProgressBar(label = "Жири", current = 45, target = 80, color = Color(0xFFFBBF24))
                    MacroProgressBar(label = "Вуглеводи", current = 120, target = 300, color = Color(0xFF34D399))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Hydration quick bar
            val waterMl = health?.waterMl ?: 1500
            val waterGoal = health?.waterGoalMl ?: 2500
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
                    Icon(imageVector = Icons.Default.WaterDrop, contentDescription = "Вода", tint = Color(0xFF38BDF8))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Вода: ${(waterMl / 1000.0)} л / ${(waterGoal / 1000.0)} л",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Button(
                    onClick = { viewModel.addWater(250) },
                    modifier = Modifier.height(32.dp).testTag("quick_add_water_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Додати", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "250 мл", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Card "Активність" (Activity) ---
        LifeTrackCard(
            onClick = { onNavigateToTab(2) }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DirectionsRun,
                        contentDescription = "Активність",
                        tint = EmeraldPrimaryDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Активність за день",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ActivityMetricBox(icon = Icons.Default.DirectionsRun, title = "Кроки", value = "${health?.steps ?: 8420}", tint = EmeraldPrimaryDark)
                ActivityMetricBox(icon = Icons.Default.FitnessCenter, title = "Тренування", value = "${health?.workoutMinutes ?: 45} хв", tint = Color(0xFFF59E0B))
                ActivityMetricBox(icon = Icons.Default.LocalFireDepartment, title = "Калорії", value = "${health?.caloriesBurned ?: 320} ккал", tint = Color(0xFFEF4444))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Bottom Row: Quick Habit & Active Goal ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Habits preview
            LifeTrackCard(
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToTab(3) }
            ) {
                Text(text = "Звички", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "$completedHabitsCount / $totalHabitsCount виконано",
                    fontSize = 12.sp,
                    color = EmeraldPrimaryDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    habits.take(5).forEach { h ->
                        val isDone = todayLogs.any { it.habitId == h.id && it.completed }
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(if (isDone) EmeraldPrimaryDark else MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }
            }

            // Goals preview
            LifeTrackCard(
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToTab(4) }
            ) {
                Text(text = "Цілі", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                val firstGoal = goals.firstOrNull()
                if (firstGoal != null) {
                    Text(
                        text = firstGoal.title,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${firstGoal.currentValue.toInt()} / ${firstGoal.targetValue.toInt()} ${firstGoal.unit}",
                        fontSize = 11.sp,
                        color = EmeraldPrimaryDark
                    )
                } else {
                    Text(text = "Немає цілей", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun QuickNavPill(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.height(44.dp).testTag("quick_nav_${label.lowercase()}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = EmeraldPrimaryDark, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun ActivityMetricBox(
    icon: ImageVector,
    title: String,
    value: String,
    tint: Color
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = tint, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}
