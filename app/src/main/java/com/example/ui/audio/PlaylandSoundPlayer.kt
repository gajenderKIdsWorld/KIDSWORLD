package com.example.ui.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object PlaylandSoundPlayer {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var activeJob: Job? = null

    fun playTone(frequencyHz: Double, durationMs: Int = 240, enabled: Boolean = true) {
        if (!enabled) return
        scope.launch {
            playSineToneBlocking(frequencyHz, durationMs)
        }
    }

    fun playNoteSequence(frequencies: List<Double>, enabled: Boolean = true, onStepHighlight: (Int) -> Unit) {
        activeJob?.cancel()
        activeJob = scope.launch {
            frequencies.forEachIndexed { index, freq ->
                onStepHighlight(index)
                if (enabled) {
                    playSineToneBlocking(freq, 280)
                } else {
                    delay(280)
                }
                onStepHighlight(-1)
                delay(140)
            }
        }
    }

    fun playSuccessChime(enabled: Boolean = true) {
        if (!enabled) return
        scope.launch {
            playSineToneBlocking(523.25, 140)
            playSineToneBlocking(659.25, 140)
            playSineToneBlocking(783.99, 260)
        }
    }

    fun playGentleBoop(enabled: Boolean = true) {
        if (!enabled) return
        scope.launch {
            playSineToneBlocking(293.66, 180)
        }
    }

    private fun playSineToneBlocking(frequencyHz: Double, durationMs: Int) {
        try {
            val sampleRate = 22050
            val count = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
            val samples = ShortArray(count)
            for (i in 0 until count) {
                val t = i.toDouble() / sampleRate
                val envelope = exp(-4.0 * (i.toDouble() / count))
                val wave = sin(2.0 * PI * frequencyHz * t) * envelope
                samples[i] = (wave * Short.MAX_VALUE * 0.45).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            val minBuf = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(samples.size * 2)

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBuf)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(samples, 0, samples.size)
            track.play()
            Thread.sleep(durationMs.toLong() + 30L)
            track.stop()
            track.release()
        } catch (_: Throwable) {
            // AudioTrack may be unavailable in headless Robolectric tests; ignore gracefully.
        }
    }
}
