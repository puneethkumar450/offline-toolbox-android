package com.puneeth450.offlinetoolbox.app.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UpdatesScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Updates", style = MaterialTheme.typography.headlineMedium)
        Text(
            "What changed in this build",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Fresh dashboard", style = MaterialTheme.typography.titleMedium)
                Text("Home now supports search, history visibility, and list or grid browsing.")
                Text("New device utilities", style = MaterialTheme.typography.titleMedium)
                Text("Device Information and Unit Converter are now available.")
                Text("Tool fixes", style = MaterialTheme.typography.titleMedium)
                Text("Productivity, finance, and developer tool flows are now functional end to end.")
            }
        }
    }
}

