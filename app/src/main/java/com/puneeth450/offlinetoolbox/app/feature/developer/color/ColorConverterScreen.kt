package com.puneeth450.offlinetoolbox.app.feature.developer.color

import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneeth450.offlinetoolbox.app.ui.components.ResultCard
import com.puneeth450.offlinetoolbox.app.ui.components.ToolHeroCard
import com.puneeth450.offlinetoolbox.app.ui.components.ToolMessageCard
import com.puneeth450.offlinetoolbox.app.ui.components.ToolScaffold
import com.puneeth450.offlinetoolbox.app.ui.components.ToolSectionCard

@Composable
fun ColorConverterScreen(
    onNavigateBack: () -> Unit,
    viewModel: ColorConverterViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    ToolScaffold(title = "Color Converter", onNavigateBack = onNavigateBack) {
        ToolHeroCard(
            title = "Translate color formats",
            description = "Convert HEX values into RGB and HSL for quick design or frontend work."
        )
        ToolSectionCard(title = "Color input", subtitle = "Enter a HEX value like #4D7CFE.") {
            OutlinedTextField(value = state.input, onValueChange = viewModel::onInput, label = { Text("HEX color") })
            Button(onClick = viewModel::runPrimary) { Text("Convert Color") }
        }
        state.error?.let { ToolMessageCard(message = it, isError = true) }
        if (state.output.isNotBlank()) ResultCard("Converted output", state.output)
    }
}
