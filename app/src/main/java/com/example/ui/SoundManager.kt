package com.example.ui

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log

object SoundManager {
    private var toneGenerator: ToneGenerator? = null
    var isSoundEnabled: Boolean = true

    init {
        ensureToneGenerator()
    }

    private fun ensureToneGenerator(): ToneGenerator? {
        if (toneGenerator == null) {
            // Try STREAM_MUSIC first, then fall back to STREAM_SYSTEM or STREAM_NOTIFICATION
            val streams = listOf(
                AudioManager.STREAM_MUSIC,
                AudioManager.STREAM_SYSTEM,
                AudioManager.STREAM_NOTIFICATION
            )
            for (stream in streams) {
                try {
                    toneGenerator = ToneGenerator(stream, 100)
                    if (toneGenerator != null) {
                        Log.d("SoundManager", "Successfully initialized ToneGenerator on stream $stream")
                        break
                    }
                } catch (e: Exception) {
                    Log.e("SoundManager", "Failed to initialize ToneGenerator on stream $stream: ${e.message}")
                }
            }
        }
        return toneGenerator
    }

    fun playCorrect() {
        if (!isSoundEnabled) return
        try {
            val generator = ensureToneGenerator()
            if (generator != null) {
                generator.startTone(ToneGenerator.TONE_CDMA_PIP, 100)
            } else {
                Log.e("SoundManager", "Cannot playCorrect: ToneGenerator is null")
            }
        } catch (e: Exception) {
            Log.e("SoundManager", "Correct tone playback failed: ${e.message}")
        }
    }

    fun playError() {
        if (!isSoundEnabled) return
        try {
            val generator = ensureToneGenerator()
            if (generator != null) {
                generator.startTone(ToneGenerator.TONE_CDMA_LOW_L, 200)
            } else {
                Log.e("SoundManager", "Cannot playError: ToneGenerator is null")
            }
        } catch (e: Exception) {
            Log.e("SoundManager", "Error tone playback failed: ${e.message}")
        }
    }

    fun playSuccess() {
        if (!isSoundEnabled) return
        try {
            val generator = ensureToneGenerator()
            if (generator != null) {
                Thread {
                    try {
                        generator.startTone(ToneGenerator.TONE_DTMF_3, 80)
                        Thread.sleep(100)
                        generator.startTone(ToneGenerator.TONE_DTMF_6, 80)
                        Thread.sleep(100)
                        generator.startTone(ToneGenerator.TONE_DTMF_9, 140)
                    } catch (ignored: Exception) {}
                }.start()
            } else {
                Log.e("SoundManager", "Cannot playSuccess: ToneGenerator is null")
            }
        } catch (e: Exception) {
            Log.e("SoundManager", "Success tone playback failed: ${e.message}")
        }
    }
}
