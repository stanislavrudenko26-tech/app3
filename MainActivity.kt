package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.LifeTrackViewModel
import com.example.ui.screens.AddMealDialog
import com.example.ui.screens.AddTransactionDialog
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.FinanceScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NutritionHealthScreen
import com.example.ui.screens.HabitsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.EmeraldPrimaryDark
import com.example.ui.theme.LifeTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: LifeTrackViewModel = viewModel()
            val profile by viewModel.userProfile.collectAsState()

            var isDarkTheme by remember { mutableStateOf(true) }

            LifeTrackTheme(darkTheme = isDarkTheme) {
                LifeTrackApp(
                    viewModel = viewModel,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = { isDarkTheme = !isDarkTheme }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeTrackApp(
    viewModel: LifeTrackViewModel,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showQuickActionSheet by remember { mutableStateOf(false) }

    var showQuickTxDialog by remember { mutableStateOf(false) }
    var showQuickMealDialog by remember { mutableStateOf(false) }

    val profile by viewModel.userProfile.collectAsState()
    val currency = profile?.currencySymbol ?: "₴"

    // BackHandler to return to Home if on another tab
    if (selectedTab != 0) {
        BackHandler {
            selectedTab = 0
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar")
            ) {
                // Tab 0: Головна
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(if (selectedTab == 0) Icons.Filled.Home else Icons.Outlined.Home, contentDescription = "Головна") },
                    label = { Text("Головна", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimaryDark,
                        selectedTextColor = EmeraldPrimaryDark,
                        indicatorColor = EmeraldPrimaryDark.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_item_home")
                )

                // Tab 1: Фінанси
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = "Фінанси") },
                    label = { Text("Фінанси", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimaryDark,
                        selectedTextColor = EmeraldPrimaryDark,
                        indicatorColor = EmeraldPrimaryDark.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_item_finance")
                )

                // Tab 2: Здоров'я
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(if (selectedTab == 2) Icons.Filled.Restaurant else Icons.Outlined.Restaurant, contentDescription = "Здоров'я") },
                    label = { Text("Здоров'я", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimaryDark,
                        selectedTextColor = EmeraldPrimaryDark,
                        indicatorColor = EmeraldPrimaryDark.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_item_health")
                )

                // Tab 3: Звички
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(if (selectedTab == 3) Icons.Filled.CheckCircle else Icons.Outlined.TaskAlt, contentDescription = "Звички") },
                    label = { Text("Звички", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimaryDark,
                        selectedTextColor = EmeraldPrimaryDark,
                        indicatorColor = EmeraldPrimaryDark.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_item_habits")
                )

                // Tab 4: Аналітика
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(if (selectedTab == 4) Icons.Filled.BarChart else Icons.Outlined.BarChart, contentDescription = "Аналітика") },
                    label = { Text("Аналітика", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimaryDark,
                        selectedTextColor = EmeraldPrimaryDark,
                        indicatorColor = EmeraldPrimaryDark.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_item_analytics")
                )

                // Tab 5: Профіль
                NavigationBarItem(
                    selected = selectedTab == 5,
                    onClick = { selectedTab = 5 },
                    icon = { Icon(if (selectedTab == 5) Icons.Filled.Person else Icons.Outlined.Person, contentDescription = "Профіль") },
                    label = { Text("Профіль", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimaryDark,
                        selectedTextColor = EmeraldPrimaryDark,
                        indicatorColor = EmeraldPrimaryDark.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_item_profile")
                )
            }
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showQuickActionSheet = true },
                    containerColor = EmeraldPrimaryDark,
                    contentColor = Color.Black,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(6.dp),
                    modifier = Modifier.testTag("main_action_fab")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Швидка дія")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(viewModel = viewModel, onNavigateToTab = { selectedTab = it })
                1 -> FinanceScreen(viewModel = viewModel)
                2 -> NutritionHealthScreen(viewModel = viewModel)
                3 -> HabitsScreen(viewModel = viewModel)
                4 -> AnalyticsScreen(viewModel = viewModel)
                5 -> ProfileScreen(viewModel = viewModel, isDarkTheme = isDarkTheme, onToggleTheme = onToggleTheme)
            }
        }
    }

    // Quick Action Bottom Sheet
    if (showQuickActionSheet) {
        ModalBottomSheet(
            onDismissRequest = { showQuickActionSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Швидкий запис",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(18.dp))

                QuickActionItem(
                    icon = Icons.Outlined.AccountBalanceWallet,
                    title = "Додати витрату або дохід",
                    subtitle = "Записати чек чи фінансову операцію",
                    onClick = {
                        showQuickActionSheet = false
                        showQuickTxDialog = true
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                QuickActionItem(
                    icon = Icons.Default.Restaurant,
                    title = "Записати прийом їжі",
                    subtitle = "Сніданок, обід чи перекус з калоріями",
                    onClick = {
                        showQuickActionSheet = false
                        showQuickMealDialog = true
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                QuickActionItem(
                    icon = Icons.Default.WaterDrop,
                    title = "Випити склянку води (+250 мл)",
                    subtitle = "Підтримати рівень гідратації організму",
                    onClick = {
                        viewModel.addWater(250)
                        showQuickActionSheet = false
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                QuickActionItem(
                    icon = Icons.Default.CheckCircle,
                    title = "Перейти до звичок дня",
                    subtitle = "Відмітити виконання щоденних цілей",
                    onClick = {
                        showQuickActionSheet = false
                        selectedTab = 3
                    }
                )
            }
        }
    }

    if (showQuickTxDialog) {
        AddTransactionDialog(
            currency = currency,
            onDismiss = { showQuickTxDialog = false },
            onAdd = { type, amount, category, note ->
                viewModel.addTransaction(type, amount, category, note)
                showQuickTxDialog = false
            }
        )
    }

    if (showQuickMealDialog) {
        AddMealDialog(
            onDismiss = { showQuickMealDialog = false },
            onAdd = { name, calories, mealType, time, protein, fat, carbs ->
                viewModel.addMeal(name, calories, mealType, time, protein, fat, carbs)
                showQuickMealDialog = false
            }
        )
    }
}

@Composable
fun QuickActionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(EmeraldPrimaryDark.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = EmeraldPrimaryDark, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
