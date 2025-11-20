package com.example.appruido.ui.screens

import android.R.attr.duration
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appruido.data.AudioRepository
import com.example.appruido.data.CriticalNoise
import com.example.appruido.data.CriticalNoiseRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.math.absoluteValue


class HomeScreenViewModel(
    val audioRepository: AudioRepository, val criticalNoiseRepository: CriticalNoiseRepository
) : ViewModel() {

    private val TAG: String = "HomeScreenViewModel"

    val userId = Firebase.auth.currentUser?.uid ?: ""

    @OptIn(FlowPreview::class)
    val decibels: StateFlow<Double> = audioRepository.decibels.sample(333).stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(), initialValue = 0.0
    )

    private val _history = MutableStateFlow<List<Double>>(emptyList())
    val history: StateFlow<List<Double>> = _history



    private val criticalNoiseState = CriticalNoiseState()


    init {
        // Every time decibels emits, update the history buffer
        viewModelScope.launch {
            val criticalHistory = criticalNoiseRepository.getAll(userId = userId).map { it.average }
            Log.d(TAG, "criticalHistory: $criticalHistory")

            decibels.collect { value ->
                var dbValue = value.absoluteValue
                if (dbValue.isInfinite()) {
                    dbValue = 0.0
                }

                // Update history for the chart
                val updated = _history.value.plus(dbValue).takeLast(25)
                _history.value = updated

                // High noise event detection logic
                handleNoiseEvent(dbValue)
            }
        }
    }

    private val _isRunning = MutableStateFlow(audioRepository.isRecording)
    val isRunning: StateFlow<Boolean> = _isRunning

    fun setIsRunning(value: Boolean) {
        _isRunning.value = value
    }

    private fun sendAudioCriticalEvent(average: Double, startedAt: Date) {
        viewModelScope.launch {
            val audioCritical = CriticalNoise(
                average = average, startedAt = startedAt, endedAt = Date(), // Event ends now
                userId = Firebase.auth.currentUser?.uid ?: ""
            )
            criticalNoiseRepository.insert(audioCritical)
        }
    }

    private fun handleNoiseEvent(dbValue: Double) {
        // CASE 2: Immediate trigger for Extreme noise (>= 100dB)
        if (dbValue >= 80) {
            Log.d(TAG, "noise enter critical: dbValue: $dbValue")
            // CASE 3 Start/Continue: Noise is High (>= 80dB), but not Extreme.

            criticalNoiseState.update(dbValue)
        } else { // dbValue < 80
            // Nível de barulho caiu.
            if (!criticalNoiseState.isCounting()) {
                return
            }

            val maxDb =criticalNoiseState.getMaxDb()
            val startedAt = criticalNoiseState.getStartedAt()!!


            Log.d(TAG, "noise leave critical: dbValue: $dbValue, duration: $duration")

            if (criticalNoiseState.ellapsedTime() >= 3000 || maxDb >= 100) {
                // The event was sustained for > 3 seconds. Send it.
                sendAudioCriticalEvent(maxDb, startedAt)
            }

            criticalNoiseState.reset()
        }
    }
}

private class CriticalNoiseState {
    private var maxDb: Double = 0.0
    private var startedAt: Date? = null
    private var startTime: Long? = null

    fun update(dbValue: Double) {
        if (startTime == null) {

            // This is the start of a potential event
            maxDb = dbValue
            startedAt = Date()
            startTime = System.currentTimeMillis()
        } else {
            // Event is ongoing, update the max value if needed
            if (dbValue > maxDb) {
                maxDb = dbValue
            }
        }
    }

    fun isCounting(): Boolean {
        return startTime != null
    }

    fun getStartedAt(): Date? {
        return startedAt
    }

    fun getMaxDb(): Double {
        return maxDb
    }

    fun ellapsedTime(): Long {
        val currentTime = System.currentTimeMillis()
        return startTime?.let { currentTime - it } ?: 0
    }


    fun reset() {
        maxDb = 0.0
        startedAt = null
        startTime = null
    }
}