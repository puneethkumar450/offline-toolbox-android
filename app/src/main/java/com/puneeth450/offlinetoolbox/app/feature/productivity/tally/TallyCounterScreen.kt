package com.puneeth450.offlinetoolbox.app.feature.productivity.tally

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneeth450.offlinetoolbox.app.ui.components.ResultCard
import com.puneeth450.offlinetoolbox.app.ui.components.ToolScaffold

@Composable
fun TallyCounterScreen(viewModel: TallyCounterViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    ToolScaffold("Tally Counter") {
        ResultCard("Time", "${state.elapsedMillis / 1000} sec")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = viewModel::start) { Text("Start") }
            Button(onClick = viewModel::stop) { Text("Stop") }
            Button(onClick = viewModel::reset) { Text("Reset") }
        }
        ResultCard("Counter", state.count.toString())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = viewModel::decrement) { Text("-") }
            Button(onClick = viewModel::increment) { Text("+") }
        }
    }
}
