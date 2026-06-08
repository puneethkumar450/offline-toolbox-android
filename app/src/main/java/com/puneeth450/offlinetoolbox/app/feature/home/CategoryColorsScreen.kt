package com.puneeth450.offlinetoolbox.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    ToolScaffold(title = "Customize Category Colors", onNavigateBack = onNavigateBack) {
        Text(
            "Pick a color for each category. Home uses these as live dashboard accents and icon tints.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        state.orderedCategories.forEach { category ->
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
                                state.categoryColors[category] ?: category.defaultColorHex,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(
                                    (state.categoryColors[category] ?: category.defaultColorHex).toComposeColor(),
                                    CircleShape
                                )
                        )
                    }
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CategoryPalette.forEach { colorHex ->
                            val selected = (state.categoryColors[category] ?: category.defaultColorHex) == colorHex
                            Surface(
                                onClick = { viewModel.setCategoryColor(category, colorHex) },
                                shape = CircleShape,
                                tonalElevation = if (selected) 6.dp else 0.dp,
                                shadowElevation = if (selected) 6.dp else 0.dp
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(colorHex.toComposeColor(), CircleShape)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun String.toComposeColor(): Color = Color(android.graphics.Color.parseColor(this))
