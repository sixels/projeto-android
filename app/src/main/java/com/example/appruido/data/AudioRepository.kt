package com.example.appruido.data

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.log10
import kotlin.math.sqrt

object AudioRepository {
    private const val TAG = "RUIDO_AudioRepository"

    private const val SAMPLE_RATE = 22050
    private const val FIXED_CALIBRATION_OFFSET = 90.0


    private val _decibels = MutableStateFlow(0.0)
    val decibels: StateFlow<Double> = _decibels
    val isRecording: Boolean
        get() = recordJob != null


    private var _calibrationOffset = FIXED_CALIBRATION_OFFSET

    private var recordJob: Job? = null
    private var audioRecord: AudioRecord? = null

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun start() {
        if (recordJob != null) return

        val minBufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
        )

        val recorder = AudioRecord(
            MediaRecorder.AudioSource.UNPROCESSED,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            minBufferSize
        )

        if (recorder.state != AudioRecord.STATE_INITIALIZED) {
            throw IllegalStateException("AudioRecord initialization failed")
        }

        audioRecord = recorder
        recorder.startRecording()

        recordJob = CoroutineScope(Dispatchers.IO).launch {
            val buffer = ShortArray(minBufferSize)

            while (isActive) {
                val read = recorder.read(buffer, 0, buffer.size)
                if (read > 0) {
                    val rms = calculateRms(buffer, read)

                    // O valor máximo de um Short (16-bit) é 32767.0
                    // dBFS = 20 * log10(RMS / MAX_POSSIBLE_AMPLITUDE)
                    // O resultado será negativo (ex: -20 dBFS, -6 dBFS)
                    val dbFS = if (rms > 0) 20 * log10(rms / 32767.0) else -120.0

                    // Converter para SPL
                    var dbSPL = dbFS + _calibrationOffset

                    // Não mostrar valores negativos em silêncio absoluto
                    if (dbSPL < 0) dbSPL = 0.0

                    _decibels.value = dbSPL
                }
            }
        }
    }

    fun stop() {
        recordJob?.cancel()
        recordJob = null

        audioRecord?.apply {
            try {
                stop()
            } catch (_: Exception) {}
            release()
        }

        audioRecord = null
        _decibels.value = 0.0
    }

    fun adjustCalibration(value: Double) {
        _calibrationOffset = FIXED_CALIBRATION_OFFSET + value
    }

    private fun calculateRms(buffer: ShortArray, len: Int): Double {
        var sum = 0.0
        for (i in 0 until len) {
            val v = buffer[i].toDouble()
            sum += v * v
        }
        val mean = sum / len
        return sqrt(mean)
    }
}