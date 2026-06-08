package com.puneeth450.offlinetoolbox.app.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.puneeth450.offlinetoolbox.app.domain.model.ToolCatalog

@Composable
fun FavoritesScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val favorites = ToolCatalog.all.filter { it.id in state.favorites }

    if (favorites.isEmpty()) {
        SimpleTabState(
            title = "Favorites",
            subtitle = "Save the tools you use most so they stay one tap away."
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Favorites", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Your pinned tools",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        items(favorites, key = { it.id }) { tool ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.markRecent(tool.id)
                        navController.navigate(tool.route)
                    }
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(tool.title, style = MaterialTheme.typography.titleMedium)
                    Text(tool.subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

