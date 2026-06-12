package com.puneeth450.offlinetoolbox.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.puneeth450.offlinetoolbox.app.feature.datetime.analog.AnalogClockScreen
import com.puneeth450.offlinetoolbox.app.feature.datetime.digital.DigitalClockScreen
import com.puneeth450.offlinetoolbox.app.feature.datetime.timezone.TimeZoneConverterScreen
import com.puneeth450.offlinetoolbox.app.feature.developer.color.ColorConverterScreen
import com.puneeth450.offlinetoolbox.app.feature.developer.hash.HashGeneratorScreen
import com.puneeth450.offlinetoolbox.app.feature.developer.json.JsonFormatterScreen
import com.puneeth450.offlinetoolbox.app.feature.developer.lorem.LoremGeneratorScreen
import com.puneeth450.offlinetoolbox.app.feature.developer.url.UrlCodecScreen
import com.puneeth450.offlinetoolbox.app.feature.device.flashlight.FlashlightScreen
import com.puneeth450.offlinetoolbox.app.feature.device.info.DeviceInfoScreen
import com.puneeth450.offlinetoolbox.app.feature.device.unit.UnitConverterScreen
import com.puneeth450.offlinetoolbox.app.feature.finance.discount.DiscountCalculatorScreen
import com.puneeth450.offlinetoolbox.app.feature.finance.emi.EmiCalculatorScreen
import com.puneeth450.offlinetoolbox.app.feature.finance.rule72.Rule72CalculatorScreen
import com.puneeth450.offlinetoolbox.app.feature.finance.split.SplitBillCalculatorScreen
import com.puneeth450.offlinetoolbox.app.feature.home.FavoritesScreen
import com.puneeth450.offlinetoolbox.app.feature.home.HomeScreen
import com.puneeth450.offlinetoolbox.app.feature.home.CategoryColorsScreen
import com.puneeth450.offlinetoolbox.app.feature.home.LayoutArrangementScreen
import com.puneeth450.offlinetoolbox.app.feature.home.SettingsScreen
import com.puneeth450.offlinetoolbox.app.feature.home.UpdatesScreen
import com.puneeth450.offlinetoolbox.app.feature.productivity.breathing.BreathingPacerScreen
import com.puneeth450.offlinetoolbox.app.feature.productivity.pomodoro.PomodoroTimerScreen
import com.puneeth450.offlinetoolbox.app.feature.productivity.stopwatch.StopwatchTimerScreen
import com.puneeth450.offlinetoolbox.app.feature.productivity.tally.TallyCounterScreen
import com.puneeth450.offlinetoolbox.app.ui.components.CommonTopBar

private data class BottomDestination(val route: String, val label: String, val icon: ImageVector)

@Composable
fun OfflineToolboxNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val bottomRoutes = listOf(
        BottomDestination(Routes.HOME, "Home", Icons.Default.Home),
        BottomDestination(Routes.FAVORITES, "Favorites", Icons.Default.Favorite),
        BottomDestination(Routes.UPDATES, "Updates", Icons.Default.Notifications),
        BottomDestination(Routes.SETTINGS, "Settings", Icons.Default.Settings)
    )
    val showBottomBar = currentRoute in bottomRoutes.map { it.route }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomRoutes.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(Routes.HOME) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(destination.icon, contentDescription = destination.label) },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding),
            color = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            NavHost(
                navController = navController,
                startDestination = Routes.HOME
            ) {
                composable(Routes.HOME) { HomeScreen(navController) }
                composable(Routes.FAVORITES) { FavoritesScreen(navController) }
                composable(Routes.UPDATES) { UpdatesScreen() }
                composable(Routes.SETTINGS) { SettingsScreen(navController) }
                composable(Routes.SETTINGS_LAYOUT) { LayoutArrangementScreen(onNavigateBack = navController::navigateUp) }
                composable(Routes.SETTINGS_CATEGORY_COLORS) { CategoryColorsScreen(onNavigateBack = navController::navigateUp) }
                composable(Routes.ANALOG_CLOCK) { AnalogClockScreen(onNavigateBack = navController::navigateUp) }
                composable(Routes.DIGITAL_CLOCK) { DigitalClockScreen(onNavigateBack = navController::navigateUp) }
                composable(Routes.TIME_ZONE_CONVERTER) { TimeZoneConverterScreen(onNavigateBack = navController::navigateUp) }
                composable(Routes.CALENDAR) { ComingSoonToolScreen("Calendar", navController::navigateUp) }
                composable(Routes.POMODORO) { PomodoroTimerScreen(onNavigateBack = navController::navigateUp) }
                composable(Routes.STOPWATCH_TIMER) { StopwatchTimerScreen(onNavigateBack = navController::navigateUp) }
                composable(Routes.BREATHING) { BreathingPacerScreen(onNavigateBack = navController::navigateUp) }
                composable(Routes.TALLY) { TallyCounterScreen(onNavigateBack = navController::navigateUp) }
                composable(Routes.EMI) { EmiCalculatorScreen(onNavigateBack = navController::navigateUp) }
                composable(Routes.SPLIT_BILL) { SplitBillCalculatorScreen(onNavigateBack = navController::navigateUp) }
                composable(Routes.DISCOUNT) { DiscountCalculatorScreen(onNavigateBack = navController::navigateUp) }
                composable(Routes.RULE72) { Rule72CalculatorScreen(onNavigateBack = navController::navigateUp) }
                composable(Routes.JSON) { JsonFormatterScreen(onNavigateBack = navController::navigateUp) }
                composable(Routes.URL_CODEC) { UrlCodecScreen(onNavigateBack = navController::navigateUp) }
                composable(Routes.HASH) { HashGeneratorScreen(onNavigateBack = navController::navigateUp) }
                composable(Routes.LOREM) { LoremGeneratorScreen(onNavigateBack = navController::navigateUp) }
                composable(Routes.COLOR) { ColorConverterScreen(onNavigateBack = navController::navigateUp) }
                composable(Routes.FLASHLIGHT) { FlashlightScreen(onNavigateBack = navController::navigateUp) }
                composable(Routes.DEVICE_INFO) { DeviceInfoScreen(onNavigateBack = navController::navigateUp) }
                composable(Routes.UNIT_CONVERTER) { UnitConverterScreen(onNavigateBack = navController::navigateUp) }
            }
        }
    }
}

@Composable
private fun ComingSoonToolScreen(title: String, onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.Start
    ) {
        CommonTopBar(title = title, onNavigateBack = onNavigateBack)
        Text(
            "This tool is being prepared for an upcoming update.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
