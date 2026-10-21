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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LifeTrackViewModel
import com.example.ui.components.CircularProgressBadge
import com.example.ui.components.LifeTrackCard
import com.example.ui.components.SimpleBarChart
import com.example.ui.theme.EmeraldPrimaryDark

@Composable
fun AnalyticsScreen(viewModel: LifeTrackViewModel) {
    val financeSummary by viewModel.financeSummary.collectAsState()
    val health by viewModel.dailyHealth.collectAsState()
    val habits by viewModel.habits.collectAsState()
    val todayLogs by viewModel.todayHabitLogs.collectAsState()
    val aiAdvice by viewModel.aiAdvice.collectAsState()
    val aiSources by viewModel.aiSources.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()

    var selectedRangeIndex by remember { mutableIntStateOf(0) }
    val ranges = listOf("Тиждень", "Місяць", "3 місяці", "Рік")

    var userQuestionInput by remember { mutableStateOf("") }

    val completedHabits = todayLogs.count { it.completed }
    val totalHabits = habits.size.coerceAtLeast(1)
    val habitRate = ((completedHabits.toFloat() / totalHabits) * 100).toInt()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .testTag("analytics_screen_content")
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Аналітика",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Time Range Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ranges.forEachIndexed { index, range ->
                    val isSelected = selectedRangeIndex == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
                            .clickable { selectedRangeIndex = index }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = range,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) EmeraldPrimaryDark else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Income and Expense Chart Card
            LifeTrackCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Дохід і витрати", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = "28 жовт — 3 лист", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(
                        text = "+4 250 ₴",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimaryDark
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                SimpleBarChart(
                    values = listOf(700f, 1200f, 950f, 1600f, 2100f, 850f, 1100f),
                    height = 110.dp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(EmeraldPrimaryDark))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Дохід: 56 800 ₴", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFEF4444)))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Витрати: 52 550 ₴", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2x2 Mini Metrics Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Weight Card
                LifeTrackCard(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Вага", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Icon(Icons.Default.MonitorWeight, contentDescription = null, tint = EmeraldPrimaryDark, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "-2.3 кг", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimaryDark)
                    Text(text = "${health?.weightKg ?: 66.8} кг поточна", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                // Activity Card
                LifeTrackCard(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Активність", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Icon(Icons.Default.DirectionsRun, contentDescription = null, tint = EmeraldPrimaryDark, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "${health?.steps ?: 8420}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "Середньо кроків/день", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Habits Rate Card
                LifeTrackCard(modifier = Modifier.weight(1f)) {
                    Text(text = "Виконання звичок", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressBadge(progress = habitRate / 100f, percentageText = "$habitRate%", size = 52.dp, strokeWidth = 5.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "$completedHabits з $totalHabits\nсьогодні", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Productivity Card
                LifeTrackCard(modifier = Modifier.weight(1f)) {
                    Text(text = "Продуктивність", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressBadge(progress = 0.72f, percentageText = "72%", size = 52.dp, strokeWidth = 5.dp, progressColor = Color(0xFF38BDF8))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "5 з 7 цілей\nзавершено", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // AI Coach with Search Grounding
            LifeTrackCard(backgroundColor = MaterialTheme.colorScheme.surfaceVariant) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = EmeraldPrimaryDark, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "LifeTrack AI Коуч", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Персональні рекомендації з пошуковим обґрунтуванням Google Search Grounding",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (isAiLoading) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(color = EmeraldPrimaryDark, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Аналізую ваші метрики...", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(14.dp)
                    ) {
                        Text(
                            text = aiAdvice,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (aiSources.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = EmeraldPrimaryDark, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Джерела Google: ${aiSources.joinToString(", ")}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive question input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = userQuestionInput,
                        onValueChange = { userQuestionInput = it },
                        placeholder = { Text("Запитайте AI про звички або фінанси...", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f).testTag("ai_prompt_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (userQuestionInput.isNotBlank()) {
                                viewModel.requestAiAdvice(userQuestionInput)
                                userQuestionInput = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimaryDark)
                            .testTag("send_ai_prompt_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Відправити", tint = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
