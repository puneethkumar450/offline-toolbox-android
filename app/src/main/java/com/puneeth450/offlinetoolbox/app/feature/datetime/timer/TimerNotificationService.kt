package com.puneeth450.offlinetoolbox.app.feature.datetime.timer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.puneeth450.offlinetoolbox.app.MainActivity
import com.puneeth450.offlinetoolbox.app.R
import com.puneeth450.offlinetoolbox.app.data.repository.TimerRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.ceil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TimerNotificationService : Service() {
    @Inject
    lateinit var timerRepository: TimerRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var lastCompletionCount = 0
    private var lastShownSeconds = Long.MIN_VALUE

    override fun onCreate() {
        super.onCreate()
        createChannel()
        serviceScope.launch {
            timerRepository.uiState.collectLatest { state ->
                if (state.isRunning) {
                    val wholeSeconds = ceil(state.remainingMillis / 1000.0).toLong()
                    if (wholeSeconds != lastShownSeconds) {
                        lastShownSeconds = wholeSeconds
                        startForeground(NOTIFICATION_ID, buildRunningNotification(formatTimer(state.remainingMillis)))
                    }
                } else {
                    if (state.completionCount > lastCompletionCount) {
                        lastCompletionCount = state.completionCount
                        NotificationManagerCompat.from(this@TimerNotificationService)
                            .notify(COMPLETED_NOTIFICATION_ID, buildCompletedNotification())
                    }
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PAUSE -> timerRepository.pause()
            ACTION_STOP -> timerRepository.reset()
            ACTION_START -> Unit
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildRunningNotification(remainingText: String): Notification {
        val openAppIntent = PendingIntent.getActivity(
            this,
            100,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val pauseIntent = PendingIntent.getService(
            this,
            101,
            Intent(this, TimerNotificationService::class.java).setAction(ACTION_PAUSE),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val stopIntent = PendingIntent.getService(
            this,
            102,
            Intent(this, TimerNotificationService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Timer Running")
            .setContentText(remainingText)
            .setContentIntent(openAppIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .addAction(0, "Pause", pauseIntent)
            .addAction(0, "Stop", stopIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun buildCompletedNotification(): Notification {
        val openAppIntent = PendingIntent.getActivity(
            this,
            103,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Timer Finished")
            .setContentText("Your timer has ended.")
            .setContentIntent(openAppIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Smart Tools Timer",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Running timer notifications"
            }
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "smart_tools_timer"
        private const val NOTIFICATION_ID = 401
        private const val COMPLETED_NOTIFICATION_ID = 402
        private const val ACTION_START = "timer_start"
        private const val ACTION_PAUSE = "timer_pause"
        private const val ACTION_STOP = "timer_stop"

        fun start(context: Context) {
            val intent = Intent(context, TimerNotificationService::class.java).setAction(ACTION_START)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, TimerNotificationService::class.java))
        }
    }
}
