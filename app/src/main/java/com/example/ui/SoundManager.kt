package com.example.ui

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log

object SoundManager {
    private var toneGenerator: ToneGenerator? = null
    var isSoundEnabled: Boolean = true
    
    var isMusicEnabled: Boolean = true
        set(value) {
            field = value
            if (value) {
                try {
                    BgmPlayer.start()
                } catch (t: Throwable) {
                    Log.e("SoundManager", "Failed to start BgmPlayer safely: ${t.message}")
                }
            } else {
                try {
                    BgmPlayer.stop()
                } catch (t: Throwable) {
                    Log.e("SoundManager", "Failed to stop BgmPlayer safely: ${t.message}")
                }
            }
        }

    init {
        // Lazily initialize ToneGenerator only when sound effects are played to prevent early appops checks on headless hosts
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
                } catch (t: Throwable) {
                    Log.e("SoundManager", "Failed to initialize ToneGenerator on stream $stream: ${t.message}")
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

/**
 * Procedural low-overhead Background Music synthesizer using AudioTrack
 * Runs entirely on a lightweight background thread without blocking UI.
 */
object BgmPlayer {
    private var audioTrack: android.media.AudioTrack? = null
    private var isPlaying = false
    private var playThread: Thread? = null
    private const val sampleRate = 22050

    fun start() {
        if (isPlaying) return
        isPlaying = true
        playThread = Thread {
            try {
                val minBufferSize = android.media.AudioTrack.getMinBufferSize(
                    sampleRate,
                    android.media.AudioFormat.CHANNEL_OUT_MONO,
                    android.media.AudioFormat.ENCODING_PCM_16BIT
                )

                var track: android.media.AudioTrack? = null
                
                // Safe block to accommodate all possible Android configurations
                try {
                    val attributes = android.media.AudioAttributes.Builder()
                        .setUsage(android.media.AudioAttributes.USAGE_GAME)
                        .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()

                    val format = android.media.AudioFormat.Builder()
                        .setSampleRate(sampleRate)
                        .setChannelMask(android.media.AudioFormat.CHANNEL_OUT_MONO)
                        .setEncoding(android.media.AudioFormat.ENCODING_PCM_16BIT)
                        .build()

                    track = android.media.AudioTrack.Builder()
                        .setAudioAttributes(attributes)
                        .setAudioFormat(format)
                        .setBufferSizeInBytes(minBufferSize)
                        .setTransferMode(android.media.AudioTrack.MODE_STREAM)
                        .build()
                } catch (t: Throwable) {
                    Log.e("BgmPlayer", "Standard AudioTrack.Builder failed, trying legacy constructor: ${t.message}")
                    try {
                        @Suppress("DEPRECATION")
                        track = android.media.AudioTrack(
                            android.media.AudioManager.STREAM_MUSIC,
                            sampleRate,
                            android.media.AudioFormat.CHANNEL_OUT_MONO,
                            android.media.AudioFormat.ENCODING_PCM_16BIT,
                            minBufferSize,
                            android.media.AudioTrack.MODE_STREAM
                        )
                    } catch (ex: Throwable) {
                        Log.e("BgmPlayer", "Legacy AudioTrack constructor also failed: ${ex.message}")
                    }
                }

                if (track == null) {
                    isPlaying = false
                    return@Thread
                }

                audioTrack = track
                track.play()

                // Soft bell pad frequencies corresponding to major chords
                val scale = doubleArrayOf(329.63, 369.99, 415.30, 493.88, 554.37, 659.25) // E4, F#4, G#4, B4, C#5, E5
                val chordProgression = arrayOf(
                    intArrayOf(0, 2, 4), // E Major chord pad
                    intArrayOf(1, 3, 5), // F# minor chord pad
                    intArrayOf(2, 4, 0), // Harmonic root shift
                    intArrayOf(3, 1, 4)  // Calming transition resolution
                )

                var chordIdx = 0
                val shortBuffer = ShortArray(1024)

                while (isPlaying) {
                    val currentChord = chordProgression[chordIdx]
                    chordIdx = (chordIdx + 1) % chordProgression.size

                    // Synthesize a series of overlapping bells
                    for (noteOffset in 0 until 4) {
                        if (!isPlaying) break

                        val freq = scale[currentChord[noteOffset % currentChord.size]]
                        val noteDurationSec = 1.6
                        val totalSamples = (sampleRate * noteDurationSec).toInt()

                        var phase = 0.0
                        val phaseIncrement = 2.0 * Math.PI * freq / sampleRate

                        var samplesProcessed = 0
                        while (samplesProcessed < totalSamples && isPlaying) {
                            val chunkSize = minOf(shortBuffer.size, totalSamples - samplesProcessed)
                            for (i in 0 until chunkSize) {
                                val t = (samplesProcessed + i).toDouble() / sampleRate
                                // ADSR like smooth envelope curve
                                val attackTime = 0.12
                                val decayTime = 1.48
                                val envelope = when {
                                    t < attackTime -> t / attackTime
                                    else -> Math.exp(-3.0 * (t - attackTime) / decayTime)
                                }

                                val sineWave = Math.sin(phase)
                                val subHarmonic = Math.sin(phase / 2.0) * 0.45 // Sub-octave warm body
                                val octaveOvertone = Math.sin(phase * 2.0) * 0.12 // Sparkling bell shimmer

                                val rawSample = (sineWave + subHarmonic + octaveOvertone) * envelope
                                val volume = 0.05 // Soft, pure background ambiance level
                                val sampleVal = (rawSample * volume * Short.MAX_VALUE).toInt()
                                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                                shortBuffer[i] = sampleVal.toShort()
                                
                                phase += phaseIncrement
                                if (phase > 2.0 * Math.PI) {
                                    phase -= 2.0 * Math.PI
                                }
                            }
                            try {
                                track.write(shortBuffer, 0, chunkSize)
                            } catch (ignored: Exception) {}
                            samplesProcessed += chunkSize
                        }
                        
                        // Small aesthetic rest gap between bell notes
                        Thread.sleep(200)
                    }

                    // Gentle breathing space rest between phrase layouts
                    var restMs = 1500
                    while (restMs > 0 && isPlaying) {
                        Thread.sleep(100)
                        restMs -= 100
                    }
                }

                try {
                    track.stop()
                    track.release()
                } catch (ignored: Exception) {}
            } catch (t: Throwable) {
                Log.e("BgmPlayer", "BgmPlayer worker thread caught error: ${t.message}")
            }
        }
        playThread?.name = "MSB-BgmPlayerSynthesizer"
        playThread?.start()
    }

    fun stop() {
        isPlaying = false
        playThread?.interrupt()
        playThread = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (ignored: Exception) {}
        audioTrack = null
    }
}
