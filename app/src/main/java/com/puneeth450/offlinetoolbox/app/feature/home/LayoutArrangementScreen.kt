package com.puneeth450.offlinetoolbox.app.feature.home

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneeth450.offlinetoolbox.app.domain.model.ToolCategory
import com.puneeth450.offlinetoolbox.app.ui.components.ToolScaffold

@Composable
fun LayoutArrangementScreen(onNavigateBack: () -> Unit, viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var orderedCategories by remember { mutableStateOf(state.orderedCategories) }
    var draggingCategory by remember { mutableStateOf<ToolCategory?>(null) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    val itemHeights = remember { mutableStateMapOf<ToolCategory, Int>() }

    LaunchedEffect(state.orderedCategories) {
        if (draggingCategory == null) {
            orderedCategories = state.orderedCategories
        }
    }

    ToolScaffold(title = "Layout Arrangement", onNavigateBack = onNavigateBack) {
        Text(
            "Long-press and drag categories to rearrange Home. The same order is used in chips and grouped tool lists.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(orderedCategories, key = { it.name }) { category ->
                val isDragging = draggingCategory == category
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = if (isDragging) 8.dp else 0.dp,
                    shadowElevation = if (isDragging) 10.dp else 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            translationY = if (isDragging) dragOffsetY else 0f
                        }
                        .onSizeChanged { itemHeights[category] = it.height }
                        .pointerInput(orderedCategories, draggingCategory) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = {
                                    draggingCategory = category
                                    dragOffsetY = 0f
                                },
                                onDragCancel = {
                                    draggingCategory = null
                                    dragOffsetY = 0f
                                },
                                onDragEnd = {
                                    draggingCategory = null
                                    dragOffsetY = 0f
                                    viewModel.setCategoryOrder(orderedCategories)
                                }
                            ) { change, dragAmount ->
                                if (draggingCategory != category) return@detectDragGesturesAfterLongPress
                                change.consume()
                                dragOffsetY += dragAmount.y
                                val currentIndex = orderedCategories.indexOf(category)
                                val itemHeight = itemHeights[category]?.toFloat() ?: return@detectDragGesturesAfterLongPress

                                if (dragOffsetY > itemHeight / 2 && currentIndex < orderedCategories.lastIndex) {
                                    val mutable = orderedCategories.toMutableList()
                                    mutable.removeAt(currentIndex)
                                    mutable.add(currentIndex + 1, category)
                                    orderedCategories = mutable
                                    dragOffsetY -= itemHeight
                                } else if (dragOffsetY < -itemHeight / 2 && currentIndex > 0) {
                                    val mutable = orderedCategories.toMutableList()
                                    mutable.removeAt(currentIndex)
                                    mutable.add(currentIndex - 1, category)
                                    orderedCategories = mutable
                                    dragOffsetY += itemHeight
                                }
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(category.title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                if (isDragging) "Drop to save position" else "Long-press and drag",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            "${orderedCategories.indexOf(category) + 1}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
