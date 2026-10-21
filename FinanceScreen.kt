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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.local.TransactionEntity
import com.example.domain.CategoryExpense
import com.example.ui.LifeTrackViewModel
import com.example.ui.components.DonutExpenseChart
import com.example.ui.components.LifeTrackCard
import com.example.ui.components.SimpleBarChart
import com.example.ui.components.getCategoryColor
import com.example.ui.theme.EmeraldPrimaryDark

@Composable
fun FinanceScreen(viewModel: LifeTrackViewModel) {
    val summary by viewModel.financeSummary.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val currency = profile?.currencySymbol ?: "₴"

    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = EmeraldPrimaryDark,
                contentColor = Color.Black,
                modifier = Modifier.testTag("add_transaction_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Додати операцію")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .testTag("finance_screen_content")
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Фінанси",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Total Balance Card ---
                LifeTrackCard(backgroundColor = MaterialTheme.colorScheme.surfaceVariant) {
                    Text(
                        text = "Загальний баланс",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${summary.totalBalance.toInt()} $currency",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val sign = if (summary.todayNet >= 0) "+" else ""
                    Text(
                        text = "$sign${summary.todayNet.toInt()} $currency за сьогодні",
                        fontSize = 13.sp,
                        color = if (summary.todayNet >= 0) EmeraldPrimaryDark else Color(0xFFEF4444),
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Дохід", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "${summary.totalIncome.toInt()} $currency", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Column {
                            Text(text = "Витрати", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "${summary.totalExpense.toInt()} $currency", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Column {
                            Text(text = "Бюджет", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "${summary.budgetLimit.toInt()} $currency", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EmeraldPrimaryDark)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Category Breakdown with Donut Chart ---
                LifeTrackCard {
                    Text(
                        text = "Категорії витрат",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DonutExpenseChart(
                            breakdown = summary.categoryBreakdown,
                            size = 140.dp
                        )
                        Spacer(modifier = Modifier.width(18.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            summary.categoryBreakdown.take(6).forEach { item ->
                                CategoryLegendRow(item = item, currency = currency)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Monthly Budget Progress Card ---
                LifeTrackCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Місячний бюджет",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Ліміт ${summary.budgetLimit.toInt()} $currency",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(EmeraldPrimaryDark))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Залишок ${summary.budgetRemaining.toInt()} $currency",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "${(summary.budgetProgress * 100).toInt()}%",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { summary.budgetProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = EmeraldPrimaryDark,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Monthly Expenses Bar Chart ---
                LifeTrackCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Витрати за тиждень", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(text = "Жовтень 2026 >", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    SimpleBarChart(
                        values = listOf(450f, 620f, 310f, 890f, 1420f, 980f, 650f),
                        height = 100.dp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Історія операцій",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            if (transactions.isEmpty()) {
                item {
                    Text(
                        text = "Операцій поки немає. Натисніть '+', щоб додати першу!",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 20.dp)
                    )
                }
            } else {
                items(transactions, key = { it.id }) { tx ->
                    TransactionItemRow(
                        transaction = tx,
                        currency = currency,
                        onDelete = { viewModel.deleteTransaction(tx.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (showAddDialog) {
        AddTransactionDialog(
            currency = currency,
            onDismiss = { showAddDialog = false },
            onAdd = { type, amount, category, note ->
                viewModel.addTransaction(type, amount, category, note)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun CategoryLegendRow(item: CategoryExpense, currency: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(getCategoryColor(item.category))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = item.category,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${item.percentage}%",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${item.amount.toInt()} $currency",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun TransactionItemRow(
    transaction: TransactionEntity,
    currency: String,
    onDelete: () -> Unit
) {
    val isIncome = transaction.type.equals("income", ignoreCase = true)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .testTag("tx_${transaction.id}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (isIncome) EmeraldPrimaryDark.copy(alpha = 0.2f) else Color(0xFFEF4444).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = if (isIncome) EmeraldPrimaryDark else Color(0xFFEF4444),
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = transaction.category,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (transaction.note.isNotBlank()) {
                    Text(
                        text = transaction.note,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                Text(
                    text = transaction.date,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            val prefix = if (isIncome) "+" else "-"
            Text(
                text = "$prefix${transaction.amount.toInt()} $currency",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (isIncome) EmeraldPrimaryDark else Color(0xFFEF4444)
            )
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Видалити",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun AddTransactionDialog(
    currency: String,
    onDismiss: () -> Unit,
    onAdd: (type: String, amount: Double, category: String, note: String) -> Unit
) {
    var type by remember { mutableStateOf("expense") }
    var amountText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Їжа") }
    var note by remember { mutableStateOf("") }

    val expenseCategories = listOf("Їжа", "Житло", "Транспорт", "Розваги", "Покупки", "Здоров'я", "Інше")
    val incomeCategories = listOf("Зарплата", "Фріланс", "Інвестиції", "Подарунок", "Інше")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Додати операцію", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                // Type switch
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = type == "expense", onClick = {
                            type = "expense"
                            category = "Їжа"
                        })
                        Text("Витрата", fontSize = 13.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = type == "income", onClick = {
                            type = "income"
                            category = "Зарплата"
                        })
                        Text("Дохід", fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Сума ($currency)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("amount_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Категорія:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                val currentCategories = if (type == "expense") expenseCategories else incomeCategories
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    currentCategories.take(4).forEach { cat ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (category == cat) EmeraldPrimaryDark else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { category = cat }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                color = if (category == cat) Color.Black else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Нотатка (необов'язково)") },
                    modifier = Modifier.fillMaxWidth().testTag("note_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (amount > 0) {
                        onAdd(type, amount, category, note)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimaryDark),
                modifier = Modifier.testTag("save_transaction_btn")
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
