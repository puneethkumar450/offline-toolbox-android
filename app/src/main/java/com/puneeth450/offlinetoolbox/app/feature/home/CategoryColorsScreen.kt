package com.puneeth450.offlinetoolbox.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneeth450.offlinetoolbox.app.domain.model.ToolCategory
import com.puneeth450.offlinetoolbox.app.ui.components.CommonTopBar

private val CategoryPalette = listOf(
    "#6366F1", "#10B981", "#F97316", "#06B6D4",
    "#3B82F6", "#EC4899", "#64748B", "#14B8A6",
    "#FF6467", "#8B5CF6", "#EAB308", "#2563EB",
    "#EF4444", "#22C55E", "#FF5722", "#9C27B0",
    "#06B6D4", "#FF9800", "#4CAF50", "#E91E63",
    "#8D6E63", "#607D8B", "#F44336", "#009688"
)

@Composable
fun CategoryColorsScreen(onNavigateBack: () -> Unit, viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var selectedCategory by remember { mutableStateOf<ToolCategory?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 28.dp)
    ) {
        CommonTopBar(
            title = "Category Colors",
            onNavigateBack = onNavigateBack,
            actionIcon = Icons.Default.Refresh,
            actionDescription = "Reset category colors",
            onActionClick = {
                state.orderedCategories.forEach { category ->
                    viewModel.setCategoryColor(category, category.defaultColorHex)
                }
            }
        )
        Text(
            text = "Modern Layout only",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 64.dp, top = 2.dp)
        )

        Spacer(Modifier.height(30.dp))
        Text(
            text = "Tap a category to change its accent color",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(22.dp))

        state.orderedCategories.forEachIndexed { index, category ->
            CategoryColorRow(
                category = category,
                color = state.categoryColors[category] ?: category.defaultColorHex,
                onClick = { selectedCategory = category }
            )
            if (index < state.orderedCategories.lastIndex) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.62f))
            }
        }
    }

    selectedCategory?.let { category ->
        CategoryColorDialog(
            category = category,
            initialColor = state.categoryColors[category] ?: category.defaultColorHex,
            onDismiss = { selectedCategory = null },
            onApply = { color ->
                viewModel.setCategoryColor(category, color)
                selectedCategory = null
            }
        )
    }
}

@Composable
private fun CategoryColorRow(category: ToolCategory, color: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(color.safeToComposeColor())
                .border(4.dp, Color.Black.copy(alpha = 0.12f), CircleShape)
        )
        Text(
            text = category.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 28.dp)
        )
        Icon(
            imageVector = Icons.Default.Palette,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(28.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryColorDialog(
    category: ToolCategory,
    initialColor: String,
    onDismiss: () -> Unit,
    onApply: (String) -> Unit
) {
    var selectedColor by remember(category) { mutableStateOf(normalizeHexColor(initialColor) ?: category.defaultColorHex) }
    var customColor by remember(category) { mutableStateOf(selectedColor) }
    var customVisible by remember(category) { mutableStateOf(false) }
    var error by remember(category) { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedColor) {
        customColor = selectedColor
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(30.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 6.dp,
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(category.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(selectedColor.safeToComposeColor())
                )
                Text(
                    text = "Select a color",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                FlowRow(
                    maxItemsInEachRow = 4,
                    horizontalArrangement = Arrangement.spacedBy(22.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    CategoryPalette.forEach { colorHex ->
                        PaletteDot(
                            colorHex = colorHex,
                            selected = selectedColor.equals(colorHex, ignoreCase = true),
                            onClick = {
                                selectedColor = colorHex
                                error = null
                            }
                        )
                    }
                }
                OutlinedButton(
                    onClick = { customVisible = !customVisible },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(Icons.Default.Palette, contentDescription = null)
                    Text("Pick Custom Color", modifier = Modifier.padding(start = 10.dp))
                }
                if (customVisible) {
                    OutlinedTextField(
                        value = customColor,
                        onValueChange = {
                            customColor = it.uppercase()
                            error = null
                        },
                        label = { Text("HEX color") },
                        supportingText = { Text(error ?: "Use 6-digit HEX format like #4D7CFE") },
                        isError = error != null,
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val normalized = if (customVisible) normalizeHexColor(customColor) else selectedColor
                            if (normalized == null) {
                                error = "Enter a valid HEX color like #4D7CFE"
                            } else {
                                onApply(normalized)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.padding(start = 12.dp)
                    ) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}

@Composable
private fun PaletteDot(colorHex: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(62.dp)
            .clip(CircleShape)
            .background(colorHex.safeToComposeColor())
            .border(
                width = if (selected) 4.dp else 2.dp,
                color = if (selected) MaterialTheme.colorScheme.onSurface else Color.Black.copy(alpha = 0.10f),
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

private fun normalizeHexColor(raw: String): String? {
    val trimmed = raw.trim().removePrefix("#")
    if (trimmed.length != 6 || trimmed.any { !it.isDigit() && it.uppercaseChar() !in 'A'..'F' }) return null
    return "#${trimmed.uppercase()}"
}

private fun String.safeToComposeColor(): Color =
    normalizeHexColor(this)?.let { Color(android.graphics.Color.parseColor(it)) }
        ?: Color(0xFF64748B)
