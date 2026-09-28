package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppNavTab
import com.example.ui.MainViewModel
import com.example.ui.components.MiniPlayerBar
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.musify.MusifyPlayerSheet
import com.example.ui.musify.MusifyScreen
import com.example.ui.photos.PhotosScreen
import com.example.ui.theme.DarkGlassBackground
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.OmniPlayTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.vault.VaultAndHubScreen
import com.example.ui.video.PLAYitScreen
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.currentTheme.collectAsState()
            val currentTab by viewModel.currentTab.collectAsState()
            val isMusifyExpanded by viewModel.isMusifyExpanded.collectAsState()
            val isPLAYitFullscreen by viewModel.isPLAYitFullscreen.collectAsState()
            val viewingPhoto by viewModel.viewingPhoto.collectAsState()
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                viewModel.toastMessage.collectLatest { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }

            // Custom Back Navigation Handler
            BackHandler(enabled = isMusifyExpanded || isPLAYitFullscreen || viewingPhoto != null || currentTab != AppNavTab.DASHBOARD) {
                when {
                    isMusifyExpanded -> viewModel.toggleMusifyExpanded(false)
                    isPLAYitFullscreen -> viewModel.closeVideoFullscreen()
                    viewingPhoto != null -> viewModel.closePhotoViewer()
                    currentTab != AppNavTab.DASHBOARD -> viewModel.setTab(AppNavTab.DASHBOARD)
                }
            }

            OmniPlayTheme(themeMode = themeMode) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkGlassBackground)
                ) {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.statusBars),
                        containerColor = Color.Transparent,
                        bottomBar = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .windowInsetsPadding(WindowInsets.navigationBars)
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    // Docked Mini Player Bar
                                    MiniPlayerBar(viewModel = viewModel)

                                    // Floating Glass Navigation Bar
                                    GlassBottomNavigation(
                                        currentTab = currentTab,
                                        onTabSelected = { viewModel.setTab(it) }
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            Crossfade(targetState = currentTab, label = "tab_crossfade") { tab ->
                                when (tab) {
                                    AppNavTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                                    AppNavTab.MUSIFY -> MusifyScreen(viewModel = viewModel)
                                    AppNavTab.PHOTOS -> PhotosScreen(viewModel = viewModel)
                                    AppNavTab.PLAYIT -> PLAYitScreen(viewModel = viewModel)
                                    AppNavTab.VAULT_STUDIO -> VaultAndHubScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }

                    // Full Musify Player Sheet
                    MusifyPlayerSheet(viewModel = viewModel)
                }
            }
        }
    }
}

data class NavItem(
    val tab: AppNavTab,
    val label: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector,
    val activeColor: Color
)

@Composable
fun GlassBottomNavigation(
    currentTab: AppNavTab,
    onTabSelected: (AppNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem(AppNavTab.DASHBOARD, "Home", Icons.Filled.Home, Icons.Outlined.Home, NeonCyan),
        NavItem(AppNavTab.MUSIFY, "Musify", Icons.Filled.MusicNote, Icons.Outlined.MusicNote, NeonMagenta),
        NavItem(AppNavTab.PHOTOS, "Photos", Icons.Filled.PhotoLibrary, Icons.Outlined.PhotoLibrary, Color(0xFF00C6FF)),
        NavItem(AppNavTab.PLAYIT, "PLAYit", Icons.Filled.Videocam, Icons.Outlined.Videocam, Color(0xFFFF9900)),
        NavItem(AppNavTab.VAULT_STUDIO, "Vault", Icons.Filled.Lock, Icons.Outlined.Lock, Color(0xFF00FF88))
    )

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF0D121F).copy(alpha = 0.95f))
            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp)),
        containerColor = Color.Transparent,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            val isSelected = currentTab == item.tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(item.tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.activeIcon else item.inactiveIcon,
                        contentDescription = item.label,
                        tint = if (isSelected) item.activeColor else TextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (isSelected) item.activeColor else TextMuted,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = item.activeColor.copy(alpha = 0.15f)
                ),
                modifier = Modifier.testTag("nav_${item.label.lowercase()}")
            )
        }
    }
}
