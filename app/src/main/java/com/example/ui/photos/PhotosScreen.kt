package com.example.ui.photos

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Grid3x3
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.PhotoItem
import com.example.ui.MainViewModel
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
fun PhotosScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val allPhotos by viewModel.allPhotos.collectAsState()
    val favoritePhotos by viewModel.favoritePhotos.collectAsState()
    val trashPhotos by viewModel.trashPhotos.collectAsState()
    val viewingPhoto by viewModel.viewingPhoto.collectAsState()
    val editState by viewModel.photoEditState.collectAsState()

    var selectedAlbum by remember { mutableStateOf("All Photos") }
    var gridColumns by remember { mutableIntStateOf(2) }

    val albums = listOf("All Photos", "Favorites", "Nature & Landscapes", "Travels", "Wallpapers", "Trash")

    val displayedPhotos = when (selectedAlbum) {
        "Favorites" -> favoritePhotos
        "Trash" -> trashPhotos
        "All Photos" -> allPhotos
        else -> allPhotos.filter { it.albumName.contains(selectedAlbum, ignoreCase = true) || it.tags.contains(selectedAlbum, ignoreCase = true) }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(gridColumns),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header
            item(span = { GridItemSpan(gridColumns) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Gufran's Photos",
                                color = TextPrimary,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            NeonBadge(text = "Smart AI Gallery", color = ElectricBlue)
                        }
                        Text(
                            text = "${allPhotos.size} Total Memories • EXIF & Glass Filters",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Row {
                        GlassIconButton(
                            icon = if (gridColumns == 2) Icons.Filled.Grid3x3 else Icons.Filled.GridView,
                            onClick = { gridColumns = if (gridColumns == 2) 3 else 2 },
                            tint = ElectricBlue,
                            size = 40.dp,
                            contentDescription = "Toggle Grid"
                        )
                    }
                }
            }

            // Albums Filter Carousel
            item(span = { GridItemSpan(gridColumns) }) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    items(albums) { album ->
                        val isSelected = selectedAlbum == album
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(
                                    if (isSelected) Brush.horizontalGradient(listOf(ElectricBlue, NeonCyan))
                                    else Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.08f)))
                                )
                                .border(1.dp, if (isSelected) ElectricBlue else GlassBorder, RoundedCornerShape(100.dp))
                                .clickable { selectedAlbum = album }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = album,
                                color = if (isSelected) Color.Black else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Empty state check
            if (displayedPhotos.isEmpty()) {
                item(span = { GridItemSpan(gridColumns) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.PhotoLibrary, null, tint = TextMuted, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("No photos in $selectedAlbum", color = TextSecondary, fontSize = 14.sp)
                        }
                    }
                }
            }

            // Photo Grid Items
            items(displayedPhotos, key = { it.id }) { photo ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
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

                    // Gradient overlay at bottom
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f)),
                                    startY = 60f
                                )
                            )
                    )

                    if (photo.filterApplied != "Normal") {
                        NeonBadge(
                            text = photo.filterApplied,
                            color = NeonCyan,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(6.dp)
                        )
                    }

                    if (photo.isFavorite) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Favorite",
                            tint = NeonMagenta,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(18.dp)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
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
                            text = photo.locationName,
                            color = TextSecondary,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Single Photo High-Res Viewer Dialog Overlay
        if (viewingPhoto != null) {
            PhotoViewerDialog(
                photo = viewingPhoto!!,
                onDismiss = { viewModel.closePhotoViewer() },
                onToggleFav = { viewModel.togglePhotoFavorite(viewingPhoto!!) },
                onOpenEditor = { viewModel.openPhotoEditor(viewingPhoto!!) },
                onMoveTrash = { viewModel.movePhotoToTrash(viewingPhoto!!, !viewingPhoto!!.isTrash) },
                onLockVault = { viewModel.setPhotoVaultLocked(viewingPhoto!!, true) }
            )
        }

        // Photo Glass Editor Sheet Overlay
        if (editState != null) {
            PhotoEditorSheet(
                viewModel = viewModel,
                onDismiss = { viewModel.closePhotoEditor() }
            )
        }
    }
}
