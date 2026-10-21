package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.CategoryExpense
import com.example.ui.theme.ColorFood
import com.example.ui.theme.ColorHealth
import com.example.ui.theme.ColorHousing
import com.example.ui.theme.ColorLeisure
import com.example.ui.theme.ColorOther
import com.example.ui.theme.ColorShopping
import com.example.ui.theme.ColorTransport
import com.example.ui.theme.EmeraldPrimaryDark

@Composable
fun LifeTrackCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            content()
        }
    }
}

@Composable
fun CircularProgressBadge(
    progress: Float,
    percentageText: String,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    strokeWidth: Dp = 8.dp,
    progressColor: Color = EmeraldPrimaryDark,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val arcSize = size.toPx() - strokePx
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
                topLeft = Offset(strokePx / 2, strokePx / 2),
                size = Size(arcSize, arcSize)
            )
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
                topLeft = Offset(strokePx / 2, strokePx / 2),
                size = Size(arcSize, arcSize)
            )
        }
        Text(
            text = percentageText,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Composable
fun DonutExpenseChart(
    breakdown: List<CategoryExpense>,
    modifier: Modifier = Modifier,
    size: Dp = 140.dp,
    totalText: String = "Всього"
) {
    val totalExpense = breakdown.sumOf { it.amount }
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidth = 22.dp.toPx()
            val arcSize = size.toPx() - strokeWidth
            var currentAngle = -90f

            if (breakdown.isEmpty() || totalExpense <= 0) {
                drawArc(
                    color = Color.Gray.copy(alpha = 0.2f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth)
                )
            } else {
                for (item in breakdown) {
                    val sweep = (item.amount / totalExpense * 360f).toFloat()
                    val color = getCategoryColor(item.category)
                    drawArc(
                        color = color,
                        startAngle = currentAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        style = Stroke(width = strokeWidth),
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(arcSize, arcSize)
                    )
                    currentAngle += sweep
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${totalExpense.toInt()} ₴",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
            Text(
                text = totalText,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
    }
}

fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "їжа", "продукти", "харчування" -> ColorFood
        "житло", "оренда", "комунальні" -> ColorHousing
        "транспорт", "авто", "пальне" -> ColorTransport
        "розваги", "відпочинок" -> ColorLeisure
        "покупки", "шопінг", "одяг" -> ColorShopping
        "здоров'я", "аптека", "ліки" -> ColorHealth
        else -> ColorOther
    }
}

@Composable
fun SimpleBarChart(
    values: List<Float>,
    modifier: Modifier = Modifier,
    height: Dp = 90.dp,
    barColor: Color = EmeraldPrimaryDark
) {
    val maxVal = (values.maxOrNull() ?: 1f).coerceAtLeast(1f)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        val days = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Нд")
        values.take(7).forEachIndexed { index, value ->
            val ratio = (value / maxVal).coerceIn(0.1f, 1f)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .width(16.dp)
                        .height(height * ratio * 0.75f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (index == 4) EmeraldPrimaryDark else barColor.copy(alpha = 0.5f))
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = days.getOrElse(index) { "" },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun MacroProgressBar(
    label: String,
    current: Int,
    target: Int,
    color: Color,
    unit: String = "г"
) {
    val progress = if (target > 0) (current.toFloat() / target).coerceIn(0f, 1f) else 0f
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
            Text(text = "$current $unit / $target $unit", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
