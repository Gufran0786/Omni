package com.example.ui.musify

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.player.audio.RepeatMode as AudioRepeatMode
import com.example.ui.MainViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassIconButton
import com.example.ui.components.GlowGlassCard
import com.example.ui.components.NeonBadge
import com.example.ui.components.PulseWaveformVisualizer
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusifyPlayerSheet(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val isExpanded by viewModel.isMusifyExpanded.collectAsState()
    val showEqualizer by viewModel.showEqualizer.collectAsState()
    val showLyrics by viewModel.showLyrics.collectAsState()

    val currentTrack by viewModel.audioPlayer.currentTrack.collectAsState()
    val isPlaying by viewModel.audioPlayer.isPlaying.collectAsState()
    val currentPositionMs by viewModel.audioPlayer.currentPositionMs.collectAsState()
    val durationMs by viewModel.audioPlayer.durationMs.collectAsState()
    val repeatMode by viewModel.audioPlayer.repeatMode.collectAsState()
    val isShuffle by viewModel.audioPlayer.isShuffle.collectAsState()
    val visualizerBars by viewModel.audioPlayer.visualizerFrequencies.collectAsState()
    val activeLyric by viewModel.audioPlayer.currentLyricLine.collectAsState()

    if (isExpanded && currentTrack != null) {
        val track = currentTrack!!
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { viewModel.toggleMusifyExpanded(false) },
            sheetState = sheetState,
            containerColor = Color(0xFF0D121F),
            dragHandle = null
        ) {
            val infiniteTransition = rememberInfiniteTransition()
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(20000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlassIconButton(
                        icon = Icons.Filled.KeyboardArrowDown,
                        onClick = { viewModel.toggleMusifyExpanded(false) },
                        size = 42.dp,
                        contentDescription = "Collapse"
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "PLAYING FROM MUSIFY",
                            color = NeonMagenta,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = track.album,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    }

                    GlassIconButton(
                        icon = Icons.Filled.Equalizer,
                        onClick = { viewModel.toggleEqualizer(true) },
                        tint = NeonCyan,
                        size = 42.dp,
                        contentDescription = "Equalizer"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Glowing Vinyl Artwork Deck
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .clip(CircleShape)
                        .border(3.dp, Brush.sweepGradient(listOf(NeonCyan, NeonMagenta, NeonPurple, NeonCyan)), CircleShape)
                        .padding(8.dp)
                        .rotate(if (isPlaying) rotation else 0f),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = track.coverArtUrl,
                        contentDescription = track.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    // Vinyl center hole
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0B0E14))
                            .border(2.dp, NeonCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.8f)))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Track Title & Favorite
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.title,
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${track.artist} • ${track.genre}",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            maxLines = 1
                        )
                    }
                    IconButton(onClick = { viewModel.toggleAudioFavorite(track) }) {
                        Icon(
                            imageVector = if (track.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (track.isFavorite) NeonMagenta else TextSecondary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Karaoke Live Lyric Highlight Card
                if (activeLyric != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(NeonMagenta.copy(alpha = 0.15f))
                            .border(1.dp, NeonMagenta.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .clickable { viewModel.toggleLyrics(true) }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🎤 ${activeLyric!!.text}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Spectrum Visualizer
                PulseWaveformVisualizer(
                    frequencies = visualizerBars,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp),
                    barColor = NeonCyan,
                    barWidth = 4.dp,
                    spacing = 4.dp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Scrub Slider & Timing
                val maxDuration = durationMs.coerceAtLeast(1)
                Slider(
                    value = currentPositionMs.toFloat().coerceIn(0f, maxDuration.toFloat()),
                    onValueChange = { viewModel.audioPlayer.seekTo(it.toInt()) },
                    valueRange = 0f..maxDuration.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("musify_scrub_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = NeonMagenta,
                        activeTrackColor = NeonMagenta,
                        inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val curSec = currentPositionMs / 1000
                    val durSec = maxDuration / 1000
                    Text(
                        text = "${curSec / 60}:${String.format("%02d", curSec % 60)}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${durSec / 60}:${String.format("%02d", durSec % 60)}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Playback Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.audioPlayer.toggleShuffle() }) {
                        Icon(
                            imageVector = Icons.Filled.Shuffle,
                            contentDescription = "Shuffle",
                            tint = if (isShuffle) NeonCyan else TextMuted
                        )
                    }

                    GlassIconButton(
                        icon = Icons.Filled.SkipPrevious,
                        onClick = { viewModel.audioPlayer.skipPrevious() },
                        size = 48.dp,
                        contentDescription = "Previous"
                    )

                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(NeonCyan, NeonMagenta)))
                            .clickable { viewModel.audioPlayer.togglePlayPause() }
                            .testTag("musify_play_pause_button"),
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
                        icon = Icons.Filled.SkipNext,
                        onClick = { viewModel.audioPlayer.skipNext() },
                        size = 48.dp,
                        contentDescription = "Next"
                    )

                    IconButton(onClick = { viewModel.audioPlayer.toggleRepeat() }) {
                        Icon(
                            imageVector = if (repeatMode == AudioRepeatMode.REPEAT_ONE) Icons.Filled.RepeatOne else Icons.Filled.Repeat,
                            contentDescription = "Repeat",
                            tint = if (repeatMode != AudioRepeatMode.OFF) NeonMagenta else TextMuted
                        )
                    }
                }
            }
        }
    }

    // Equalizer Sheet Overlay
    if (showEqualizer) {
        EqualizerSheet(
            viewModel = viewModel,
            onDismiss = { viewModel.toggleEqualizer(false) }
        )
    }

    // Full Lyrics Sheet Overlay
    if (showLyrics) {
        LyricsSheet(
            viewModel = viewModel,
            onDismiss = { viewModel.toggleLyrics(false) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerSheet(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val eqState by viewModel.audioPlayer.equalizerState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF131926)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "5-Band Studio Equalizer",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                NeonBadge(text = eqState.currentPreset, color = NeonCyan)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Frequency Sliders
            eqState.bands.forEachIndexed { index, band ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = band.frequencyLabel,
                        color = NeonCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.width(64.dp)
                    )
                    Slider(
                        value = band.levelDb,
                        onValueChange = { viewModel.audioPlayer.updateBand(index, it) },
                        valueRange = -12f..12f,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = NeonCyan,
                            activeTrackColor = NeonCyan
                        )
                    )
                    Text(
                        text = "${band.levelDb.toInt()} dB",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.width(44.dp),
                        textAlign = TextAlign.End
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bass Boost & Virtualizer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlassCard(modifier = Modifier.weight(1f)) {
                    Column {
                        Text("Bass Boost", color = NeonMagenta, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("${eqState.bassBoostPercent}%", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        Slider(
                            value = eqState.bassBoostPercent.toFloat(),
                            onValueChange = { viewModel.audioPlayer.updateBassBoost(it.toInt()) },
                            valueRange = 0f..100f,
                            colors = SliderDefaults.colors(thumbColor = NeonMagenta, activeTrackColor = NeonMagenta)
                        )
                    }
                }

                GlassCard(modifier = Modifier.weight(1f)) {
                    Column {
                        Text("3D Virtualizer", color = NeonPurple, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("${eqState.virtualizerPercent}%", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        Slider(
                            value = eqState.virtualizerPercent.toFloat(),
                            onValueChange = { viewModel.audioPlayer.updateVirtualizer(it.toInt()) },
                            valueRange = 0f..100f,
                            colors = SliderDefaults.colors(thumbColor = NeonPurple, activeTrackColor = NeonPurple)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LyricsSheet(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val track by viewModel.audioPlayer.currentTrack.collectAsState()
    val activeLyric by viewModel.audioPlayer.currentLyricLine.collectAsState()
    val currentPositionMs by viewModel.audioPlayer.currentPositionMs.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0F1424)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Synchronized Karaoke Lyrics",
                color = NeonMagenta,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = track?.title ?: "",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            val lyrics = track?.lyrics ?: ""
            if (lyrics.isBlank()) {
                Text(
                    text = "No synchronized lyrics available for this track.",
                    color = TextMuted,
                    fontSize = 14.sp
                )
            } else {
                lyrics.lines().forEach { rawLine ->
                    val cleanText = rawLine.replace(Regex("""\[\d{2}:\d{2}\.\d{2}]"""), "").trim()
                    if (cleanText.isNotBlank()) {
                        val isActive = activeLyric?.text == cleanText
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isActive) NeonMagenta.copy(alpha = 0.2f) else Color.Transparent)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = cleanText,
                                color = if (isActive) Color.White else TextMuted,
                                fontSize = if (isActive) 18.sp else 14.sp,
                                fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
