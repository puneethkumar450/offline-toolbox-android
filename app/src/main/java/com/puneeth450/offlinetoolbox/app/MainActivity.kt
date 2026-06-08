package com.puneeth450.offlinetoolbox.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.puneeth450.offlinetoolbox.app.data.repository.SettingsRepository
import com.puneeth450.offlinetoolbox.app.navigation.OfflineToolboxNavHost
import com.puneeth450.offlinetoolbox.app.ui.theme.DayBackground
import com.puneeth450.offlinetoolbox.app.ui.theme.NightBackground
import com.puneeth450.offlinetoolbox.app.ui.theme.OfflineToolboxTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val darkTheme by settingsRepository.isDarkTheme.collectAsStateWithLifecycle(initialValue = false)
            val systemBarColor = remember(darkTheme) {
                if (darkTheme) NightBackground.toArgb() else DayBackground.toArgb()
            }
            WindowCompat.getInsetsController(window, window.decorView).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
            window.statusBarColor = systemBarColor
            window.navigationBarColor = systemBarColor
            OfflineToolboxTheme(darkTheme = darkTheme) {
                OfflineToolboxNavHost()
            }
        }
    }
}
