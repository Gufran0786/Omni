package com.example.ui.photos

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.PhotoFilter
import com.example.ui.MainViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassIconButton
import com.example.ui.components.GlowButton
import com.example.ui.components.NeonBadge
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoEditorSheet(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val editState by viewModel.photoEditState.collectAsState()
    val state = editState ?: return
    val photo = state.activePhoto ?: return
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0F1523)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassIconButton(
                    icon = Icons.Filled.Close,
                    onClick = onDismiss,
                    size = 40.dp,
                    contentDescription = "Cancel"
                )

                Text(
                    text = "Glass Photo Studio",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                GlassIconButton(
                    icon = Icons.AutoMirrored.Filled.RotateRight,
                    onClick = { viewModel.rotatePhoto() },
                    tint = NeonCyan,
                    size = 40.dp,
                    contentDescription = "Rotate 90"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Preview Canvas with live filter & rotation
            val colorMatrix = remember(state.selectedFilter, state.brightnessAdjustment, state.contrastAdjustment) {
                val matrix = ColorMatrix()
                when (state.selectedFilter) {
                    PhotoFilter.NOIR -> matrix.setToSaturation(0f)
                    PhotoFilter.SEPIA_VINTAGE -> {
                        matrix.setToSaturation(0.3f)
                    }
                    PhotoFilter.CYBERPUNK -> {
                        matrix.setToSaturation(1.6f)
                    }
                    PhotoFilter.VIVID_NEON -> {
                        matrix.setToSaturation(1.8f)
                    }
                    PhotoFilter.TEAL_ORANGE -> {
                        matrix.setToSaturation(1.4f)
                    }
                    else -> {}
                }
                matrix
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.5.dp, Brush.horizontalGradient(listOf(NeonCyan, NeonMagenta)), RoundedCornerShape(20.dp))
            ) {
                AsyncImage(
                    model = photo.urlOrPath,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(state.rotationDegrees),
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.colorMatrix(colorMatrix)
                )

                NeonBadge(
                    text = state.selectedFilter.filterName,
                    color = NeonCyan,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Glass Filter Presets Carousel
            Text(
                text = "Preset Glass Color Tone",
                color = NeonCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(PhotoFilter.values()) { filter ->
                    val isSelected = state.selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) Brush.horizontalGradient(listOf(NeonCyan, ElectricBlue))
                                else Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.08f)))
                            )
                            .border(1.dp, if (isSelected) NeonCyan else GlassBorder, RoundedCornerShape(14.dp))
                            .clickable { viewModel.selectPhotoFilter(filter) }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = filter.filterName,
                            color = if (isSelected) Color.Black else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Adjustment Sliders (Brightness & Contrast)
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Brightness Boost", color = TextPrimary, fontSize = 13.sp)
                        Text("${(state.brightnessAdjustment * 100).toInt()}%", color = NeonCyan, fontSize = 13.sp)
                    }
                    Slider(
                        value = state.brightnessAdjustment,
                        onValueChange = { viewModel.updateBrightness(it) },
                        valueRange = -0.5f..0.5f,
                        colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("HDR Contrast", color = TextPrimary, fontSize = 13.sp)
                        Text("${(state.contrastAdjustment * 100).toInt()}%", color = NeonMagenta, fontSize = 13.sp)
                    }
                    Slider(
                        value = state.contrastAdjustment,
                        onValueChange = { viewModel.updateContrast(it) },
                        valueRange = 0.5f..1.5f,
                        colors = SliderDefaults.colors(thumbColor = NeonMagenta, activeTrackColor = NeonMagenta)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Save Button
            GlowButton(
                text = "Apply & Save to Gallery",
                icon = Icons.Filled.Check,
                onClick = { viewModel.savePhotoEdits() },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("save_photo_filter_button")
            )
        }
    }
}
