package com.puneeth450.offlinetoolbox.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.puneeth450.offlinetoolbox.app.feature.developer.color.ColorConverterScreen
import com.puneeth450.offlinetoolbox.app.feature.developer.hash.HashGeneratorScreen
import com.puneeth450.offlinetoolbox.app.feature.developer.json.JsonFormatterScreen
import com.puneeth450.offlinetoolbox.app.feature.developer.lorem.LoremGeneratorScreen
import com.puneeth450.offlinetoolbox.app.feature.developer.url.UrlCodecScreen
import com.puneeth450.offlinetoolbox.app.feature.device.info.DeviceInfoScreen
import com.puneeth450.offlinetoolbox.app.feature.device.unit.UnitConverterScreen
import com.puneeth450.offlinetoolbox.app.feature.finance.discount.DiscountCalculatorScreen
import com.puneeth450.offlinetoolbox.app.feature.finance.emi.EmiCalculatorScreen
import com.puneeth450.offlinetoolbox.app.feature.finance.rule72.Rule72CalculatorScreen
import com.puneeth450.offlinetoolbox.app.feature.finance.split.SplitBillCalculatorScreen
import com.puneeth450.offlinetoolbox.app.feature.home.HomeScreen
import com.puneeth450.offlinetoolbox.app.feature.productivity.breathing.BreathingPacerScreen
import com.puneeth450.offlinetoolbox.app.feature.productivity.pomodoro.PomodoroTimerScreen
import com.puneeth450.offlinetoolbox.app.feature.productivity.stopwatch.StopwatchTimerScreen
import com.puneeth450.offlinetoolbox.app.feature.productivity.tally.TallyCounterScreen

@Composable
fun OfflineToolboxNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) { HomeScreen(navController) }
        composable(Routes.POMODORO) { PomodoroTimerScreen() }
        composable(Routes.STOPWATCH_TIMER) { StopwatchTimerScreen() }
        composable(Routes.BREATHING) { BreathingPacerScreen() }
        composable(Routes.TALLY) { TallyCounterScreen() }
        composable(Routes.EMI) { EmiCalculatorScreen() }
        composable(Routes.SPLIT_BILL) { SplitBillCalculatorScreen() }
        composable(Routes.DISCOUNT) { DiscountCalculatorScreen() }
        composable(Routes.RULE72) { Rule72CalculatorScreen() }
        composable(Routes.JSON) { JsonFormatterScreen() }
        composable(Routes.URL_CODEC) { UrlCodecScreen() }
        composable(Routes.HASH) { HashGeneratorScreen() }
        composable(Routes.LOREM) { LoremGeneratorScreen() }
        composable(Routes.COLOR) { ColorConverterScreen() }
        composable(Routes.DEVICE_INFO) { DeviceInfoScreen() }
        composable(Routes.UNIT_CONVERTER) { UnitConverterScreen() }
    }
}
