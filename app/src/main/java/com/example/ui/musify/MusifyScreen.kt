package com.example.ui.musify

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AudioTrack
import com.example.ui.MainViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassIconButton
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

@Composable
fun MusifyScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val tracks by viewModel.allTracks.collectAsState()
    val favoriteTracks by viewModel.favoriteTracks.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val currentTrack by viewModel.audioPlayer.currentTrack.collectAsState()
    val isPlaying by viewModel.audioPlayer.isPlaying.collectAsState()
    val visualizerBars by viewModel.audioPlayer.visualizerFrequencies.collectAsState()
    val eqState by viewModel.audioPlayer.equalizerState.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All Songs (${tracks.size})", "Favorites (${favoriteTracks.size})", "Playlists (${playlists.size})")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Musify Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Gufran's Musify",
                            color = TextPrimary,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        NeonBadge(text = "Hi-Res Audio", color = NeonMagenta)
                    }
                    Text(
                        text = "Lossless Stream • Dynamic EQ • Real-Time Lyrics",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Row {
                    GlassIconButton(
                        icon = Icons.Filled.Equalizer,
                        onClick = { viewModel.toggleEqualizer(true) },
                        tint = NeonCyan,
                        size = 42.dp,
                        contentDescription = "Equalizer"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    GlassIconButton(
                        icon = Icons.Filled.Subtitles,
                        onClick = { viewModel.toggleLyrics(true) },
                        tint = NeonMagenta,
                        size = 42.dp,
                        contentDescription = "Lyrics"
                    )
                }
            }
        }

        // Quick Equalizer Presets Bar
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Quick Equalizer Preset: ${eqState.currentPreset}",
                            color = NeonCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        NeonBadge(text = "Bass +${eqState.bassBoostPercent}%", color = NeonGreen)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val presets = listOf("Bass Heavy", "Pop", "Rock", "Jazz", "EDM", "Vocal Booster")
                        items(presets) { preset ->
                            val isSelected = eqState.currentPreset == preset
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(
                                        if (isSelected) Brush.horizontalGradient(listOf(NeonMagenta, NeonPurple))
                                        else Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.08f)))
                                    )
                                    .border(1.dp, if (isSelected) NeonMagenta else GlassBorder, RoundedCornerShape(100.dp))
                                    .clickable { viewModel.audioPlayer.setEqualizerPreset(preset) }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = preset,
                                    color = if (isSelected) Color.White else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        // Tabs (All Songs / Favorites / Playlists)
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = NeonMagenta,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = NeonMagenta
                    )
                },
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) NeonMagenta else TextSecondary,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                // All Songs List
                items(tracks, key = { it.id }) { track ->
                    AudioTrackRow(
                        track = track,
                        isCurrent = currentTrack?.id == track.id,
                        isPlaying = isPlaying,
                        visualizerBars = visualizerBars,
                        onPlay = { viewModel.playTrack(track, tracks) },
                        onToggleFav = { viewModel.toggleAudioFavorite(track) },
                        onLockVault = { viewModel.setAudioVaultLocked(track, true) },
                        onOpenLyrics = {
                            viewModel.playTrack(track, tracks)
                            viewModel.toggleLyrics(true)
                        }
                    )
                }
            }
            1 -> {
                // Favorites List
                if (favoriteTracks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Outlined.FavoriteBorder, null, tint = TextMuted, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("No favorites yet", color = TextSecondary, fontSize = 14.sp)
                                Text("Tap heart on any song to save it here", color = TextMuted, fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    items(favoriteTracks, key = { it.id }) { track ->
                        AudioTrackRow(
                            track = track,
                            isCurrent = currentTrack?.id == track.id,
                            isPlaying = isPlaying,
                            visualizerBars = visualizerBars,
                            onPlay = { viewModel.playTrack(track, favoriteTracks) },
                            onToggleFav = { viewModel.toggleAudioFavorite(track) },
                            onLockVault = { viewModel.setAudioVaultLocked(track, true) },
                            onOpenLyrics = {
                                viewModel.playTrack(track, favoriteTracks)
                                viewModel.toggleLyrics(true)
                            }
                        )
                    }
                }
            }
            2 -> {
                // Playlists View
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Custom Playlists", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(NeonMagenta.copy(alpha = 0.2f))
                                .clickable {
                                    viewModel.createPlaylist("Neon Night Drive", "Top synthwave beats", tracks.take(3).map { it.id })
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Add, null, tint = NeonMagenta, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Playlist", color = NeonMagenta, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                items(playlists, key = { it.id }) { pl ->
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (tracks.isNotEmpty()) {
                                    viewModel.playTrack(tracks.first(), tracks)
                                }
                            }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = pl.coverArtUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(pl.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(pl.description, color = TextSecondary, fontSize = 12.sp, maxLines = 1)
                                Text("High Definition Audio Mix", color = NeonCyan, fontSize = 11.sp)
                            }
                            Icon(Icons.Filled.PlayArrow, null, tint = NeonMagenta, modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AudioTrackRow(
    track: AudioTrack,
    isCurrent: Boolean,
    isPlaying: Boolean,
    visualizerBars: List<Float>,
    onPlay: () -> Unit,
    onToggleFav: () -> Unit,
    onLockVault: () -> Unit,
    onOpenLyrics: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isCurrent) NeonMagenta.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.05f))
            .border(1.dp, if (isCurrent) NeonMagenta.copy(alpha = 0.6f) else GlassBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onPlay)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.coverArtUrl,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                color = if (isCurrent) NeonMagenta else TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${track.artist} • ${track.durationSeconds / 60}:${String.format("%02d", track.durationSeconds % 60)}",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.width(6.dp))
                NeonBadge(text = "${track.bitrateKbps}k", color = NeonCyan)
            }

            if (isCurrent && isPlaying) {
                Spacer(modifier = Modifier.height(4.dp))
                PulseWaveformVisualizer(
                    frequencies = visualizerBars,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),
                    barColor = NeonMagenta
                )
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        IconButton(onClick = onToggleFav) {
            Icon(
                imageVector = if (track.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (track.isFavorite) NeonMagenta else TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }

        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Options",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(Color(0xFF1E2638))
            ) {
                DropdownMenuItem(
                    text = { Text("View Synchronized Lyrics", color = TextPrimary) },
                    onClick = {
                        showMenu = false
                        onOpenLyrics()
                    },
                    leadingIcon = { Icon(Icons.Filled.Subtitles, null, tint = NeonMagenta) }
                )
                DropdownMenuItem(
                    text = { Text("Move to Private Vault", color = TextPrimary) },
                    onClick = {
                        showMenu = false
                        onLockVault()
                    },
                    leadingIcon = { Icon(Icons.Filled.Lock, null, tint = NeonGreen) }
                )
            }
        }
    }
}
