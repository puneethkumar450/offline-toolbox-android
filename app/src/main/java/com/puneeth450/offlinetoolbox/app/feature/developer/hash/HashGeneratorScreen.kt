package com.puneeth450.offlinetoolbox.app.feature.developer.hash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneeth450.offlinetoolbox.app.domain.developer.HashAlgorithm
import com.puneeth450.offlinetoolbox.app.ui.components.ResultCard
import com.puneeth450.offlinetoolbox.app.ui.components.ToolScaffold

@Composable
fun HashGeneratorScreen(
    onNavigateBack: () -> Unit,
    viewModel: HashGeneratorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    ToolScaffold(title = "Hash Generator", onNavigateBack = onNavigateBack) {
        OutlinedTextField(value = state.input, onValueChange = viewModel::onInput, label = { Text("Text to hash") })
        Row(
            modifier = androidx.compose.ui.Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HashAlgorithm.entries.forEach { algorithm ->
                Button(onClick = { viewModel.setAlgorithm(algorithm) }) {
                    Text(algorithm.label)
                }
            }
        }
        Button(onClick = viewModel::runPrimary) { Text("Generate") }
        state.error?.let { Text(it) }
        if (state.output.isNotBlank()) ResultCard(state.algorithm.label, state.output)
    }
}
