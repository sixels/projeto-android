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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.log10
import kotlin.math.sqrt

class AudioRepository(private val settingsRepository: SettingsRepository) {
    private val TAG = "RUIDO_AudioRepository"

    private val SAMPLE_RATE = 22050
    private val FIXED_CALIBRATION_OFFSET = 90.0

    private val _decibels = MutableStateFlow(0.0)
    val decibels: StateFlow<Double> = _decibels.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private var _calibrationOffset = FIXED_CALIBRATION_OFFSET

    private var recordJob: Job? = null
    private var audioRecord: AudioRecord? = null

    init {
        // Carrega a calibração inicial do banco de dados
        CoroutineScope(Dispatchers.IO).launch {
            val settings = settingsRepository.getSettings().first()
            settings?.let {
                adjustCalibration(it.calibration)
            }
        }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun start() {
        if (_isRecording.value) return

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
            _isRecording.value = false
            throw IllegalStateException("AudioRecord initialization failed")
        }

        audioRecord = recorder
        recorder.startRecording()
        _isRecording.value = true

        recordJob = CoroutineScope(Dispatchers.IO).launch {
            val buffer = ShortArray(minBufferSize)

            while (isActive) {
                val read = recorder.read(buffer, 0, buffer.size)
                if (read > 0) {
                    val rms = calculateRms(buffer, read)
                    val dbFS = if (rms > 0) 20 * log10(rms / 32767.0) else -120.0
                    var dbSPL = dbFS + _calibrationOffset

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
            } catch (_: Exception) {
            }
            release()
        }

        audioRecord = null
        _decibels.value = 0.0
        _isRecording.value = false
    }

    fun adjustCalibration(value: Double) {
        // CORREÇÃO: Define o valor em vez de somar
        _calibrationOffset += value
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
