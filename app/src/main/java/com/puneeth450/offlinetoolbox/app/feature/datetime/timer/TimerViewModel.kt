package com.puneeth450.offlinetoolbox.app.feature.datetime.timer

import android.content.Context
import androidx.lifecycle.ViewModel
import com.puneeth450.offlinetoolbox.app.data.repository.TimerRepository
import com.puneeth450.offlinetoolbox.app.data.repository.TimerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val timerRepository: TimerRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {
    val uiState: StateFlow<TimerUiState> = timerRepository.uiState

    fun setHours(value: Int) = timerRepository.setHours(value)
    fun setMinutes(value: Int) = timerRepository.setMinutes(value)
    fun setSeconds(value: Int) = timerRepository.setSeconds(value)
    fun applyPreset(totalMinutes: Int) = timerRepository.applyPreset(totalMinutes)

    fun startTimer(withNotification: Boolean) {
        timerRepository.start()
        if (withNotification) {
            TimerNotificationService.start(appContext)
        } else {
            TimerNotificationService.stop(appContext)
        }
    }

    fun pauseTimer() {
        timerRepository.pause()
        TimerNotificationService.stop(appContext)
    }

    fun resetTimer() {
        timerRepository.reset()
        TimerNotificationService.stop(appContext)
    }
}
