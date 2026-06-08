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
import com.puneeth450.offlinetoolbox.app.ui.components.ToolScaffold

@Composable
fun UrlCodecScreen(viewModel: UrlCodecViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    ToolScaffold("URL Encoder/Decoder") {
        OutlinedTextField(value = state.input, onValueChange = viewModel::onInput, label = { Text("URL text") })
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = { viewModel.setMode(UrlCodecMode.ENCODE) }) { Text("Encode") }
            Button(onClick = { viewModel.setMode(UrlCodecMode.DECODE) }) { Text("Decode") }
            Button(onClick = viewModel::runPrimary) { Text("Run") }
        }
        state.error?.let { Text(it) }
        if (state.output.isNotBlank()) ResultCard("Output", state.output)
    }
}
