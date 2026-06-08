package com.puneeth450.offlinetoolbox.app.domain.productivity

object TimerEngine {
    fun elapsedMillis(startedAt: Long, now: Long = System.currentTimeMillis(), accumulated: Long = 0L): Long =
        (now - startedAt).coerceAtLeast(0L) + accumulated

    fun remainingMillis(durationMillis: Long, startedAt: Long, now: Long = System.currentTimeMillis(), accumulated: Long = 0L): Long =
        (durationMillis - elapsedMillis(startedAt, now, accumulated)).coerceAtLeast(0L)
}

enum class TimerStatus { IDLE, RUNNING, PAUSED, FINISHED }
