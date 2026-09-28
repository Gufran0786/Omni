package com.example.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.ui.AppNavTab
import com.example.ui.MainViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassIconButton
import com.example.ui.components.GlowGlassCard
import com.example.ui.components.NeonBadge
import com.example.ui.components.PulseWaveformVisualizer
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val tracks by viewModel.allTracks.collectAsState()
    val photos by viewModel.allPhotos.collectAsState()
    val videos by viewModel.allVideos.collectAsState()
    val storage by viewModel.storageBreakdown.collectAsState()
    val isSyncing by viewModel.isCloudSyncing.collectAsState()
    val syncProgress by viewModel.syncProgress.collectAsState()

    val currentTrack by viewModel.audioPlayer.currentTrack.collectAsState()
    val isAudioPlaying by viewModel.audioPlayer.isPlaying.collectAsState()
    val visualizerBars by viewModel.audioPlayer.visualizerFrequencies.collectAsState()

    val searchQuery by viewModel.aiSearchQuery.collectAsState()
    val isAiSearching by viewModel.isAiSearching.collectAsState()
    val aiResult by viewModel.aiSearchResult.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // App Header & Branding for Gufran Khan
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Gufran Khan",
                            color = TextPrimary,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        NeonBadge(
                            text = "PRO CREATOR",
                            color = NeonCyan
                        )
                    }
                    Text(
                        text = "OmniPlay Studio • Musify + Photos + PLAYit",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    GlassIconButton(
                        icon = if (isSyncing) Icons.Filled.CloudSync else Icons.Filled.CloudDone,
                        onClick = { viewModel.triggerCloudSync() },
                        tint = if (isSyncing) NeonAmber else NeonGreen,
                        size = 42.dp,
                        contentDescription = "Cloud Sync"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    GlassIconButton(
                        icon = Icons.Filled.Security,
                        onClick = { viewModel.setTab(AppNavTab.VAULT_STUDIO) },
                        tint = NeonMagenta,
                        size = 42.dp,
                        contentDescription = "Vault Shortcut"
                    )
                }
            }
        }

        // AI-Powered Smart Media Search Bar
        item {
            GlowGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_search_bar_container"),
                glowColor = NeonCyan
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "AI Smart Media Discovery",
                            color = NeonCyan,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.updateAiSearchQuery(it) },
                            placeholder = {
                                Text(
                                    text = "e.g. \"chill synthwave with neon sunset photos\"",
                                    color = TextMuted,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("ai_search_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = GlassBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                cursorColor = NeonCyan
                            ),
                            singleLine = true,
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.clearAiSearch() }) {
                                        Icon(
                                            imageVector = Icons.Outlined.Close,
                                            contentDescription = "Clear",
                                            tint = TextSecondary
                                        )
                                    }
                                }
                            }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Brush.linearGradient(listOf(NeonCyan, NeonMagenta)))
                                .clickable { viewModel.executeAiSearch() }
                                .testTag("ai_search_submit_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isAiSearching) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = Color.Black,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = "Search",
                                    tint = Color.Black,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    // Quick AI Mood Prompts chips
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val prompts = listOf("⚡ High Energy Gym", "🌧️ Lo-Fi Rainy Study", "🌄 Sunset Golden Hour", "🚀 Cyberpunk 4K")
                        items(prompts) { prompt ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(Color.White.copy(alpha = 0.08f))
                                    .border(1.dp, GlassBorder, RoundedCornerShape(100.dp))
                                    .clickable {
                                        viewModel.updateAiSearchQuery(prompt.substring(3))
                                        viewModel.executeAiSearch()
                                    }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = prompt,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // AI Search Results Sheet (When active)
        if (aiResult != null) {
            item {
                val res = aiResult!!
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonCyan,
                    borderWidth = 1.5.dp
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.AutoAwesome, null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("AI Discovery Matches", color = NeonCyan, fontWeight = FontWeight.Bold)
                            }
                            NeonBadge(text = "Mood: ${res.suggestedMood}", color = NeonMagenta)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(res.explanation, color = TextSecondary, fontSize = 13.sp)

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Matched Songs & Presets", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        val matchedTracks = tracks.filter { it.id in res.matchedTrackIds }
                        matchedTracks.forEach { trk ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .clickable { viewModel.playTrack(trk) }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.MusicNote, null, tint = NeonMagenta, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(trk.title, color = TextPrimary, fontSize = 13.sp, maxLines = 1, modifier = Modifier.weight(1f))
                                Icon(Icons.Filled.PlayArrow, null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }

        // 1. Musify Spotlight Deck
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.setTab(AppNavTab.MUSIFY) }
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(NeonMagenta.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.MusicNote, null, tint = NeonMagenta, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Musify Engine", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("${tracks.size} HD Tracks • Equalizer Ready", color = TextSecondary, fontSize = 12.sp)
                            }
                        }
                        NeonBadge(text = "HQ AUDIO", color = NeonMagenta)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (currentTrack != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .clickable {
                                    viewModel.toggleMusifyExpanded(true)
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = currentTrack?.coverArtUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentTrack?.title ?: "",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = currentTrack?.artist ?: "",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                PulseWaveformVisualizer(
                                    frequencies = visualizerBars,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(12.dp),
                                    barColor = NeonMagenta
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            GlassIconButton(
                                icon = if (isAudioPlaying) Icons.Filled.GraphicEq else Icons.Filled.PlayArrow,
                                onClick = { viewModel.audioPlayer.togglePlayPause() },
                                tint = NeonMagenta,
                                size = 40.dp
                            )
                        }
                    } else if (tracks.isNotEmpty()) {
                        val first = tracks.first()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                                .clickable { viewModel.playTrack(first) }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = first.coverArtUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(first.title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                                Text("Tap to start listening", color = TextSecondary, fontSize = 11.sp)
                            }
                            Icon(Icons.Filled.PlayArrow, null, tint = NeonMagenta)
                        }
                    }
                }
            }
        }

        // 2. Google Photos Carousel: "Memories From This Day"
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(ElectricBlue.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.PhotoLibrary, null, tint = ElectricBlue, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Memories & Gallery", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("${photos.size} Photos • AI Auto-Categorized", color = TextSecondary, fontSize = 12.sp)
                            }
                        }
                        Text(
                            text = "View All",
                            color = ElectricBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { viewModel.setTab(AppNavTab.PHOTOS) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(photos) { photo ->
                            Box(
                                modifier = Modifier
                                    .width(140.dp)
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                                    .clickable { viewModel.openPhotoViewer(photo) }
                            ) {
                                AsyncImage(
                                    model = photo.urlOrPath,
                                    contentDescription = photo.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                                                startY = 100f
                                            )
                                        )
                                )
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = photo.title,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = photo.albumName,
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. PLAYit Cinema Player Showcase
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(NeonAmber.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Videocam, null, tint = NeonAmber, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("PLAYit Video Cinema", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Gesture HUD • PiP • MP3 Extractor", color = TextSecondary, fontSize = 12.sp)
                            }
                        }
                        Text(
                            text = "Open PLAYit",
                            color = NeonAmber,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { viewModel.setTab(AppNavTab.PLAYIT) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val featuredVideo = videos.firstOrNull()
                    if (featuredVideo != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                                .clickable { viewModel.playVideo(featuredVideo) }
                        ) {
                            AsyncImage(
                                model = featuredVideo.thumbnailUri,
                                contentDescription = featuredVideo.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.45f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(NeonAmber, NeonMagenta)))
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.PlayArrow, null, tint = Color.Black, modifier = Modifier.size(32.dp))
                                }
                            }

                            NeonBadge(
                                text = featuredVideo.resolution,
                                color = NeonGreen,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(10.dp)
                            )

                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = featuredVideo.title,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${featuredVideo.durationSeconds / 60}m ${featuredVideo.durationSeconds % 60}s • Tap to Launch Player",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Storage & OmniCloud Sync Gauge
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("OmniCloud & Storage Engine", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                if (isSyncing) "Syncing media ($syncProgress%)..." else "All media backed up & synchronized",
                                color = if (isSyncing) NeonAmber else NeonGreen,
                                fontSize = 12.sp
                            )
                        }
                        if (isSyncing) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = NeonAmber, strokeWidth = 2.dp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (isSyncing) {
                        LinearProgressIndicator(
                            progress = { syncProgress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(100.dp)),
                            color = NeonAmber,
                            trackColor = Color.White.copy(alpha = 0.1f),
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Music", color = TextSecondary, fontSize = 11.sp)
                            Text("${(storage?.musicBytes ?: 0) / 1_000_000} MB", color = NeonMagenta, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Column {
                            Text("Photos", color = TextSecondary, fontSize = 11.sp)
                            Text("${(storage?.photoBytes ?: 0) / 1_000_000} MB", color = ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Column {
                            Text("Videos", color = TextSecondary, fontSize = 11.sp)
                            Text("${(storage?.videoBytes ?: 0) / 1_000_000} MB", color = NeonAmber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Column {
                            Text("Vault", color = TextSecondary, fontSize = 11.sp)
                            Text("${(storage?.vaultBytes ?: 0) / 1_000_000} MB", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // 5. Scan Device Media Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                    .clickable { viewModel.scanDevice() }
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Tune, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Scan & Sync Local Device Media Files",
                        color = NeonCyan,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
