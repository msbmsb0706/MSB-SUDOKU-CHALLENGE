package com.example.ui

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log

object SoundManager {
    private var toneGenerator: ToneGenerator? = null
    var isSoundEnabled: Boolean = true

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
        } catch (e: Exception) {
            Log.e("SoundManager", "Failed to initialize ToneGenerator in constructor: ${e.message}")
        }
    }

    fun playCorrect() {
        if (!isSoundEnabled) return
        try {
            // TONE_PROP_ACK is a swift positive acknowledgement note
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 120)
        } catch (e: Exception) {
            Log.e("SoundManager", "Correct tone playback failed: ${e.message}")
        }
    }

    fun playError() {
        if (!isSoundEnabled) return
        try {
            // TONE_PROP_NACK is a short buzz-like tone representing incorrect answers or errors
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 220)
        } catch (e: Exception) {
            Log.e("SoundManager", "Error tone playback failed: ${e.message}")
        }
    }

    fun playSuccess() {
        if (!isSoundEnabled) return
        try {
            // Rapid succession of rising chords to represent final triumph
            Thread {
                try {
                    toneGenerator?.startTone(ToneGenerator.TONE_DTMF_3, 80)
                    Thread.sleep(100)
                    toneGenerator?.startTone(ToneGenerator.TONE_DTMF_6, 80)
                    Thread.sleep(100)
                    toneGenerator?.startTone(ToneGenerator.TONE_DTMF_9, 140)
                } catch (ignored: Exception) {}
            }.start()
        } catch (e: Exception) {
            Log.e("SoundManager", "Success tone playback failed: ${e.message}")
        }
    }
}
