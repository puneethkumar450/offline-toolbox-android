package com.puneeth450.offlinetoolbox.app.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.puneeth450.offlinetoolbox.app.BuildConfig
import com.puneeth450.offlinetoolbox.app.data.repository.ThemeMode
import com.puneeth450.offlinetoolbox.app.navigation.Routes

@Composable
fun SettingsScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)

        SettingsSection("Appearance") {
            SettingBlock("Theme", "Choose how ToolNest should look across the app.") {
                ThemeMode.entries.forEach { mode ->
                    SettingRadioRow(
                        title = mode.name.lowercase().replaceFirstChar(Char::titlecase),
                        selected = state.themeMode == mode,
                        onClick = { viewModel.setThemeMode(mode) }
                    )
                }
            }
            SettingBlock(
                "Layout Arrangement",
                "Decide the order in which categories appear on Home so your most important sections stay first.",
                onClick = { navController.navigate(Routes.SETTINGS_LAYOUT) }
            )
            SettingBlock(
                "Customize Category Colors",
                "Set a distinct accent for each category and reflect those colors back on the Home dashboard.",
                onClick = { navController.navigate(Routes.SETTINGS_CATEGORY_COLORS) }
            )
        }

        SettingsSection("Support") {
            SettingBlock(
                "Submit Feedback",
                "Share suggestions about flows, tools, or polish that would make the app more useful in daily use."
            )
            SettingBlock(
                "Report Issue",
                "Flag bugs, broken calculations, crashes, or anything that feels inconsistent so it can be fixed quickly."
            )
        }

        SettingsSection("About") {
            SettingBlock("App Version", "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
            SettingBlock(
                "About App",
                "ToolNest is built by Puneeth as a calm all-in-one utilities app for productivity, finance, developer helpers, and device tools. Social links can be added here next."
            )
        }

        SettingsSection("Legal") {
            SettingBlock(
                "Privacy Policy",
                "Explains what stays on device, what data is not collected, and how local preferences like favorites and history are stored."
            )
            SettingBlock(
                "Terms & Conditions",
                "Defines app usage expectations, feature limitations, and the informational nature of calculator and utility outputs."
            )
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            content()
        }
    }
}

@Composable
private fun SettingBlock(
    title: String,
    description: String,
    onClick: (() -> Unit)? = null,
    extra: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = if (onClick != null) Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp) else Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(
            description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        extra?.invoke()
    }
}

@Composable
private fun SettingRadioRow(title: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(title)
    }
}

@Composable
internal fun SimpleTabState(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
