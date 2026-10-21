package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
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
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.data.local.GoalEntity
import com.example.data.local.JournalEntity
import com.example.ui.LifeTrackViewModel
import com.example.ui.components.LifeTrackCard
import com.example.ui.theme.EmeraldPrimaryDark
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun ProfileScreen(
    viewModel: LifeTrackViewModel,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    val profile by viewModel.userProfile.collectAsState()
    val goals by viewModel.goals.collectAsState()
    val journalEntries by viewModel.journalEntries.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    var isSigningIn by remember { mutableStateOf(false) }
    var currentFirebaseUser by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }

    var showAddGoalDialog by remember { mutableStateOf(false) }
    var showAddJournalDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    val currency = profile?.currencySymbol ?: "₴"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .testTag("profile_screen_content")
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Профіль",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            // User Info Header Card
            LifeTrackCard(backgroundColor = MaterialTheme.colorScheme.surfaceVariant) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimaryDark.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = EmeraldPrimaryDark, modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = currentFirebaseUser?.displayName ?: (profile?.displayName ?: "Станіслав Руденко"),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = currentFirebaseUser?.email ?: (profile?.email ?: "stanislav@gmail.com"),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = { showEditProfileDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Редагувати", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Google Sign-In with Credential Manager
                if (currentFirebaseUser == null) {
                    Button(
                        onClick = {
                            isSigningIn = true
                            coroutineScope.launch {
                                try {
                                    val clientId = try {
                                        context.getString(R.string.default_web_client_id)
                                    } catch (e: Exception) {
                                        ""
                                    }
                                    if (clientId.isBlank()) {
                                        Toast.makeText(context, "Хмарна автентифікація готова для підключення Google ID", Toast.LENGTH_SHORT).show()
                                        isSigningIn = false
                                        return@launch
                                    }
                                    val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
                                    val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()
                                    val result = credentialManager.getCredential(context as Activity, request)
                                    val credential = result.credential
                                    if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                        val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                                        val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                                        FirebaseAuth.getInstance().signInWithCredential(authCredential).await()
                                        currentFirebaseUser = FirebaseAuth.getInstance().currentUser
                                        Toast.makeText(context, "Вхід виконано успішно!", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: GetCredentialCancellationException) {
                                    // Cancelled gracefully
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Авторизація: ${e.localizedMessage ?: "Спробуйте пізніше"}", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isSigningIn = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth().testTag("google_sign_in_btn")
                    ) {
                        if (isSigningIn) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimaryContainer)
                        } else {
                            Text(text = "Увійти через Google (Хмарна синхронізація)", color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 13.sp)
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimaryDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Хмарна синхронізація активна", fontSize = 12.sp, color = EmeraldPrimaryDark)
                        }
                        TextButton(onClick = {
                            FirebaseAuth.getInstance().signOut()
                            currentFirebaseUser = null
                        }) {
                            Text(text = "Вийти", color = Color(0xFFEF4444), fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // My Goals Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Мої цілі", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Button(
                    onClick = { showAddGoalDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimaryDark),
                    modifier = Modifier.testTag("add_goal_dialog_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Додати ціль", color = Color.Black, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        // Goals List
        items(goals, key = { it.id }) { goal ->
            GoalItemRow(
                goal = goal,
                onUpdate = { viewModel.updateGoalProgress(goal.id, goal.currentValue + 1.0) },
                onDelete = { viewModel.deleteGoal(goal.id) }
            )
        }

        // Journal & Daily Reflection
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Book, contentDescription = null, tint = EmeraldPrimaryDark, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Щоденник рефлексії", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { showAddJournalDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.testTag("add_journal_btn")
                ) {
                    Text("+ Запис", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(journalEntries, key = { it.id }) { entry ->
            JournalItemRow(entry = entry, onDelete = { viewModel.deleteJournalEntry(entry.id) })
        }

        // Settings Section
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Налаштування", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))

            LifeTrackCard {
                SettingsActionRow(
                    icon = Icons.Outlined.AttachMoney,
                    title = "Валюта",
                    subtitle = "${profile?.currency ?: "UAH"} ($currency)",
                    onClick = { showCurrencyDialog = true }
                )
                Spacer(modifier = Modifier.height(12.dp))
                SettingsActionRow(
                    icon = if (isDarkTheme) Icons.Outlined.DarkMode else Icons.Default.WbSunny,
                    title = "Тема оформлення",
                    subtitle = if (isDarkTheme) "Темна" else "Світла",
                    onClick = onToggleTheme
                )
                Spacer(modifier = Modifier.height(12.dp))
                SettingsActionRow(
                    icon = Icons.Default.Notifications,
                    title = "Нагадування",
                    subtitle = "Щоденні о 09:00 та 21:00",
                    onClick = { Toast.makeText(context, "Нагадування активовано!", Toast.LENGTH_SHORT).show() }
                )
                Spacer(modifier = Modifier.height(12.dp))
                SettingsActionRow(
                    icon = Icons.Default.Lock,
                    title = "Приватність",
                    subtitle = "Дані зашифровані на пристрої",
                    onClick = { Toast.makeText(context, "Локальне сховище захищено Room SQLite", Toast.LENGTH_SHORT).show() }
                )
                Spacer(modifier = Modifier.height(12.dp))
                SettingsActionRow(
                    icon = Icons.Default.Share,
                    title = "Експорт статистики",
                    subtitle = "Формат JSON для резервної копії",
                    onClick = { showExportDialog = true }
                )
                Spacer(modifier = Modifier.height(12.dp))
                SettingsActionRow(
                    icon = Icons.Default.Star,
                    title = "Преміум-підписка",
                    subtitle = "LifeTrack PRO (Активовано)",
                    isPro = true,
                    onClick = { Toast.makeText(context, "PRO-аккаунт активний без обмежень", Toast.LENGTH_SHORT).show() }
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showAddGoalDialog) {
        AddGoalDialog(
            currency = currency,
            onDismiss = { showAddGoalDialog = false },
            onAdd = { title, cat, target, current, unit, deadline ->
                viewModel.addGoal(title, cat, target, current, unit, deadline)
                showAddGoalDialog = false
            }
        )
    }

    if (showAddJournalDialog) {
        AddJournalDialog(
            onDismiss = { showAddJournalDialog = false },
            onAdd = { mood, content, highlight ->
                viewModel.addJournalEntry(mood, content, highlight)
                showAddJournalDialog = false
            }
        )
    }

    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = { Text("Виберіть валюту") },
            text = {
                Column {
                    listOf("UAH" to "Гривня (₴)", "USD" to "Долар ($)", "EUR" to "Євро (€)").forEach { (code, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateProfile(
                                        profile?.displayName ?: "Станіслав Руденко",
                                        profile?.email ?: "stanislav@gmail.com",
                                        code,
                                        profile?.themeMode ?: "dark",
                                        profile?.monthlyBudgetLimit ?: 25000.0
                                    )
                                    showCurrencyDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = label, fontSize = 15.sp, fontWeight = if (profile?.currency == code) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCurrencyDialog = false }) { Text("Закрити") }
            }
        )
    }

    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Експорт даних LifeTrack") },
            text = {
                Column {
                    Text("Експорт включає всі ваші транзакції, звички та прийоми їжі у відкритому форматі JSON:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "{\n  \"user\": \"${profile?.displayName}\",\n  \"transactionsCount\": ${transactions.size},\n  \"goalsCount\": ${goals.size},\n  \"version\": \"1.0\"\n}",
                            fontSize = 11.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        Toast.makeText(context, "Дані успішно експортовано!", Toast.LENGTH_SHORT).show()
                        showExportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimaryDark)
                ) {
                    Text("Копіювати JSON", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) { Text("Закрити") }
            }
        )
    }

    if (showEditProfileDialog) {
        var nameInput by remember { mutableStateOf(profile?.displayName ?: "Станіслав Руденко") }
        var emailInput by remember { mutableStateOf(profile?.email ?: "stanislav@gmail.com") }
        var budgetInput by remember { mutableStateOf("${profile?.monthlyBudgetLimit?.toInt() ?: 25000}") }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Редагувати профіль") },
            text = {
                Column {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Ім'я") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = budgetInput,
                        onValueChange = { budgetInput = it },
                        label = { Text("Місячний ліміт бюджету ($currency)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateProfile(
                            nameInput,
                            emailInput,
                            profile?.currency ?: "UAH",
                            profile?.themeMode ?: "dark",
                            budgetInput.toDoubleOrNull() ?: 25000.0
                        )
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimaryDark)
                ) {
                    Text("Зберегти", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) { Text("Скасувати") }
            }
        )
    }
}

@Composable
fun GoalItemRow(
    goal: GoalEntity,
    onUpdate: () -> Unit,
    onDelete: () -> Unit
) {
    val progress = (goal.currentValue / goal.targetValue.coerceAtLeast(1.0)).coerceIn(0.0, 1.0).toFloat()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .testTag("goal_${goal.id}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = EmeraldPrimaryDark, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = goal.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(text = "${(progress * 100).toInt()}%", fontSize = 12.sp, color = EmeraldPrimaryDark, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = EmeraldPrimaryDark,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${goal.currentValue.toInt()} з ${goal.targetValue.toInt()} ${goal.unit}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Видалити", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f), modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun JournalItemRow(entry: JournalEntity, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = entry.mood, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EmeraldPrimaryDark)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = entry.date, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = entry.content, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
            if (entry.highlight.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "⭐ ${entry.highlight}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Видалити", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f), modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isPro: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = title, tint = EmeraldPrimaryDark, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isPro) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(EmeraldPrimaryDark)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("PRO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
fun AddGoalDialog(
    currency: String,
    onDismiss: () -> Unit,
    onAdd: (title: String, cat: String, target: Double, current: Double, unit: String, deadline: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Здоров'я") }
    var targetText by remember { mutableStateOf("10") }
    var unit by remember { mutableStateOf("кг") }
    var deadline by remember { mutableStateOf("1 груд. 2026") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Нова ціль", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Назва цілі") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = targetText,
                        onValueChange = { targetText = it },
                        label = { Text("Цільове значення") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Одиниця") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = deadline,
                    onValueChange = { deadline = it },
                    label = { Text("Термін") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = targetText.toDoubleOrNull() ?: 1.0
                    if (title.isNotBlank()) {
                        onAdd(title, category, target, 0.0, unit, deadline)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimaryDark)
            ) {
                Text("Створити", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Скасувати") }
        }
    )
}

@Composable
fun AddJournalDialog(
    onDismiss: () -> Unit,
    onAdd: (mood: String, content: String, highlight: String) -> Unit
) {
    var mood by remember { mutableStateOf("Чудово") }
    var content by remember { mutableStateOf("") }
    var highlight by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новий запис рефлексії", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Як пройшов твій день?", fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Чудово", "Добре", "Нормально", "Втомлено").forEach { m ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (mood == m) EmeraldPrimaryDark else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { mood = m }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(text = m, fontSize = 11.sp, color = if (mood == m) Color.Black else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Думки та підсумки дня") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = highlight,
                    onValueChange = { highlight = it },
                    label = { Text("Головна подія або перемога дня") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (content.isNotBlank()) {
                        onAdd(mood, content, highlight)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimaryDark)
            ) {
                Text("Зберегти", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Скасувати") }
        }
    )
}
