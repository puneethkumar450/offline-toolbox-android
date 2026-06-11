package com.puneeth450.offlinetoolbox.app.feature.developer.url

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
fun UrlCodecScreen(
    onNavigateBack: () -> Unit,
    viewModel: UrlCodecViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    ToolScaffold(title = "URL Encoder/Decoder", onNavigateBack = onNavigateBack) {
        ToolHeroCard(
            title = "Prepare safe URL text",
            description = "Encode query text for requests or decode existing URL strings back into readable text."
        )
        ToolSectionCard(title = "Text input", subtitle = "Useful for URLs, query params, redirect links, and webhook payloads.") {
            OutlinedTextField(value = state.input, onValueChange = viewModel::onInput, label = { Text("URL text") })
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = { viewModel.setMode(UrlCodecMode.ENCODE) }) { Text("Encode") }
                Button(onClick = { viewModel.setMode(UrlCodecMode.DECODE) }) { Text("Decode") }
                Button(onClick = viewModel::runPrimary) { Text("Run") }
            }
        }
        state.error?.let { ToolMessageCard(message = it, isError = true) }
        if (state.output.isNotBlank()) ResultCard("Output", state.output)
    }
}
