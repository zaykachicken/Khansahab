package com.example.ui.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.Ringtone
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * High-reliability order receive alert sound manager for restaurant kitchen & admin panel.
 * Uses a synthesized dual-tone harmonic restaurant order bell via AudioTrack (STREAM_MUSIC),
 * combined with system notification ringtone, ToneGenerator, and tactile haptic vibration.
 */
object OrderAlertSoundManager {
    private const val TAG = "OrderAlertSoundManager"
    private const val SAMPLE_RATE = 44100

    @Volatile
    private var isPlaying = false
    private var soundJob: Job? = null
    private var currentAudioTrack: AudioTrack? = null
    private var toneGen: ToneGenerator? = null
    private var systemRingtone: Ringtone? = null

    // Pre-synthesized PCM buffer for rapid, zero-latency restaurant order bell chime
    private val alertPcmData: ByteArray by lazy {
        generateRestaurantOrderChime()
    }

    init {
        try {
            toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        } catch (e: Exception) {
            try {
                toneGen = ToneGenerator(AudioManager.STREAM_ALARM, 100)
            } catch (ex: Exception) {
                Log.e(TAG, "ToneGenerator init error", ex)
            }
        }
    }

    /**
     * Synthesizes a crisp, loud, restaurant order bell chime sequence:
     * Note 1 (880 Hz A5) -> Note 2 (1174 Hz D6) -> Note 3 (1568 Hz G6) -> Note 4 (1760 Hz A6 bell strike)
     * Followed by a second confirmation strike with resonant decay.
     */
    private fun generateRestaurantOrderChime(): ByteArray {
        val totalDurationSeconds = 1.3
        val totalSamples = (SAMPLE_RATE * totalDurationSeconds).toInt()
        val shortBuffer = ShortArray(totalSamples)

        // Synthesizes individual note with brass bell harmonics & exponential decay
        fun addNote(startSec: Double, durationSec: Double, freq1: Double, freq2: Double, volume: Double = 0.85) {
            val startSample = (startSec * SAMPLE_RATE).toInt()
            val numSamples = (durationSec * SAMPLE_RATE).toInt()
            val endSample = (startSample + numSamples).coerceAtMost(totalSamples)

            for (i in startSample until endSample) {
                val t = (i - startSample).toDouble() / SAMPLE_RATE
                // Exponential decay envelope (smooth bell ring)
                val envelope = exp(-t * 5.5) * volume
                // Harmonic rich bell frequency mixture
                val wave = 0.55 * sin(2.0 * PI * freq1 * t) +
                        0.30 * sin(2.0 * PI * freq2 * t) +
                        0.15 * sin(2.0 * PI * (freq1 * 2.0) * t)

                val sampleVal = (wave * envelope * Short.MAX_VALUE).toInt()
                val currentVal = shortBuffer[i].toInt()
                val mixed = (currentVal + sampleVal).coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                shortBuffer[i] = mixed.toShort()
            }
        }

        // Sequence 1: Rapid 3-tone ascent (alert pattern)
        addNote(startSec = 0.00, durationSec = 0.16, freq1 = 880.0, freq2 = 1760.0, volume = 0.80)
        addNote(startSec = 0.18, durationSec = 0.16, freq1 = 1174.66, freq2 = 2349.32, volume = 0.85)
        addNote(startSec = 0.36, durationSec = 0.35, freq1 = 1567.98, freq2 = 3135.96, volume = 0.95)

        // Sequence 2: High resonant confirmation chime
        addNote(startSec = 0.65, durationSec = 0.18, freq1 = 1318.51, freq2 = 2637.02, volume = 0.85)
        addNote(startSec = 0.85, durationSec = 0.45, freq1 = 1760.00, freq2 = 3520.00, volume = 1.00)

        // Convert short PCM to little-endian byte array
        val byteBuffer = ByteArray(totalSamples * 2)
        for (i in 0 until totalSamples) {
            val s = shortBuffer[i]
            byteBuffer[i * 2] = (s.toInt() and 0xFF).toByte()
            byteBuffer[i * 2 + 1] = ((s.toInt() shr 8) and 0xFF).toByte()
        }
        return byteBuffer
    }

    /**
     * Plays a single alert chime cycle across all available hardware channels.
     */
    fun playSingleBeep(context: Context) {
        try {
            playAudioTrackChime()
        } catch (e: Exception) {
            Log.e(TAG, "AudioTrack chime failed", e)
        }

        // Also trigger ToneGenerator backup
        try {
            toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP2, 350)
        } catch (e: Exception) {
            Log.e(TAG, "ToneGenerator play failed", e)
        }

        // Also trigger Ringtone fallback
        try {
            playSystemRingtone(context)
        } catch (e: Exception) {
            Log.e(TAG, "Ringtone fallback failed", e)
        }

        // Haptic feedback
        triggerVibration(context)
    }

    private fun playAudioTrackChime() {
        try {
            currentAudioTrack?.let {
                try {
                    it.stop()
                    it.release()
                } catch (_: Exception) { }
            }

            val pcmBytes = alertPcmData
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(pcmBytes.size)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(pcmBytes, 0, pcmBytes.size)
            track.play()
            currentAudioTrack = track
        } catch (e: Exception) {
            Log.e(TAG, "Failed creating/playing AudioTrack", e)
        }
    }

    private fun playSystemRingtone(context: Context) {
        try {
            val alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            if (systemRingtone == null) {
                systemRingtone = RingtoneManager.getRingtone(context.applicationContext, alertUri)
            }
            systemRingtone?.let { ringtone ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ringtone.audioAttributes = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                }
                ringtone.play()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing system ringtone", e)
        }
    }

    private fun triggerVibration(context: Context) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        VibrationEffect.createWaveform(
                            longArrayOf(0, 200, 100, 200, 100, 350),
                            -1
                        )
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(longArrayOf(0, 200, 100, 200, 100, 350), -1)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Vibrator trigger error", e)
        }
    }

    /**
     * Starts continuous ringing loop that repeats until stopRinging() is called
     * (when the restaurant admin accepts the order or mutes the alert).
     */
    fun startRinging(context: Context, scope: CoroutineScope) {
        if (isPlaying) return
        isPlaying = true
        Log.d(TAG, "OrderAlertSoundManager: Starting order alert ringing loop")

        soundJob?.cancel()
        soundJob = scope.launch(Dispatchers.Default) {
            while (isActive && isPlaying) {
                playSingleBeep(context)
                // Repeat chime every 1.8 seconds while new order is pending acceptance
                delay(1800)
            }
        }
    }

    /**
     * Stops the continuous ringing loop and releases audio resources immediately.
     */
    fun stopRinging() {
        if (!isPlaying) return
        Log.d(TAG, "OrderAlertSoundManager: Stopping order alert sound")
        isPlaying = false
        soundJob?.cancel()
        soundJob = null

        try {
            currentAudioTrack?.let {
                it.stop()
                it.release()
            }
            currentAudioTrack = null
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping AudioTrack", e)
        }

        try {
            toneGen?.stopTone()
            systemRingtone?.stop()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping Tone/Ringtone", e)
        }
    }

    fun isRinging(): Boolean = isPlaying
}
