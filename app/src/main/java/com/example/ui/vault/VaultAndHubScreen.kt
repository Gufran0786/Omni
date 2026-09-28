package com.example.ui.vault

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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.MainViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassIconButton
import com.example.ui.components.GlowButton
import com.example.ui.components.GlowGlassCard
import com.example.ui.components.NeonBadge
import com.example.ui.theme.AppThemeMode
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
fun VaultAndHubScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val isVaultUnlocked by viewModel.isVaultUnlocked.collectAsState()
    val vaultTracks by viewModel.vaultTracks.collectAsState()
    val vaultPhotos by viewModel.vaultPhotos.collectAsState()
    val vaultVideos by viewModel.vaultVideos.collectAsState()
    val analytics by viewModel.analytics.collectAsState()
    val storage by viewModel.storageBreakdown.collectAsState()
    val syncLogs by viewModel.syncLogs.collectAsState()
    val currentTheme by viewModel.currentTheme.collectAsState()
    val isSyncing by viewModel.isCloudSyncing.collectAsState()

    var enteredPin by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
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
                            text = "Gufran's Vault",
                            color = TextPrimary,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        NeonBadge(
                            text = if (isVaultUnlocked) "VAULT UNLOCKED" else "VAULT LOCKED",
                            color = if (isVaultUnlocked) NeonGreen else NeonMagenta
                        )
                    }
                    Text(
                        text = "Biometric Vault • Deep Analytics • OmniCloud Sync",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                GlassIconButton(
                    icon = if (isVaultUnlocked) Icons.Filled.LockOpen else Icons.Filled.Lock,
                    onClick = {
                        if (isVaultUnlocked) viewModel.lockVault() else viewModel.unlockVault("1234")
                    },
                    tint = if (isVaultUnlocked) NeonGreen else NeonMagenta,
                    size = 42.dp,
                    contentDescription = "Lock/Unlock"
                )
            }
        }

        // 1. Private Media Vault Card (Biometric / PIN Keypad)
        item {
            GlowGlassCard(
                modifier = Modifier.fillMaxWidth(),
                glowColor = if (isVaultUnlocked) NeonGreen else NeonMagenta
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Security,
                                contentDescription = null,
                                tint = if (isVaultUnlocked) NeonGreen else NeonMagenta,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Biometric Media Vault",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        if (isVaultUnlocked) {
                            Text(
                                text = "Lock Now",
                                color = NeonMagenta,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { viewModel.lockVault() }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!isVaultUnlocked) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Enter 4-Digit Security PIN or Use Fingerprint",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            // PIN Display Dots
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                repeat(4) { i ->
                                    val isFilled = i < enteredPin.length
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(if (isFilled) NeonMagenta else Color.White.copy(alpha = 0.2f))
                                            .border(1.dp, if (isFilled) NeonMagenta else GlassBorder, CircleShape)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Keypad (1 to 9, 0, Biometric & Clear)
                            val keypad = listOf(
                                listOf("1", "2", "3"),
                                listOf("4", "5", "6"),
                                listOf("7", "8", "9"),
                                listOf("BIO", "0", "CLR")
                            )

                            keypad.forEach { row ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    row.forEach { digit ->
                                        Box(
                                            modifier = Modifier
                                                .size(width = 72.dp, height = 44.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color.White.copy(alpha = 0.08f))
                                                .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                                                .clickable {
                                                    when (digit) {
                                                        "BIO" -> {
                                                            viewModel.unlockVault("1234")
                                                        }
                                                        "CLR" -> {
                                                            if (enteredPin.isNotEmpty()) enteredPin = enteredPin.dropLast(1)
                                                        }
                                                        else -> {
                                                            if (enteredPin.length < 4) {
                                                                val next = enteredPin + digit
                                                                enteredPin = next
                                                                if (next.length == 4) {
                                                                    val ok = viewModel.unlockVault(next)
                                                                    if (!ok) enteredPin = ""
                                                                }
                                                            }
                                                        }
                                                    }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (digit == "BIO") {
                                                Icon(Icons.Filled.Fingerprint, null, tint = NeonGreen, modifier = Modifier.size(24.dp))
                                            } else {
                                                Text(
                                                    text = digit,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Default test PIN: 1234 (or tap fingerprint icon)", color = TextMuted, fontSize = 11.sp)
                        }
                    } else {
                        // Vault contents
                        Column {
                            Text(
                                text = "Secure Hidden Items (${vaultTracks.size + vaultPhotos.size + vaultVideos.size})",
                                color = NeonGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            if (vaultTracks.isEmpty() && vaultPhotos.isEmpty() && vaultVideos.isEmpty()) {
                                Text("No hidden items in vault. Tap 'Move to Vault' on any track, photo, or video to secure it.", color = TextMuted, fontSize = 13.sp)
                            }

                            vaultTracks.forEach { track ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White.copy(alpha = 0.05f))
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.MusicNote, null, tint = NeonMagenta)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(track.title, color = TextPrimary, fontSize = 13.sp, maxLines = 1)
                                    }
                                    Text("Restore", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { viewModel.setAudioVaultLocked(track, false) })
                                }
                            }

                            vaultPhotos.forEach { photo ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White.copy(alpha = 0.05f))
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.PhotoCamera, null, tint = ElectricBlue)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(photo.title, color = TextPrimary, fontSize = 13.sp, maxLines = 1)
                                    }
                                    Text("Restore", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { viewModel.setPhotoVaultLocked(photo, false) })
                                }
                            }

                            vaultVideos.forEach { video ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White.copy(alpha = 0.05f))
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Videocam, null, tint = NeonAmber)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(video.title, color = TextPrimary, fontSize = 13.sp, maxLines = 1)
                                    }
                                    Text("Restore", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { viewModel.setVideoVaultLocked(video, false) })
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Analytical Reporting Tools & Trends
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Analytics, null, tint = NeonCyan, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Usage Analytics & Trends", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        NeonBadge(text = "LIVE STATS", color = NeonCyan)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Listen Time", color = TextSecondary, fontSize = 11.sp)
                            Text("${(analytics?.totalMusicSeconds ?: 0) / 60} mins", color = NeonMagenta, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Column {
                            Text("Watch Time", color = TextSecondary, fontSize = 11.sp)
                            Text("${(analytics?.totalVideoSeconds ?: 0) / 60} mins", color = NeonAmber, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Column {
                            Text("Photos Seen", color = TextSecondary, fontSize = 11.sp)
                            Text("${analytics?.totalPhotosViewed ?: 0}", color = ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Column {
                            Text("AI Queries", color = TextSecondary, fontSize = 11.sp)
                            Text("${analytics?.totalAiSearches ?: 0}", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Storage Distribution Bar
                    Text("Device Storage Allocation", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(100.dp))
                    ) {
                        Box(modifier = Modifier.weight(0.25f).background(NeonMagenta))
                        Box(modifier = Modifier.weight(0.35f).background(ElectricBlue))
                        Box(modifier = Modifier.weight(0.30f).background(NeonAmber))
                        Box(modifier = Modifier.weight(0.10f).background(NeonGreen))
                    }
                }
            }
        }

        // 3. Dynamic Glass Themes Switcher
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.ColorLens, null, tint = NeonPurple, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Dynamic Glass Theme Engine", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(AppThemeMode.values()) { theme ->
                            val isSelected = currentTheme == theme
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        when (theme) {
                                            AppThemeMode.DARK_GLASS -> Color(0xFF141923)
                                            AppThemeMode.CYBERPUNK -> Color(0xFF00381B)
                                            AppThemeMode.SUNSET_AURA -> Color(0xFF3B121C)
                                            AppThemeMode.EMERALD_AURORA -> Color(0xFF0F3124)
                                            AppThemeMode.OLED_BLACK -> Color(0xFF050505)
                                        }
                                    )
                                    .border(1.5.dp, if (isSelected) NeonCyan else GlassBorder, RoundedCornerShape(14.dp))
                                    .clickable { viewModel.setTheme(theme) }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = theme.displayName,
                                    color = if (isSelected) NeonCyan else TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. OmniCloud Sync & Workflow Data Exports
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.CloudSync, null, tint = NeonGreen, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Workflow Automation & Export", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        GlowButton(
                            text = "Backup Cloud",
                            icon = Icons.Filled.CloudSync,
                            onClick = { viewModel.triggerCloudSync() },
                            modifier = Modifier.weight(1f),
                            gradient = Brush.horizontalGradient(listOf(NeonGreen, ElectricBlue))
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                                .clickable {
                                    val json = viewModel.exportMediaMetadataJson()
                                    viewModel.emitToast("Exported JSON Manifest (${json.length} chars)")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Export JSON", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
