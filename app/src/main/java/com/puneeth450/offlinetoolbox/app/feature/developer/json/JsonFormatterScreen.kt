package com.puneeth450.offlinetoolbox.app.feature.developer.json

import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneeth450.offlinetoolbox.app.ui.components.ResultCard
import com.puneeth450.offlinetoolbox.app.ui.components.ToolScaffold

@Composable
fun JsonFormatterScreen(viewModel: JsonFormatterViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    ToolScaffold("JSON Formatter") {
        OutlinedTextField(value = state.input, onValueChange = viewModel::onInput, label = { Text("Input") })
        Button(onClick = viewModel::runPrimary) { Text("Run") }
        state.error?.let { Text(it) }
        if (state.output.isNotBlank()) ResultCard("Output", state.output)
    }
}
