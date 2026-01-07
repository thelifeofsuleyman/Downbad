package com.thelifeofsuleyman.downbad.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.thelifeofsuleyman.downbad.data.SoberRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime

data class TimeData(
    val days: Long,
    val hours: Long,
    val minutes: Long,
    val seconds: Long
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SoberRepository(application)

    private val _startTime = repository.startTimeFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), LocalDateTime.now()
    )

    val habitName: StateFlow<String> = repository.habitNameFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), "sober"
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val timeTicker: Flow<TimeData> = _startTime.flatMapLatest { start ->
        flow {
            while (true) {
                val duration = Duration.between(start, LocalDateTime.now())
                val totalSeconds = if (duration.isNegative) 0L else duration.seconds

                emit(TimeData(
                    days = duration.toDays(),
                    hours = (totalSeconds / 3600) % 24,
                    minutes = (totalSeconds / 60) % 60,
                    seconds = totalSeconds % 60
                ))
                delay(1000)
            }
        }
    }

    fun resetToNow() {
        viewModelScope.launch { repository.saveStartTime(LocalDateTime.now()) }
    }

    fun updateStartTime(newDateTime: LocalDateTime) {
        viewModelScope.launch { repository.saveStartTime(newDateTime) }
    }

    fun updateHabitName(name: String) {
        viewModelScope.launch { repository.saveHabitName(name) }
    }
}