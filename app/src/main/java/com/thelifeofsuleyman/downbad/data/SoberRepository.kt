package com.thelifeofsuleyman.downbad.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

private val Context.dataStore by preferencesDataStore(name = "sober_prefs")

class SoberRepository(private val context: Context) {
    private val START_TIME_KEY = longPreferencesKey("start_time_epoch")
    private val HABIT_NAME_KEY = stringPreferencesKey("habit_name")

    val startTimeFlow: Flow<LocalDateTime> = context.dataStore.data.map { prefs ->
        val epoch = prefs[START_TIME_KEY] ?: LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond()
        LocalDateTime.ofInstant(Instant.ofEpochSecond(epoch), ZoneId.systemDefault())
    }

    // THIS WAS MISSING:
    val habitNameFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[HABIT_NAME_KEY] ?: "sober"
    }

    suspend fun saveStartTime(dateTime: LocalDateTime) {
        context.dataStore.edit { prefs ->
            prefs[START_TIME_KEY] = dateTime.atZone(ZoneId.systemDefault()).toEpochSecond()
        }
    }

    // THIS WAS MISSING:
    suspend fun saveHabitName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[HABIT_NAME_KEY] = name
        }
    }
}