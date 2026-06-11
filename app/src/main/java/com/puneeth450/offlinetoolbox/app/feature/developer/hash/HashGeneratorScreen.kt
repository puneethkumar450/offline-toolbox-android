package com.puneeth450.offlinetoolbox.app.feature.developer.hash

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneeth450.offlinetoolbox.app.domain.developer.HashAlgorithm
import com.puneeth450.offlinetoolbox.app.ui.components.ResultCard
import com.puneeth450.offlinetoolbox.app.ui.components.ToolHeroCard
import com.puneeth450.offlinetoolbox.app.ui.components.ToolMessageCard
import com.puneeth450.offlinetoolbox.app.ui.components.ToolScaffold
import com.puneeth450.offlinetoolbox.app.ui.components.ToolSectionCard

@Composable
fun HashGeneratorScreen(
    onNavigateBack: () -> Unit,
    viewModel: HashGeneratorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    ToolScaffold(title = "Hash Generator", onNavigateBack = onNavigateBack) {
        ToolHeroCard(
            title = "Create quick checksums",
            description = "Generate hashes for text verification, debugging, or lightweight integrity checks."
        )
        ToolSectionCard(title = "Hash input", subtitle = "Choose an algorithm, then generate the fingerprint for your text.") {
            OutlinedTextField(value = state.input, onValueChange = viewModel::onInput, label = { Text("Text to hash") })
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HashAlgorithm.entries.forEach { algorithm ->
                    Button(onClick = { viewModel.setAlgorithm(algorithm) }) {
                        Text(algorithm.label)
                    }
                }
            }
            Button(onClick = viewModel::runPrimary) { Text("Generate Hash") }
        }
        state.error?.let { ToolMessageCard(message = it, isError = true) }
        if (state.output.isNotBlank()) ResultCard(state.algorithm.label, state.output)
    }
}
