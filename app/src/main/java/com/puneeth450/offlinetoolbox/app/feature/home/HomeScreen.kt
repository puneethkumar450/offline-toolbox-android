package com.puneeth450.offlinetoolbox.app.feature.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.puneeth450.offlinetoolbox.app.data.repository.ThemeMode
import com.puneeth450.offlinetoolbox.app.domain.model.ToolCategory
import com.puneeth450.offlinetoolbox.app.domain.model.ToolInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val heroScale = remember { Animatable(0.96f) }

    LaunchedEffect(Unit) {
        heroScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
        )
    }

    if (state.layoutMode == HomeLayoutMode.GRID) {
        GridHomeScreen(state, navController, viewModel, heroScale.value)
    } else {
        ListHomeScreen(state, navController, viewModel, heroScale.value)
    }
}

@Composable
private fun ListHomeScreen(
    state: HomeUiState,
    navController: NavController,
    viewModel: HomeViewModel,
    heroScale: Float
) {
    val groupedTools = remember(state.tools, state.orderedCategories) {
        state.orderedCategories.mapNotNull { category ->
            val tools = state.tools.filter { it.category == category }
            if (tools.isEmpty()) null else category to tools
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            HomeHeader(state, viewModel, heroScale)
        }
        item {
            HomeControls(state, viewModel)
        }
        item {
            HistorySection(state, navController, viewModel)
        }
        groupedTools.forEach { (category, tools) ->
            item {
                AnimatedVisibility(visible = tools.isNotEmpty()) {
                    SectionTitle(category.title)
                }
            }
            items(tools, key = { it.id }) { tool ->
                ToolListCard(
                    tool = tool,
                    categoryColor = state.categoryColors[tool.category]?.toComposeColor() ?: MaterialTheme.colorScheme.primary,
                    isFavorite = tool.id in state.favorites,
                    onFavorite = { viewModel.toggleFavorite(tool.id) },
                    onClick = { navigateToTool(tool, navController, viewModel) }
                )
            }
        }
    }
}

@Composable
private fun GridHomeScreen(
    state: HomeUiState,
    navController: NavController,
    viewModel: HomeViewModel,
    heroScale: Float
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
            HomeHeader(state, viewModel, heroScale)
        }
        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
            HomeControls(state, viewModel)
        }
        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
            HistorySection(state, navController, viewModel)
        }
        items(state.tools, key = { it.id }) { tool ->
            FeaturedToolCard(
                tool = tool,
                categoryColor = state.categoryColors[tool.category]?.toComposeColor() ?: MaterialTheme.colorScheme.primary,
                isFavorite = tool.id in state.favorites,
                onFavorite = { viewModel.toggleFavorite(tool.id) },
                onClick = { navigateToTool(tool, navController, viewModel) }
            )
        }
    }
}

@Composable
private fun HomeHeader(state: HomeUiState, viewModel: HomeViewModel, heroScale: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ToolNest",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Smart, simple utility tools that work instantly.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = viewModel::cycleThemeMode) {
                Icon(
                    imageVector = when (state.themeMode) {
                        ThemeMode.LIGHT -> Icons.Default.LightMode
                        ThemeMode.DARK -> Icons.Default.DarkMode
                        ThemeMode.SYSTEM -> Icons.Default.Contrast
                    },
                    contentDescription = "Toggle theme"
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .scale(heroScale),
            shape = RoundedCornerShape(28.dp),
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.28f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f),
                                MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    )
                    .padding(22.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("All-in-one toolkit", style = MaterialTheme.typography.titleLarge)
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                    }
                    Text(
                        text = "Productivity utilities, finance calculators, dev helpers, and device tools in one calm workspace.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatPill("${state.tools.size} tools")
                        StatPill("No login")
                        StatPill(state.orderedCategories.firstOrNull()?.title ?: "Instant")
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeControls(state: HomeUiState, viewModel: HomeViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::onSearchChanged,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                placeholder = { Text("Search any tool...") },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.weight(1f)
            )
            Surface(
                modifier = Modifier.clickable(onClick = viewModel::toggleHistory),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(Modifier.padding(14.dp), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.History, contentDescription = "History")
                }
            }
            Surface(
                modifier = Modifier.clickable(onClick = viewModel::toggleLayoutMode),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(Modifier.padding(14.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        if (state.layoutMode == HomeLayoutMode.LIST) Icons.Default.Apps else Icons.Default.ViewAgenda,
                        contentDescription = "Toggle layout"
                    )
                }
            }
        }

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CategoryChip(
                label = "All",
                tint = MaterialTheme.colorScheme.primary,
                selected = state.selectedCategory == null,
                onClick = { viewModel.onCategorySelected(null) }
            )
            state.orderedCategories.forEach { category ->
                CategoryChip(
                    label = category.title,
                    tint = state.categoryColors[category]?.toComposeColor() ?: MaterialTheme.colorScheme.primary,
                    selected = state.selectedCategory == category,
                    onClick = { viewModel.onCategorySelected(category) }
                )
            }
        }

    }
}

@Composable
private fun HistorySection(state: HomeUiState, navController: NavController, viewModel: HomeViewModel) {
    AnimatedVisibility(visible = state.isHistoryVisible) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionTitle("History")
            if (state.recentTools.isEmpty()) {
                Text("No recent tools yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    state.recentTools.forEach { tool ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { navigateToTool(tool, navController, viewModel) },
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = tool.icon(),
                                    contentDescription = null,
                                    tint = state.categoryColors[tool.category]?.toComposeColor() ?: MaterialTheme.colorScheme.primary
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(tool.title, style = MaterialTheme.typography.titleMedium)
                                    Text(tool.category.title, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun navigateToTool(tool: ToolInfo, navController: NavController, viewModel: HomeViewModel) {
    viewModel.markRecent(tool.id)
    navController.navigate(tool.route)
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun StatPill(text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun CategoryChip(label: String, tint: Color, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(50),
        color = if (selected) tint else tint.copy(alpha = 0.15f)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            color = if (selected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun FeaturedToolCard(
    tool: ToolInfo,
    categoryColor: Color,
    isFavorite: Boolean,
    onFavorite: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            categoryColor.copy(alpha = 0.14f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(categoryColor.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(tool.icon(), contentDescription = null, tint = categoryColor)
                }
                IconButton(onClick = onFavorite, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(tool.title, style = MaterialTheme.typography.titleMedium)
            Text(
                tool.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.alpha(0.9f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ToolListCard(
    tool: ToolInfo,
    categoryColor: Color,
    isFavorite: Boolean,
    onFavorite: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(categoryColor.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(tool.icon(), contentDescription = null, tint = categoryColor)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(tool.title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    tool.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun ToolInfo.icon(): ImageVector = when (category) {
    ToolCategory.PRODUCTIVITY -> Icons.Default.Timer
    ToolCategory.FINANCE -> Icons.Default.Payments
    ToolCategory.DEVELOPER -> Icons.Default.Code
    ToolCategory.DEVICE -> Icons.Default.Devices
}

private fun String.toComposeColor(): Color = Color(android.graphics.Color.parseColor(this))
