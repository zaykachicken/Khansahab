package com.example.ui.util

import android.content.Context
import android.media.AudioManager
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

object OrderAlertSoundManager {
    private const val TAG = "OrderAlertSoundManager"
    private var isPlaying = false
    private var soundJob: Job? = null
    private var toneGen: ToneGenerator? = null
    private var systemRingtone: Ringtone? = null

    init {
        try {
            toneGen = ToneGenerator(AudioManager.STREAM_ALARM, 100)
        } catch (e: Exception) {
            try {
                toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 90)
            } catch (ex: Exception) {
                Log.e(TAG, "Failed to init ToneGenerator", ex)
            }
        }
    }

    fun startRinging(context: Context, scope: CoroutineScope) {
        if (isPlaying) return
        isPlaying = true
        Log.d(TAG, "Starting order alert sound loop")

        soundJob?.cancel()
        soundJob = scope.launch(Dispatchers.Default) {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            while (isActive && isPlaying) {
                try {
                    // Play loud distinctive restaurant order chime sequence (double beep chime)
                    toneGen?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 350)
                } catch (e: Exception) {
                    // Fallback to system notification sound
                    try {
                        if (systemRingtone == null) {
                            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                            systemRingtone = RingtoneManager.getRingtone(context.applicationContext, uri)
                        }
                        systemRingtone?.play()
                    } catch (ex: Exception) {
                        Log.e(TAG, "Fallback sound error", ex)
                    }
                }

                // Vibrate device
                try {
                    if (vibrator?.hasVibrator() == true) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(
                                VibrationEffect.createWaveform(
                                    longArrayOf(0, 200, 100, 200),
                                    -1
                                )
                            )
                        } else {
                            @Suppress("DEPRECATION")
                            vibrator.vibrate(longArrayOf(0, 200, 100, 200), -1)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Vibration error", e)
                }

                // Delay between rings (1.8 seconds)
                delay(1800)
            }
        }
    }

    fun stopRinging() {
        if (!isPlaying) return
        Log.d(TAG, "Stopping order alert sound loop")
        isPlaying = false
        soundJob?.cancel()
        soundJob = null
        try {
            toneGen?.stopTone()
            systemRingtone?.stop()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping sound", e)
        }
    }

    fun playSingleBeep(context: Context) {
        try {
            toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP2, 250)
        } catch (e: Exception) {
            try {
                val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                RingtoneManager.getRingtone(context.applicationContext, uri)?.play()
            } catch (ex: Exception) {
                Log.e(TAG, "Single beep error", ex)
            }
        }
    }
}
