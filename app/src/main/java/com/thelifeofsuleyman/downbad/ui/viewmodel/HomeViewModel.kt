package com.thelifeofsuleyman.downbad.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Duration
import java.time.LocalDateTime

class HomeViewModel : ViewModel() {

    // In a real app, this comes from SharedPreferences.
    // For now, let's pretend the user started exactly 10h 12m 50s ago
    private var startTime = LocalDateTime.now()
        .minusHours(10)
        .minusMinutes(12)
        .minusSeconds(50)

    // A ticker that emits every second
    val timeTicker: Flow<TimeData> = flow {
        while (true) {
            val now = LocalDateTime.now()
            val duration = Duration.between(startTime, now)

            emit(TimeData(
                days = duration.toDays(),
                hours = duration.toHours() % 24,
                minutes = duration.toMinutes() % 60,
                seconds = duration.seconds % 60
            ))
            delay(1000) // Update every second
        }
    }

    fun resetTimer() {
        startTime = LocalDateTime.now()
    }
}

data class TimeData(
    val days: Long,
    val hours: Long,
    val minutes: Long,
    val seconds: Long
)