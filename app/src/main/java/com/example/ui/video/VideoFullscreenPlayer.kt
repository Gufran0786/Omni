package com.example.ui.video

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.ui.MainViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassIconButton
import com.example.ui.components.GlowGlassCard
import com.example.ui.components.NeonBadge
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun VideoFullscreenPlayer(
    viewModel: MainViewModel,
    onClose: () -> Unit
) {
    val currentVideo by viewModel.videoPlayer.currentVideo.collectAsState()
    val isPlaying by viewModel.videoPlayer.isPlaying.collectAsState()
    val currentSec by viewModel.videoPlayer.currentPositionSeconds.collectAsState()
    val durationSec by viewModel.videoPlayer.durationSeconds.collectAsState()
    val speed by viewModel.videoPlayer.playbackSpeed.collectAsState()
    val aspectRatio by viewModel.videoPlayer.aspectRatio.collectAsState()
    val gestureHud by viewModel.videoPlayer.gestureHud.collectAsState()
    val subtitlesEnabled by viewModel.videoPlayer.subtitlesEnabled.collectAsState()
    val subtitleText by viewModel.videoPlayer.currentSubtitle.collectAsState()
    val isPip by viewModel.videoPlayer.isPipMode.collectAsState()

    val video = currentVideo ?: return
    var showControls by remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { offset ->
                            val width = size.width
                            if (offset.x < width / 2) {
                                viewModel.videoPlayer.seekRelative(-10)
                            } else {
                                viewModel.videoPlayer.seekRelative(10)
                            }
                        },
                        onTap = {
                            showControls = !showControls
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val width = size.width
                        val x = change.position.x
                        if (kotlin.math.abs(dragAmount.y) > kotlin.math.abs(dragAmount.x)) {
                            // Vertical drag
                            val deltaPercent = (-dragAmount.y / 8f).toInt()
                            if (x < width / 2) {
                                viewModel.videoPlayer.adjustBrightness(deltaPercent)
                            } else {
                                viewModel.videoPlayer.adjustVolume(deltaPercent)
                            }
                        } else {
                            // Horizontal drag -> Seek
                            val deltaSec = (dragAmount.x / 10f).toInt()
                            viewModel.videoPlayer.seekRelative(deltaSec)
                        }
                    }
                }
        ) {
            // Video Frame Simulation / Hardware Surface
            AsyncImage(
                model = video.thumbnailUri,
                contentDescription = video.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = when (aspectRatio.name) {
                    "FILL" -> ContentScale.Crop
                    "RATIO_16_9" -> ContentScale.FillWidth
                    "RATIO_4_3" -> ContentScale.FillHeight
                    else -> ContentScale.Fit
                }
            )

            // Dynamic Subtitle Ribbon
            if (subtitlesEnabled) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = if (showControls) 110.dp else 40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = subtitleText,
                        color = Color.Yellow,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Gesture HUD Overlay (Brightness / Volume / Seek)
            AnimatedVisibility(
                visible = gestureHud.isVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF141A29).copy(alpha = 0.9f))
                        .border(1.dp, NeonCyan, RoundedCornerShape(20.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        when (gestureHud.type) {
                            "BRIGHTNESS" -> {
                                Icon(Icons.Filled.BrightnessMedium, null, tint = NeonAmber, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Brightness: ${gestureHud.valuePercent}%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            "VOLUME" -> {
                                Icon(Icons.AutoMirrored.Filled.VolumeUp, null, tint = NeonCyan, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Volume: ${gestureHud.valuePercent}%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            "SEEK" -> {
                                Icon(Icons.Filled.FastForward, null, tint = NeonMagenta, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("${if (gestureHud.seekDeltaSeconds >= 0) "+" else ""}${gestureHud.seekDeltaSeconds}s", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }

            // Controls Overlay (Top Bar & Bottom Scrub Controls)
            AnimatedVisibility(
                visible = showControls,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                ) {
                    // Top Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp, start = 16.dp, end = 16.dp)
                            .align(Alignment.TopCenter),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlassIconButton(
                            icon = Icons.Filled.Close,
                            onClick = onClose,
                            size = 40.dp,
                            contentDescription = "Close Player"
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
                        ) {
                            Text(video.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                            Text(video.resolution, color = NeonAmber, fontSize = 11.sp)
                        }

                        Row {
                            GlassIconButton(
                                icon = Icons.Filled.PictureInPicture,
                                onClick = {
                                    viewModel.videoPlayer.togglePipMode()
                                    viewModel.emitToast("Picture-in-Picture Mini-Mode Active")
                                },
                                tint = NeonCyan,
                                size = 40.dp,
                                contentDescription = "PiP Mode"
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            GlassIconButton(
                                icon = Icons.Filled.Audiotrack,
                                onClick = { viewModel.convertVideoToMp3(video) },
                                tint = NeonMagenta,
                                size = 40.dp,
                                contentDescription = "Extract MP3"
                            )
                        }
                    }

                    // Center Play/Pause & 10s Skips
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalArrangement = Arrangement.spacedBy(32.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlassIconButton(
                            icon = Icons.Filled.FastRewind,
                            onClick = { viewModel.videoPlayer.seekRelative(-10) },
                            tint = Color.White,
                            size = 52.dp,
                            contentDescription = "Rewind 10s"
                        )

                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(NeonAmber, NeonMagenta)))
                                .clickable { viewModel.videoPlayer.togglePlayPause() }
                                .testTag("video_fullscreen_play_pause_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = Color.Black,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        GlassIconButton(
                            icon = Icons.Filled.FastForward,
                            onClick = { viewModel.videoPlayer.seekRelative(10) },
                            tint = Color.White,
                            size = 52.dp,
                            contentDescription = "Forward 10s"
                        )
                    }

                    // Bottom Bar with Scrub Slider, Speed, Aspect Ratio, Subtitles
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 30.dp, start = 16.dp, end = 16.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        val maxDur = durationSec.coerceAtLeast(1)
                        Slider(
                            value = currentSec.toFloat().coerceIn(0f, maxDur.toFloat()),
                            onValueChange = { viewModel.videoPlayer.seekTo(it.toInt()) },
                            valueRange = 0f..maxDur.toFloat(),
                            modifier = Modifier.fillMaxWidth(),
                            colors = SliderDefaults.colors(
                                thumbColor = NeonAmber,
                                activeTrackColor = NeonAmber,
                                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${currentSec / 60}:${String.format("%02d", currentSec % 60)} / ${maxDur / 60}:${String.format("%02d", maxDur % 60)}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                // Speed switcher
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.12f))
                                        .clickable {
                                            val nextSpeed = when (speed) {
                                                1.0f -> 1.25f
                                                1.25f -> 1.5f
                                                1.5f -> 2.0f
                                                2.0f -> 0.75f
                                                else -> 1.0f
                                            }
                                            viewModel.videoPlayer.setSpeed(nextSpeed)
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("${speed}x", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                // Aspect Ratio switcher
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.12f))
                                        .clickable { viewModel.videoPlayer.cycleAspectRatio() }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(aspectRatio.label, color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                // Subtitles toggle
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (subtitlesEnabled) NeonMagenta.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.12f))
                                        .clickable { viewModel.videoPlayer.toggleSubtitles() }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("CC", color = if (subtitlesEnabled) NeonMagenta else TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
