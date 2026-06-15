package com.puneeth450.offlinetoolbox.app.feature.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlusOne
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.puneeth450.offlinetoolbox.app.data.repository.ThemeMode
import com.puneeth450.offlinetoolbox.app.domain.model.ToolCatalog
import com.puneeth450.offlinetoolbox.app.domain.model.ToolCategory
import com.puneeth450.offlinetoolbox.app.domain.model.ToolInfo
import com.puneeth450.offlinetoolbox.app.ui.theme.OfflineToolboxTheme

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    HomeScreenContent(
        state = state,
        onThemeCycle = viewModel::cycleThemeMode,
        onHistoryToggle = viewModel::toggleHistory,
        onLayoutToggle = viewModel::toggleLayoutMode,
        onCategorySelected = viewModel::onCategorySelected,
        onFavoriteToggle = viewModel::toggleFavorite,
        onToolClick = { tool ->
            viewModel.markRecent(tool.id)
            navController.navigate(tool.route)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    state: HomeUiState,
    onThemeCycle: () -> Unit,
    onHistoryToggle: () -> Unit,
    onLayoutToggle: () -> Unit,
    onCategorySelected: (ToolCategory?) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onToolClick: (ToolInfo) -> Unit
) {
    val heroScale = remember { Animatable(0.96f) }

    LaunchedEffect(Unit) {
        heroScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
        )
    }

    if (state.layoutMode == HomeLayoutMode.GRID) {
        GridHomeScreen(
            state = state,
            heroScale = heroScale.value,
            onThemeCycle = onThemeCycle,
            onHistoryToggle = onHistoryToggle,
            onLayoutToggle = onLayoutToggle,
            onCategorySelected = onCategorySelected,
            onFavoriteToggle = onFavoriteToggle,
            onToolClick = onToolClick
        )
    } else {
        ListHomeScreen(
            state = state,
            heroScale = heroScale.value,
            onThemeCycle = onThemeCycle,
            onHistoryToggle = onHistoryToggle,
            onLayoutToggle = onLayoutToggle,
            onCategorySelected = onCategorySelected,
            onFavoriteToggle = onFavoriteToggle,
            onToolClick = onToolClick
        )
    }
}

@Composable
private fun ListHomeScreen(
    state: HomeUiState,
    heroScale: Float,
    onThemeCycle: () -> Unit,
    onHistoryToggle: () -> Unit,
    onLayoutToggle: () -> Unit,
    onCategorySelected: (ToolCategory?) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onToolClick: (ToolInfo) -> Unit
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
            HomeHeader(state, heroScale, onThemeCycle)
        }
        item {
            HomeControls(state, onHistoryToggle, onLayoutToggle, onCategorySelected)
        }
        item {
            HistorySection(state, onToolClick)
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
                    onFavorite = { onFavoriteToggle(tool.id) },
                    onClick = { onToolClick(tool) }
                )
            }
        }
    }
}

@Composable
private fun GridHomeScreen(
    state: HomeUiState,
    heroScale: Float,
    onThemeCycle: () -> Unit,
    onHistoryToggle: () -> Unit,
    onLayoutToggle: () -> Unit,
    onCategorySelected: (ToolCategory?) -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onToolClick: (ToolInfo) -> Unit
) {
    val groupedTools = remember(state.tools, state.orderedCategories) {
        state.orderedCategories.mapNotNull { category ->
            val tools = state.tools.filter { it.category == category }
            if (tools.isEmpty()) null else category to tools
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding =  PaddingValues(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(3) }) {
            HomeHeader(state, heroScale, onThemeCycle)
        }
        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(3) }) {
            HomeControls(state, onHistoryToggle, onLayoutToggle, onCategorySelected)
        }
        /*item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(3) }) {
            HistorySection(state, onToolClick)
        }*/
        groupedTools.forEach { (category, tools) ->
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(3) }) {
                FeatureSectionHeader(
                    title = category.title,
                    count = tools.size,
                    tint = state.categoryColors[category]?.toComposeColor() ?: MaterialTheme.colorScheme.primary,
                    showSeeAll = state.selectedCategory == null,
                    onSeeAll = { onCategorySelected(category) }
                )
            }
            items(tools, key = { it.id }) { tool ->
                FeatureGridCard(
                    tool = tool,
                    categoryColor = state.categoryColors[tool.category]?.toComposeColor() ?: MaterialTheme.colorScheme.primary,
                    onClick = { onToolClick(tool) }
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(state: HomeUiState, heroScale: Float, onThemeCycle: () -> Unit) {
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
            IconButton(onClick = onThemeCycle) {
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
@OptIn(ExperimentalLayoutApi::class)
private fun HomeControls(
    state: HomeUiState,
    onHistoryToggle: () -> Unit,
    onLayoutToggle: () -> Unit,
    onCategorySelected: (ToolCategory?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            /*Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onHistoryToggle),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.History, contentDescription = "History")
                    Text(
                        text = if (state.isHistoryVisible) "Hide history" else "History",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }*/
            Spacer(modifier = Modifier.weight(1f))

            Surface(
                modifier = Modifier.clickable(onClick = onLayoutToggle),
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

        /*FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CategoryChip(
                label = "All",
                tint = MaterialTheme.colorScheme.primary,
                selected = state.selectedCategory == null,
                onClick = { onCategorySelected(null) }
            )
            state.orderedCategories.forEach { category ->
                CategoryChip(
                    label = category.title,
                    tint = state.categoryColors[category]?.toComposeColor() ?: MaterialTheme.colorScheme.primary,
                    selected = state.selectedCategory == category,
                    onClick = { onCategorySelected(category) }
                )
            }
        }*/
    }
}

@Composable
private fun HistorySection(state: HomeUiState, onToolClick: (ToolInfo) -> Unit) {
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
                                .clickable { onToolClick(tool) },
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                DateTimeToolIcon(
                                    tool = tool,
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


@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
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
private fun FeatureSectionHeader(
    title: String,
    count: Int,
    tint: Color,
    showSeeAll: Boolean,
    onSeeAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(50))
                .background(tint)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "($count)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.weight(1f))
        if (showSeeAll) {
            Text(
                text = "See all",
                modifier = Modifier.clickable(onClick = onSeeAll),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun FeatureGridCard(
    tool: ToolInfo,
    categoryColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DateTimeToolIcon(tool, categoryColor)
            Spacer(Modifier.height(10.dp))
            Text(
                text = tool.title,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DateTimeToolIcon(tool: ToolInfo, tint: Color) {
    val icon = when (tool.id) {
        "analog_clock" -> Icons.Default.AccessTime
        "time_zone_converter" -> Icons.Default.SwapHoriz
        "calendar" -> Icons.Default.CalendarToday
        "stopwatch" -> Icons.Default.Timer
        "timer" -> Icons.Default.HourglassEmpty
        "pomodoro" -> Icons.Default.PlayArrow
        "tally" -> Icons.Default.PlusOne
        "invoice_generator" -> Icons.AutoMirrored.Filled.ReceiptLong
        "interest_calculator" -> Icons.Default.AccountBalance
        "mutual_fund" -> Icons.AutoMirrored.Filled.TrendingUp
        "emi" -> Icons.Default.CreditCard
        "gst_calculator" -> Icons.Default.Receipt
        "fd_rd_calculator" -> Icons.Default.Savings
        "expense_tracker" -> Icons.Default.MonetizationOn
        "gold_silver_rates" -> Icons.Default.AutoGraph
        else -> tool.icon()
    }
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(tint.copy(alpha = 0.16f)),
        contentAlignment = Alignment.Center
    ) {
        if (tool.id == "digital_clock") {
            Text(
                text = "123",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = tint
            )
        } else {
            Icon(icon, contentDescription = null, tint = tint)
        }
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
            DateTimeToolIcon(tool = tool, tint = categoryColor)
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(tool.title, style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp))
                Spacer(Modifier.height(4.dp))
                Text(
                    tool.subtitle,
                    style = MaterialTheme.typography.bodySmall,
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
    ToolCategory.DATE_TIME -> Icons.Default.Timer
    ToolCategory.CALCULATORS -> Icons.Default.Apps
    ToolCategory.FINANCE -> Icons.Default.Payments
    ToolCategory.ESSENTIAL -> Icons.Default.Bolt
    ToolCategory.MEASUREMENT -> Icons.Default.Devices
    ToolCategory.COMMON -> Icons.Default.Apps
    ToolCategory.TEXT_TOOLS -> Icons.Default.Code
    ToolCategory.MEDIA -> Icons.Default.Apps
    ToolCategory.DEVICE_INFO -> Icons.Default.Devices
    ToolCategory.GAMES -> Icons.Default.Apps
    ToolCategory.HEALTH -> Icons.Default.Favorite
    ToolCategory.SOCIAL_WEB -> Icons.Default.Apps
    ToolCategory.AI_TOOLS -> Icons.Default.Bolt
    ToolCategory.DEVELOPER -> Icons.Default.Code
}

private fun String.toComposeColor(): Color = Color(android.graphics.Color.parseColor(this))

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    OfflineToolboxTheme {
        HomeScreenContent(
            state = HomeUiState(
                tools = ToolCatalog.all.take(6),
                recentTools = ToolCatalog.all.take(2),
                isHistoryVisible = true
            ),
            onThemeCycle = {},
            onHistoryToggle = {},
            onLayoutToggle = {},
            onCategorySelected = {},
            onFavoriteToggle = {},
            onToolClick = {}
        )
    }
}
