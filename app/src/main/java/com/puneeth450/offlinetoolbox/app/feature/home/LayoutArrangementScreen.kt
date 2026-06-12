package com.puneeth450.offlinetoolbox.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneeth450.offlinetoolbox.app.domain.model.ToolCategory
import com.puneeth450.offlinetoolbox.app.ui.components.CommonTopBar

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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            CommonTopBar(
                title = "Layout Arrangement",
                onNavigateBack = onNavigateBack,
                actionIcon = Icons.Default.Refresh,
                actionDescription = "Reset layout arrangement",
                onActionClick = {
                    orderedCategories = ToolCategory.entries
                    viewModel.setCategoryOrder(ToolCategory.entries)
                }
            )
        }
        item {
            Text(
                text = "Long press the = handle and drag, or use arrows to reorder",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 18.dp, bottom = 10.dp)
            )
        }
        items(orderedCategories, key = { it.name }) { category ->
            val index = orderedCategories.indexOf(category)
            val isDragging = draggingCategory == category

            CategoryArrangementCard(
                category = category,
                isFirst = index == 0,
                isLast = index == orderedCategories.lastIndex,
                isDragging = isDragging,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        translationY = if (isDragging) dragOffsetY else 0f
                        scaleX = if (isDragging) 1.02f else 1f
                        scaleY = if (isDragging) 1.02f else 1f
                    }
                    .onSizeChanged { itemHeights[category] = it.height },
                dragHandleModifier = Modifier.pointerInput(orderedCategories, draggingCategory) {
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
                        val swapThreshold = itemHeight / 2f

                        when {
                            dragOffsetY > swapThreshold && currentIndex < orderedCategories.lastIndex -> {
                                orderedCategories = orderedCategories.moved(currentIndex, currentIndex + 1)
                                dragOffsetY -= itemHeight
                            }
                            dragOffsetY < -swapThreshold && currentIndex > 0 -> {
                                orderedCategories = orderedCategories.moved(currentIndex, currentIndex - 1)
                                dragOffsetY += itemHeight
                            }
                        }
                    }
                },
                onMoveUp = {
                    val currentIndex = orderedCategories.indexOf(category)
                    if (currentIndex > 0) {
                        orderedCategories = orderedCategories.moved(currentIndex, currentIndex - 1)
                        viewModel.setCategoryOrder(orderedCategories)
                    }
                },
                onMoveDown = {
                    val currentIndex = orderedCategories.indexOf(category)
                    if (currentIndex < orderedCategories.lastIndex) {
                        orderedCategories = orderedCategories.moved(currentIndex, currentIndex + 1)
                        viewModel.setCategoryOrder(orderedCategories)
                    }
                }
            )
        }
    }
}

@Composable
private fun CategoryArrangementCard(
    category: ToolCategory,
    isFirst: Boolean,
    isLast: Boolean,
    isDragging: Boolean,
    modifier: Modifier = Modifier,
    dragHandleModifier: Modifier = Modifier,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = if (isDragging) 6.dp else 0.dp,
        shadowElevation = if (isDragging) 8.dp else 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "=",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.62f),
                modifier = dragHandleModifier
                    .padding(end = 18.dp)
            )
            CategoryIcon(category)
            Text(
                text = category.title,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp)
            )
            IconButton(
                onClick = onMoveUp,
                enabled = !isFirst,
                modifier = Modifier.alpha(if (isFirst) 0.28f else 1f)
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move ${category.title} up")
            }
            IconButton(
                onClick = onMoveDown,
                enabled = !isLast,
                modifier = Modifier.alpha(if (isLast) 0.28f else 1f)
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move ${category.title} down")
            }
        }
    }
}

@Composable
private fun CategoryIcon(category: ToolCategory) {
    val color = category.defaultColorHex.safeToComposeColor()
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.16f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = category.icon(),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
    }
}

private fun List<ToolCategory>.moved(fromIndex: Int, toIndex: Int): List<ToolCategory> {
    if (fromIndex !in indices || toIndex !in indices || fromIndex == toIndex) return this
    return toMutableList().apply {
        val item = removeAt(fromIndex)
        add(toIndex, item)
    }
}

private fun ToolCategory.icon(): ImageVector = when (this) {
    ToolCategory.DATE_TIME -> Icons.Default.Timer
    ToolCategory.CALCULATORS -> Icons.Default.Calculate
    ToolCategory.FINANCE -> Icons.Default.AccountBalance
    ToolCategory.ESSENTIAL -> Icons.Default.Star
    ToolCategory.MEASUREMENT -> Icons.Default.Straighten
    ToolCategory.COMMON -> Icons.Default.Build
    ToolCategory.TEXT_TOOLS -> Icons.Default.TextFields
    ToolCategory.MEDIA -> Icons.Default.Image
    ToolCategory.DEVICE_INFO -> Icons.Default.Smartphone
    ToolCategory.GAMES -> Icons.Default.PlayArrow
    ToolCategory.HEALTH -> Icons.Default.FavoriteBorder
    ToolCategory.SOCIAL_WEB -> Icons.Default.Share
    ToolCategory.AI_TOOLS -> Icons.Default.AutoAwesome
    ToolCategory.DEVELOPER -> Icons.Default.Code
}

private fun String.safeToComposeColor(): Color =
    runCatching { Color(android.graphics.Color.parseColor(this)) }.getOrElse { Color(0xFF64748B) }
