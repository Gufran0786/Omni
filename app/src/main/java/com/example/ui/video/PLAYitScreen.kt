package com.example.ui.video

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.VideoItem
import com.example.ui.MainViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassIconButton
import com.example.ui.components.NeonBadge
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PLAYitScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val videos by viewModel.allVideos.collectAsState()
    val isFullscreen by viewModel.isPLAYitFullscreen.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Gufran's PLAYit",
                                color = TextPrimary,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            NeonBadge(text = "4K HDR Engine", color = NeonAmber)
                        }
                        Text(
                            text = "Gesture Controls • Subtitles • MP3 Extraction Tool",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    GlassIconButton(
                        icon = Icons.Filled.HighQuality,
                        onClick = { viewModel.emitToast("PLAYit HW Acceleration Active") },
                        tint = NeonAmber,
                        size = 42.dp,
                        contentDescription = "HW Quality"
                    )
                }
            }

            // Featured Video Hero Card
            if (videos.isNotEmpty()) {
                item {
                    val featured = videos.first()
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.playVideo(featured) }
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            ) {
                                AsyncImage(
                                    model = featured.thumbnailUri,
                                    contentDescription = featured.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.35f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .background(Brush.linearGradient(listOf(NeonAmber, NeonMagenta))),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Filled.PlayArrow, null, tint = Color.Black, modifier = Modifier.size(34.dp))
                                    }
                                }
                                NeonBadge(
                                    text = featured.resolution,
                                    color = NeonGreen,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(featured.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("${featured.durationSeconds / 60}m ${featured.durationSeconds % 60}s • ${(featured.sizeBytes / 1_000_000)} MB", color = TextSecondary, fontSize = 12.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(NeonMagenta.copy(alpha = 0.15f))
                                        .clickable { viewModel.convertVideoToMp3(featured) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Audiotrack, null, tint = NeonMagenta, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Extract MP3", color = NeonMagenta, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Video Library List
            item {
                Text(
                    text = "Video Library (${videos.size})",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            items(videos, key = { it.id }) { video ->
                VideoRowItem(
                    video = video,
                    onPlay = { viewModel.playVideo(video) },
                    onExtractMp3 = { viewModel.convertVideoToMp3(video) },
                    onToggleFav = { viewModel.toggleVideoFavorite(video) },
                    onLockVault = { viewModel.setVideoVaultLocked(video, true) }
                )
            }
        }

        // Fullscreen Video Player Overlay
        if (isFullscreen) {
            VideoFullscreenPlayer(
                viewModel = viewModel,
                onClose = { viewModel.closeVideoFullscreen() }
            )
        }
    }
}

@Composable
fun VideoRowItem(
    video: VideoItem,
    onPlay: () -> Unit,
    onExtractMp3: () -> Unit,
    onToggleFav: () -> Unit,
    onLockVault: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onPlay)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 100.dp, height = 64.dp)
                .clip(RoundedCornerShape(10.dp))
        ) {
            AsyncImage(
                model = video.thumbnailUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.PlayArrow, null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${video.durationSeconds / 60}:${String.format("%02d", video.durationSeconds % 60)}",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = video.title,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                NeonBadge(text = video.resolution, color = NeonAmber)
                Spacer(modifier = Modifier.width(6.dp))
                Text("${video.sizeBytes / 1_000_000} MB", color = TextSecondary, fontSize = 11.sp)
            }
        }

        IconButton(onClick = onExtractMp3) {
            Icon(Icons.Filled.Audiotrack, contentDescription = "Extract Audio", tint = NeonMagenta, modifier = Modifier.size(22.dp))
        }

        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "Options", tint = TextSecondary, modifier = Modifier.size(20.dp))
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(Color(0xFF1E2638))
            ) {
                DropdownMenuItem(
                    text = { Text("Extract MP3 Audio Track", color = TextPrimary) },
                    onClick = {
                        showMenu = false
                        onExtractMp3()
                    },
                    leadingIcon = { Icon(Icons.Filled.Audiotrack, null, tint = NeonMagenta) }
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
