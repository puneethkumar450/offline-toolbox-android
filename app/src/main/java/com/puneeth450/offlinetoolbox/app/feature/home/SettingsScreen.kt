package com.puneeth450.offlinetoolbox.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.puneeth450.offlinetoolbox.app.BuildConfig
import com.puneeth450.offlinetoolbox.app.data.repository.LayoutStyle
import com.puneeth450.offlinetoolbox.app.data.repository.ThemeMode
import com.puneeth450.offlinetoolbox.app.navigation.Routes
import com.puneeth450.offlinetoolbox.app.ui.components.CommonTopBar

@Composable
fun SettingsScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 28.dp)
    ) {
        CommonTopBar(
            title = "Settings",
            onNavigateBack = { navController.navigateUp() }
        )

        Spacer(Modifier.height(28.dp))

        SectionLabel("Layout Style")
        LayoutStyleRow(
            icon = Icons.Default.Dashboard,
            title = "Modern Layout",
            description = "Material 3 design with cards and collapsing toolbar",
            selected = state.layoutMode == HomeLayoutMode.LIST,
            onClick = { viewModel.setLayoutStyle(LayoutStyle.MODERN) }
        )
        LayoutStyleRow(
            icon = Icons.Default.GridView,
            title = "Classic Layout",
            description = "Grid-based layout similar to previous version",
            selected = state.layoutMode == HomeLayoutMode.GRID,
            onClick = { viewModel.setLayoutStyle(LayoutStyle.CLASSIC) }
        )

        SectionDivider()

        SectionLabel("Appearance")
        SettingsItem(
            icon = Icons.Default.DarkMode,
            title = "Theme"
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 28.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ThemeOption("Light", state.themeMode == ThemeMode.LIGHT) { viewModel.setThemeMode(ThemeMode.LIGHT) }
            ThemeOption("Dark", state.themeMode == ThemeMode.DARK) { viewModel.setThemeMode(ThemeMode.DARK) }
            ThemeOption("System", state.themeMode == ThemeMode.SYSTEM) { viewModel.setThemeMode(ThemeMode.SYSTEM) }
        }
        SettingsItem(
            icon = Icons.Default.Palette,
            title = "Dynamic Colors",
            description = "Use Material You wallpaper-based colors",
            trailing = {
                Switch(
                    checked = state.dynamicColors,
                    onCheckedChange = viewModel::setDynamicColors
                )
            }
        )
        SettingsItem(
            icon = Icons.Default.Palette,
            title = "Customize Category Colors",
            description = "Change accent colors for each category",
            onClick = { navController.navigate(Routes.SETTINGS_CATEGORY_COLORS) }
        )
        SettingsItem(
            icon = Icons.Default.Reorder,
            title = "Layout Arrangement",
            description = "Customize category order on home screen",
            onClick = { navController.navigate(Routes.SETTINGS_LAYOUT) }
        )

        SectionDivider()

        SectionLabel("Support")
        SettingsItem(
            icon = Icons.Default.Feedback,
            title = "Submit Feedback",
            description = "Share suggestions about flows, tools, or polish"
        )
        SettingsItem(
            icon = Icons.Default.BugReport,
            title = "Report Issue",
            description = "Flag bugs, broken calculations, or crashes"
        )

        SectionDivider()

        SectionLabel("About")
        SettingsItem(
            icon = Icons.Default.Info,
            title = "App Version",
            description = "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        )
        SettingsItem(
            icon = Icons.Default.Info,
            title = "About App",
            description = "ToolNest is an offline utilities app for productivity, finance, developer helpers, and device tools."
        )

        SectionDivider()

        SectionLabel("Legal")
        SettingsItem(
            icon = Icons.Default.PrivacyTip,
            title = "Privacy Policy",
            description = "Explains what stays on device and how local preferences are stored"
        )
        SettingsItem(
            icon = Icons.Default.Gavel,
            title = "Terms & Conditions",
            description = "Defines app usage expectations and feature limitations"
        )
    }
}

@Composable
private fun SectionLabel(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 14.dp)
    )
}

@Composable
private fun LayoutStyleRow(
    icon: ImageVector,
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(30.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 28.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        RadioButton(selected = selected, onClick = onClick)
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    description: String? = null,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(30.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 28.dp, end = 16.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        trailing?.invoke()
    }
}

@Composable
private fun ThemeOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(label, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun SectionDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 24.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.62f)
    )
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
