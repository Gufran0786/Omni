package com.example.player.video

import android.content.Context
import com.example.data.model.VideoItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class VideoAspectRatio(val label: String) {
    FIT("Fit Screen"),
    FILL("Crop to Fill"),
    RATIO_16_9("16:9"),
    RATIO_4_3("4:3"),
    ORIGINAL("Original")
}

data class VideoGestureHud(
    val isVisible: Boolean = false,
    val type: String = "VOLUME", // "VOLUME", "BRIGHTNESS", "SEEK"
    val valuePercent: Int = 50,
    val seekDeltaSeconds: Int = 0
)

class VideoPlayerManager(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Main)
    private var progressJob: Job? = null
    private var hudDismissJob: Job? = null

    private val _currentVideo = MutableStateFlow<VideoItem?>(null)
    val currentVideo = _currentVideo.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentPositionSeconds = MutableStateFlow(0)
    val currentPositionSeconds = _currentPositionSeconds.asStateFlow()

    private val _durationSeconds = MutableStateFlow(100)
    val durationSeconds = _durationSeconds.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed = _playbackSpeed.asStateFlow()

    private val _aspectRatio = MutableStateFlow(VideoAspectRatio.FIT)
    val aspectRatio = _aspectRatio.asStateFlow()

    private val _isPipMode = MutableStateFlow(false)
    val isPipMode = _isPipMode.asStateFlow()

    private val _brightness = MutableStateFlow(75) // 0 - 100
    val brightness = _brightness.asStateFlow()

    private val _volume = MutableStateFlow(80) // 0 - 100
    val volume = _volume.asStateFlow()

    private val _gestureHud = MutableStateFlow(VideoGestureHud())
    val gestureHud = _gestureHud.asStateFlow()

    private val _subtitlesEnabled = MutableStateFlow(true)
    val subtitlesEnabled = _subtitlesEnabled.asStateFlow()

    private val _currentSubtitle = MutableStateFlow("4K Ultra HD • Hardware Accelerated")
    val currentSubtitle = _currentSubtitle.asStateFlow()

    fun loadVideo(video: VideoItem) {
        _currentVideo.value = video
        _durationSeconds.value = video.durationSeconds
        _currentPositionSeconds.value = video.lastWatchProgressSeconds
        _isPlaying.value = true
        startVideoPlaybackLoop()
    }

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
        if (_isPlaying.value) {
            startVideoPlaybackLoop()
        }
    }

    fun seekTo(seconds: Int) {
        val target = seconds.coerceIn(0, _durationSeconds.value)
        _currentPositionSeconds.value = target
        showGestureHud("SEEK", 0, target - _currentPositionSeconds.value)
    }

    fun seekRelative(deltaSeconds: Int) {
        val target = (_currentPositionSeconds.value + deltaSeconds).coerceIn(0, _durationSeconds.value)
        _currentPositionSeconds.value = target
        showGestureHud("SEEK", (target * 100) / _durationSeconds.value.coerceAtLeast(1), deltaSeconds)
    }

    fun setSpeed(speed: Float) {
        _playbackSpeed.value = speed
    }

    fun cycleAspectRatio() {
        _aspectRatio.value = when (_aspectRatio.value) {
            VideoAspectRatio.FIT -> VideoAspectRatio.FILL
            VideoAspectRatio.FILL -> VideoAspectRatio.RATIO_16_9
            VideoAspectRatio.RATIO_16_9 -> VideoAspectRatio.RATIO_4_3
            VideoAspectRatio.RATIO_4_3 -> VideoAspectRatio.ORIGINAL
            VideoAspectRatio.ORIGINAL -> VideoAspectRatio.FIT
        }
    }

    fun togglePipMode() {
        _isPipMode.value = !_isPipMode.value
    }

    fun toggleSubtitles() {
        _subtitlesEnabled.value = !_subtitlesEnabled.value
    }

    fun adjustBrightness(delta: Int) {
        val newB = (_brightness.value + delta).coerceIn(5, 100)
        _brightness.value = newB
        showGestureHud("BRIGHTNESS", newB)
    }

    fun adjustVolume(delta: Int) {
        val newV = (_volume.value + delta).coerceIn(0, 100)
        _volume.value = newV
        showGestureHud("VOLUME", newV)
    }

    private fun showGestureHud(type: String, value: Int, seekDelta: Int = 0) {
        _gestureHud.value = VideoGestureHud(
            isVisible = true,
            type = type,
            valuePercent = value,
            seekDeltaSeconds = seekDelta
        )
        hudDismissJob?.cancel()
        hudDismissJob = scope.launch {
            delay(1200)
            _gestureHud.value = _gestureHud.value.copy(isVisible = false)
        }
    }

    private fun startVideoPlaybackLoop() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive && _isPlaying.value) {
                val delayMs = (1000 / _playbackSpeed.value).toLong()
                delay(delayMs)
                if (_currentPositionSeconds.value < _durationSeconds.value) {
                    _currentPositionSeconds.value += 1
                    updateSubtitleLine(_currentPositionSeconds.value)
                } else {
                    _isPlaying.value = false
                    break
                }
            }
        }
    }

    private fun updateSubtitleLine(sec: Int) {
        _currentSubtitle.value = when (sec % 30) {
            in 0..6 -> "Cinematic 4K 60FPS Experience"
            in 7..14 -> "PLAYit Hardware Engine v3.8 Active"
            in 15..22 -> "Surround Sound Audio Mastered"
            else -> "Double-tap sides to seek • Swipe for Brightness & Volume"
        }
    }

    fun closePlayer() {
        progressJob?.cancel()
        _isPlaying.value = false
        _currentVideo.value = null
        _isPipMode.value = false
    }
}
