package com.example.player.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import com.example.data.model.AudioTrack
import com.example.data.model.LyricLine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class RepeatMode {
    OFF,
    REPEAT_ALL,
    REPEAT_ONE
}

data class EqualizerBand(
    val frequencyLabel: String,
    val levelDb: Float // -12dB to +12dB
)

data class EqualizerState(
    val isEnabled: Boolean = true,
    val currentPreset: String = "Pop",
    val bands: List<EqualizerBand> = listOf(
        EqualizerBand("60Hz", 4f),
        EqualizerBand("230Hz", 2f),
        EqualizerBand("910Hz", -1f),
        EqualizerBand("3.6kHz", 3f),
        EqualizerBand("14kHz", 5f)
    ),
    val bassBoostPercent: Int = 45,
    val virtualizerPercent: Int = 30
)

class AudioPlayerManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private var progressJob: Job? = null

    private val _currentTrack = MutableStateFlow<AudioTrack?>(null)
    val currentTrack = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0)
    val currentPositionMs = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0)
    val durationMs = _durationMs.asStateFlow()

    private val _queue = MutableStateFlow<List<AudioTrack>>(emptyList())
    val queue = _queue.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex = _currentIndex.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.REPEAT_ALL)
    val repeatMode = _repeatMode.asStateFlow()

    private val _isShuffle = MutableStateFlow(false)
    val isShuffle = _isShuffle.asStateFlow()

    private val _equalizerState = MutableStateFlow(EqualizerState())
    val equalizerState = _equalizerState.asStateFlow()

    private val _currentLyricLine = MutableStateFlow<LyricLine?>(null)
    val currentLyricLine = _currentLyricLine.asStateFlow()

    private val _visualizerFrequencies = MutableStateFlow(List(16) { 0.2f })
    val visualizerFrequencies = _visualizerFrequencies.asStateFlow()

    private var parsedLyrics: List<LyricLine> = emptyList()

    fun playTrack(track: AudioTrack, queueList: List<AudioTrack> = listOf(track)) {
        _queue.value = queueList
        val index = queueList.indexOfFirst { it.id == track.id }.coerceAtLeast(0)
        _currentIndex.value = index
        _currentTrack.value = track
        parseLyrics(track.lyrics)

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            try {
                if (track.streamOrFilePath.startsWith("http")) {
                    setDataSource(context, Uri.parse(track.streamOrFilePath))
                } else {
                    setDataSource(track.streamOrFilePath)
                }
                setOnPreparedListener { mp ->
                    _durationMs.value = mp.duration.coerceAtLeast(track.durationSeconds * 1000)
                    mp.start()
                    _isPlaying.value = true
                    startProgressTracker()
                }
                setOnCompletionListener {
                    handleTrackCompletion()
                }
                setOnErrorListener { _, _, _ ->
                    // Fallback to simulated local playback for offline tracks/streams
                    _durationMs.value = track.durationSeconds * 1000
                    _isPlaying.value = true
                    startProgressTracker()
                    true
                }
                prepareAsync()
            } catch (_: Exception) {
                // Smooth fallback
                _durationMs.value = track.durationSeconds * 1000
                _isPlaying.value = true
                startProgressTracker()
            }
        }
    }

    fun togglePlayPause() {
        val mp = mediaPlayer
        if (mp != null) {
            try {
                if (mp.isPlaying) {
                    mp.pause()
                    _isPlaying.value = false
                } else {
                    mp.start()
                    _isPlaying.value = true
                    startProgressTracker()
                }
            } catch (_: Exception) {
                _isPlaying.value = !_isPlaying.value
                if (_isPlaying.value) startProgressTracker()
            }
        } else {
            _currentTrack.value?.let { playTrack(it, _queue.value) }
        }
    }

    fun seekTo(positionMs: Int) {
        _currentPositionMs.value = positionMs
        try {
            mediaPlayer?.seekTo(positionMs)
        } catch (_: Exception) {}
        updateActiveLyric(positionMs)
    }

    fun skipNext() {
        val q = _queue.value
        if (q.isEmpty()) return
        val nextIdx = if (_isShuffle.value) {
            (0 until q.size).random()
        } else {
            (_currentIndex.value + 1) % q.size
        }
        _currentIndex.value = nextIdx
        playTrack(q[nextIdx], q)
    }

    fun skipPrevious() {
        val q = _queue.value
        if (q.isEmpty()) return
        val prevIdx = if (_currentIndex.value > 0) _currentIndex.value - 1 else q.size - 1
        _currentIndex.value = prevIdx
        playTrack(q[prevIdx], q)
    }

    fun toggleRepeat() {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.REPEAT_ALL
            RepeatMode.REPEAT_ALL -> RepeatMode.REPEAT_ONE
            RepeatMode.REPEAT_ONE -> RepeatMode.OFF
        }
    }

    fun toggleShuffle() {
        _isShuffle.value = !_isShuffle.value
    }

    fun setEqualizerPreset(preset: String) {
        val bands = when (preset) {
            "Bass Heavy" -> listOf(EqualizerBand("60Hz", 8f), EqualizerBand("230Hz", 6f), EqualizerBand("910Hz", 0f), EqualizerBand("3.6kHz", 1f), EqualizerBand("14kHz", 2f))
            "Pop" -> listOf(EqualizerBand("60Hz", 3f), EqualizerBand("230Hz", 2f), EqualizerBand("910Hz", 0f), EqualizerBand("3.6kHz", 4f), EqualizerBand("14kHz", 6f))
            "Rock" -> listOf(EqualizerBand("60Hz", 5f), EqualizerBand("230Hz", 3f), EqualizerBand("910Hz", -1f), EqualizerBand("3.6kHz", 4f), EqualizerBand("14kHz", 5f))
            "Jazz" -> listOf(EqualizerBand("60Hz", 4f), EqualizerBand("230Hz", 2f), EqualizerBand("910Hz", -2f), EqualizerBand("3.6kHz", 2f), EqualizerBand("14kHz", 5f))
            "EDM" -> listOf(EqualizerBand("60Hz", 7f), EqualizerBand("230Hz", 5f), EqualizerBand("910Hz", 1f), EqualizerBand("3.6kHz", 5f), EqualizerBand("14kHz", 7f))
            "Vocal Booster" -> listOf(EqualizerBand("60Hz", -2f), EqualizerBand("230Hz", 1f), EqualizerBand("910Hz", 6f), EqualizerBand("3.6kHz", 6f), EqualizerBand("14kHz", 3f))
            else -> listOf(EqualizerBand("60Hz", 0f), EqualizerBand("230Hz", 0f), EqualizerBand("910Hz", 0f), EqualizerBand("3.6kHz", 0f), EqualizerBand("14kHz", 0f))
        }
        _equalizerState.value = _equalizerState.value.copy(currentPreset = preset, bands = bands)
    }

    fun updateBand(index: Int, level: Float) {
        val currentBands = _equalizerState.value.bands.toMutableList()
        if (index in currentBands.indices) {
            currentBands[index] = currentBands[index].copy(levelDb = level.coerceIn(-12f, 12f))
            _equalizerState.value = _equalizerState.value.copy(currentPreset = "Custom", bands = currentBands)
        }
    }

    fun updateBassBoost(percent: Int) {
        _equalizerState.value = _equalizerState.value.copy(bassBoostPercent = percent.coerceIn(0, 100))
    }

    fun updateVirtualizer(percent: Int) {
        _equalizerState.value = _equalizerState.value.copy(virtualizerPercent = percent.coerceIn(0, 100))
    }

    private fun handleTrackCompletion() {
        when (_repeatMode.value) {
            RepeatMode.REPEAT_ONE -> {
                seekTo(0)
                mediaPlayer?.start()
                _isPlaying.value = true
                startProgressTracker()
            }
            RepeatMode.REPEAT_ALL -> skipNext()
            RepeatMode.OFF -> {
                if (_currentIndex.value < _queue.value.size - 1) {
                    skipNext()
                } else {
                    _isPlaying.value = false
                    _currentPositionMs.value = 0
                }
            }
        }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive && _isPlaying.value) {
                val current = try {
                    mediaPlayer?.currentPosition ?: (_currentPositionMs.value + 200)
                } catch (_: Exception) {
                    _currentPositionMs.value + 200
                }
                _currentPositionMs.value = current
                updateActiveLyric(current)

                // Visualizer pulse animation
                val bassFactor = (_equalizerState.value.bassBoostPercent / 100f) * 0.4f
                val randomBars = List(16) { i ->
                    val base = 0.2f + (kotlin.math.sin((current / 150.0) + (i * 0.5)).toFloat() * 0.4f).coerceAtLeast(0.05f)
                    (base + bassFactor).coerceIn(0.1f, 1.0f)
                }
                _visualizerFrequencies.value = randomBars

                if (_durationMs.value > 0 && current >= _durationMs.value) {
                    handleTrackCompletion()
                    break
                }
                delay(100)
            }
        }
    }

    private fun parseLyrics(rawLyrics: String) {
        if (rawLyrics.isBlank()) {
            parsedLyrics = emptyList()
            _currentLyricLine.value = null
            return
        }
        val lines = mutableListOf<LyricLine>()
        rawLyrics.lines().forEach { line ->
            val match = Regex("""\[(\d{2}):(\d{2})\.(\d{2})]""").find(line)
            if (match != null) {
                val min = match.groupValues[1].toLongOrNull() ?: 0L
                val sec = match.groupValues[2].toLongOrNull() ?: 0L
                val ms = (match.groupValues[3].toLongOrNull() ?: 0L) * 10
                val totalMs = (min * 60 * 1000) + (sec * 1000) + ms
                val text = line.replace(match.value, "").trim()
                lines.add(LyricLine(totalMs, text))
            }
        }
        parsedLyrics = lines.sortedBy { it.timeMs }
    }

    private fun updateActiveLyric(currentMs: Int) {
        if (parsedLyrics.isEmpty()) return
        val active = parsedLyrics.lastOrNull { it.timeMs <= currentMs } ?: parsedLyrics.firstOrNull()
        _currentLyricLine.value = active
    }

    fun release() {
        progressJob?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
