package com.puneeth450.offlinetoolbox.app.feature.developer.json

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneeth450.offlinetoolbox.app.ui.components.ResultCard
import com.puneeth450.offlinetoolbox.app.ui.components.ToolHeroCard
import com.puneeth450.offlinetoolbox.app.ui.components.ToolMessageCard
import com.puneeth450.offlinetoolbox.app.ui.components.ToolScaffold
import com.puneeth450.offlinetoolbox.app.ui.components.ToolSectionCard

@Composable
fun JsonFormatterScreen(
    onNavigateBack: () -> Unit,
    viewModel: JsonFormatterViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    ToolScaffold(title = "JSON Formatter", onNavigateBack = onNavigateBack) {
        ToolHeroCard(
            title = "Clean up payloads quickly",
            description = "Format or minify JSON for easier debugging, sharing, and API inspection."
        )
        ToolSectionCard(title = "JSON input", subtitle = "Paste an object, array, or API response body below.") {
            OutlinedTextField(value = state.input, onValueChange = viewModel::onInput, label = { Text("Paste JSON") })
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = { viewModel.setMode(JsonMode.FORMAT) }) { Text("Format") }
                Button(onClick = { viewModel.setMode(JsonMode.MINIFY) }) { Text("Minify") }
                Button(onClick = viewModel::runPrimary) { Text("Run") }
            }
        }
        state.error?.let { ToolMessageCard(message = it, isError = true) }
        if (state.output.isNotBlank()) ResultCard("Output", state.output)
    }
}
