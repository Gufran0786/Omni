package com.example.ui.photos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.PhotoItem
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassIconButton
import com.example.ui.components.NeonBadge
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PhotoViewerDialog(
    photo: PhotoItem,
    onDismiss: () -> Unit,
    onToggleFav: () -> Unit,
    onOpenEditor: () -> Unit,
    onMoveTrash: () -> Unit,
    onLockVault: () -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var showExifSheet by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // High-Res Image with Pinch-To-Zoom & Pan Gesture
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 4f)
                            if (scale > 1f) {
                                offsetX += pan.x
                                offsetY += pan.y
                            } else {
                                offsetX = 0f
                                offsetY = 0f
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = photo.urlOrPath,
                    contentDescription = photo.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offsetX,
                            translationY = offsetY
                        ),
                    contentScale = ContentScale.Fit
                )
            }

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
                    onClick = onDismiss,
                    tint = Color.White,
                    size = 42.dp,
                    contentDescription = "Close"
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = photo.title,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = photo.locationName,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                GlassIconButton(
                    icon = Icons.Filled.Info,
                    onClick = { showExifSheet = !showExifSheet },
                    tint = NeonCyan,
                    size = 42.dp,
                    contentDescription = "Info"
                )
            }

            // EXIF Info Overlay Sheet (Toggleable)
            if (showExifSheet) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .align(Alignment.Center)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("EXIF Details & Smart Tags", color = NeonCyan, fontWeight = FontWeight.Bold)
                            NeonBadge(text = photo.albumName, color = ElectricBlue)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("• Dimensions: ${photo.width} x ${photo.height} px", color = TextPrimary, fontSize = 13.sp)
                        Text("• File Size: ${photo.sizeBytes / 1_000_000.0} MB", color = TextPrimary, fontSize = 13.sp)
                        Text("• Camera Settings: ${photo.aperture} • ${photo.iso}", color = TextPrimary, fontSize = 13.sp)
                        Text("• Smart Tags: ${photo.tags}", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }

            // Bottom Actions Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp, start = 20.dp, end = 20.dp)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF131926).copy(alpha = 0.9f))
                    .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onToggleFav) {
                    Icon(
                        imageVector = if (photo.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (photo.isFavorite) NeonMagenta else Color.White
                    )
                }

                IconButton(onClick = onOpenEditor) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Filter",
                        tint = NeonCyan
                    )
                }

                IconButton(onClick = onLockVault) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Lock to Vault",
                        tint = NeonGreen
                    )
                }

                IconButton(onClick = onMoveTrash) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Trash",
                        tint = Color(0xFFFF5252)
                    )
                }
            }
        }
    }
}
