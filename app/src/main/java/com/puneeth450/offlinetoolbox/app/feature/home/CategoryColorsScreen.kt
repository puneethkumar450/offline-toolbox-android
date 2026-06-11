package com.puneeth450.offlinetoolbox.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneeth450.offlinetoolbox.app.domain.model.ToolCategory
import com.puneeth450.offlinetoolbox.app.ui.components.ToolScaffold

private val CategoryPalette = listOf(
    "#5FB8A1", "#EE8E6A", "#4D7CFE", "#9A7BFF",
    "#F2B84B", "#5DB6F2", "#F0719A", "#7BC96F"
)

@Composable
fun CategoryColorsScreen(onNavigateBack: () -> Unit, viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val draftColors = remember { mutableStateMapOf<ToolCategory, String>() }
    val errors = remember { mutableStateMapOf<ToolCategory, String>() }

    LaunchedEffect(state.categoryColors, state.orderedCategories) {
        state.orderedCategories.forEach { category ->
            draftColors[category] = state.categoryColors[category] ?: category.defaultColorHex
        }
    }

    ToolScaffold(title = "Customize Category Colors", onNavigateBack = onNavigateBack) {
        Text(
            "Pick from the preset palette or enter a custom HEX color. Home uses these as live dashboard accents and icon tints.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        state.orderedCategories.forEach { category ->
            val currentColor = draftColors[category] ?: category.defaultColorHex
            Surface(shape = RoundedCornerShape(22.dp), color = MaterialTheme.colorScheme.surface) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(category.title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                currentColor,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(
                                    currentColor.safeToComposeColor(),
                                    CircleShape
                                )
                        )
                    }
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CategoryPalette.forEach { colorHex ->
                            val selected = currentColor.equals(colorHex, ignoreCase = true)
                            Surface(
                                onClick = {
                                    draftColors[category] = colorHex
                                    errors.remove(category)
                                    viewModel.setCategoryColor(category, colorHex)
                                },
                                shape = CircleShape,
                                tonalElevation = if (selected) 6.dp else 0.dp,
                                shadowElevation = if (selected) 6.dp else 0.dp
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(colorHex.safeToComposeColor(), CircleShape)
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = currentColor,
                        onValueChange = { value ->
                            draftColors[category] = value.uppercase()
                            errors.remove(category)
                        },
                        label = { Text("Custom HEX color") },
                        placeholder = { Text("#AABBCC") },
                        supportingText = {
                            Text(errors[category] ?: "Use 6-digit HEX format like #4D7CFE")
                        },
                        isError = errors[category] != null,
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                val normalized = normalizeHexColor(draftColors[category].orEmpty())
                                if (normalized == null) {
                                    errors[category] = "Enter a valid HEX color like #4D7CFE"
                                } else {
                                    draftColors[category] = normalized
                                    errors.remove(category)
                                    viewModel.setCategoryColor(category, normalized)
                                }
                            }
                        ) {
                            Text("Apply Custom Color")
                        }
                        Button(
                            onClick = {
                                draftColors[category] = category.defaultColorHex
                                errors.remove(category)
                                viewModel.setCategoryColor(category, category.defaultColorHex)
                            }
                        ) {
                            Text("Reset")
                        }
                    }
                }
            }
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
        ?: Color(0xFF9AA4B2)
