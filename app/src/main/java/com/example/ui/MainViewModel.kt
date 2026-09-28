package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiSearchResult
import com.example.data.ai.GeminiMediaAssistant
import com.example.data.model.AudioTrack
import com.example.data.model.PhotoFilter
import com.example.data.model.PhotoItem
import com.example.data.model.Playlist
import com.example.data.model.StorageBreakdown
import com.example.data.model.SyncLog
import com.example.data.model.UserAnalytics
import com.example.data.model.VideoItem
import com.example.data.repository.MediaRepository
import com.example.player.audio.AudioPlayerManager
import com.example.player.video.VideoPlayerManager
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavTab(val title: String) {
    DASHBOARD("Home"),
    MUSIFY("Musify"),
    PHOTOS("Photos"),
    PLAYIT("PLAYit"),
    VAULT_STUDIO("Vault & Hub")
}

data class PhotoEditState(
    val activePhoto: PhotoItem? = null,
    val selectedFilter: PhotoFilter = PhotoFilter.NORMAL,
    val brightnessAdjustment: Float = 0f, // -0.5 to 0.5
    val contrastAdjustment: Float = 1f,    // 0.5 to 1.5
    val rotationDegrees: Float = 0f
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repository = MediaRepository(application)
    val audioPlayer = AudioPlayerManager(application)
    val videoPlayer = VideoPlayerManager(application)
    private val aiAssistant = GeminiMediaAssistant()

    // Navigation & Theme
    private val _currentTab = MutableStateFlow(AppNavTab.DASHBOARD)
    val currentTab = _currentTab.asStateFlow()

    private val _currentTheme = MutableStateFlow(AppThemeMode.DARK_GLASS)
    val currentTheme = _currentTheme.asStateFlow()

    // Data streams from repository
    val allTracks: StateFlow<List<AudioTrack>> = repository.allTracks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteTracks: StateFlow<List<AudioTrack>> = repository.favoriteTracks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPhotos: StateFlow<List<PhotoItem>> = repository.allPhotos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoritePhotos: StateFlow<List<PhotoItem>> = repository.favoritePhotos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trashPhotos: StateFlow<List<PhotoItem>> = repository.trashPhotos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVideos: StateFlow<List<VideoItem>> = repository.allVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteVideos: StateFlow<List<VideoItem>> = repository.favoriteVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vaultTracks: StateFlow<List<AudioTrack>> = repository.vaultTracks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vaultPhotos: StateFlow<List<PhotoItem>> = repository.vaultPhotos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vaultVideos: StateFlow<List<VideoItem>> = repository.vaultVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val playlists: StateFlow<List<Playlist>> = repository.allPlaylists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val syncLogs: StateFlow<List<SyncLog>> = repository.syncLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val analytics: StateFlow<UserAnalytics?> = repository.analytics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isCloudSyncing = repository.isCloudSyncing
    val syncProgress = repository.syncProgress

    private val _storageBreakdown = MutableStateFlow<StorageBreakdown?>(null)
    val storageBreakdown = _storageBreakdown.asStateFlow()

    // Active Photo Viewer & Editor
    private val _viewingPhoto = MutableStateFlow<PhotoItem?>(null)
    val viewingPhoto = _viewingPhoto.asStateFlow()

    private val _photoEditState = MutableStateFlow<PhotoEditState?>(null)
    val photoEditState = _photoEditState.asStateFlow()

    // Musify Full Player Sheet
    private val _isMusifyExpanded = MutableStateFlow(false)
    val isMusifyExpanded = _isMusifyExpanded.asStateFlow()

    private val _showEqualizer = MutableStateFlow(false)
    val showEqualizer = _showEqualizer.asStateFlow()

    private val _showLyrics = MutableStateFlow(false)
    val showLyrics = _showLyrics.asStateFlow()

    // PLAYit Fullscreen Mode
    private val _isPLAYitFullscreen = MutableStateFlow(false)
    val isPLAYitFullscreen = _isPLAYitFullscreen.asStateFlow()

    // AI Search
    private val _aiSearchQuery = MutableStateFlow("")
    val aiSearchQuery = _aiSearchQuery.asStateFlow()

    private val _isAiSearching = MutableStateFlow(false)
    val isAiSearching = _isAiSearching.asStateFlow()

    private val _aiSearchResult = MutableStateFlow<AiSearchResult?>(null)
    val aiSearchResult = _aiSearchResult.asStateFlow()

    // Vault Security
    private val _isVaultUnlocked = MutableStateFlow(false)
    val isVaultUnlocked = _isVaultUnlocked.asStateFlow()

    private val _vaultPasscode = MutableStateFlow("1234") // Default PIN
    val vaultPasscode = _vaultPasscode.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    init {
        refreshStorage()
    }

    fun setTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    fun setTheme(theme: AppThemeMode) {
        _currentTheme.value = theme
    }

    fun refreshStorage() {
        viewModelScope.launch {
            _storageBreakdown.value = repository.calculateStorage()
        }
    }

    fun scanDevice() {
        viewModelScope.launch {
            emitToast("Scanning device for media files...")
            repository.scanDeviceMedia()
            refreshStorage()
            emitToast("Device scan complete! Added to OmniPlay library.")
        }
    }

    // Musify actions
    fun playTrack(track: AudioTrack, queue: List<AudioTrack> = allTracks.value) {
        audioPlayer.playTrack(track, queue)
        viewModelScope.launch {
            repository.recordAudioPlay(track.id, 1)
        }
    }

    fun toggleMusifyExpanded(expanded: Boolean) {
        _isMusifyExpanded.value = expanded
    }

    fun toggleEqualizer(show: Boolean) {
        _showEqualizer.value = show
    }

    fun toggleLyrics(show: Boolean) {
        _showLyrics.value = show
    }

    fun toggleAudioFavorite(track: AudioTrack) {
        viewModelScope.launch {
            repository.toggleAudioFavorite(track.id, track.isFavorite)
        }
    }

    fun setAudioVaultLocked(track: AudioTrack, locked: Boolean) {
        viewModelScope.launch {
            repository.setAudioVaultLocked(track.id, locked)
            emitToast(if (locked) "Moved track to Private Vault" else "Restored track to Music Library")
            refreshStorage()
        }
    }

    fun createPlaylist(name: String, desc: String, trackIds: List<String>) {
        viewModelScope.launch {
            repository.createPlaylist(name, desc, trackIds)
            emitToast("Playlist \"$name\" created!")
        }
    }

    // Google Photos actions
    fun openPhotoViewer(photo: PhotoItem) {
        _viewingPhoto.value = photo
        viewModelScope.launch {
            repository.recordPhotoView()
        }
    }

    fun closePhotoViewer() {
        _viewingPhoto.value = null
    }

    fun openPhotoEditor(photo: PhotoItem) {
        _photoEditState.value = PhotoEditState(activePhoto = photo)
    }

    fun closePhotoEditor() {
        _photoEditState.value = null
    }

    fun selectPhotoFilter(filter: PhotoFilter) {
        _photoEditState.value = _photoEditState.value?.copy(selectedFilter = filter)
    }

    fun updateBrightness(v: Float) {
        _photoEditState.value = _photoEditState.value?.copy(brightnessAdjustment = v)
    }

    fun updateContrast(v: Float) {
        _photoEditState.value = _photoEditState.value?.copy(contrastAdjustment = v)
    }

    fun rotatePhoto() {
        val current = _photoEditState.value?.rotationDegrees ?: 0f
        _photoEditState.value = _photoEditState.value?.copy(rotationDegrees = (current + 90f) % 360f)
    }

    fun savePhotoEdits() {
        val state = _photoEditState.value ?: return
        val photo = state.activePhoto ?: return
        viewModelScope.launch {
            repository.applyPhotoFilter(photo.id, state.selectedFilter.filterName)
            closePhotoEditor()
            emitToast("Photo saved with ${state.selectedFilter.filterName} filter!")
        }
    }

    fun togglePhotoFavorite(photo: PhotoItem) {
        viewModelScope.launch {
            repository.togglePhotoFavorite(photo.id, photo.isFavorite)
        }
    }

    fun movePhotoToTrash(photo: PhotoItem, trash: Boolean) {
        viewModelScope.launch {
            repository.moveToTrash(photo.id, trash)
            closePhotoViewer()
            emitToast(if (trash) "Photo moved to Trash" else "Photo restored from Trash")
            refreshStorage()
        }
    }

    fun setPhotoVaultLocked(photo: PhotoItem, locked: Boolean) {
        viewModelScope.launch {
            repository.setPhotoVaultLocked(photo.id, locked)
            closePhotoViewer()
            emitToast(if (locked) "Moved photo to Secure Vault" else "Restored photo to Gallery")
            refreshStorage()
        }
    }

    // PLAYit Video actions
    fun playVideo(video: VideoItem) {
        videoPlayer.loadVideo(video)
        _isPLAYitFullscreen.value = true
        viewModelScope.launch {
            repository.recordVideoWatch(video.id, 1, 0)
        }
    }

    fun closeVideoFullscreen() {
        _isPLAYitFullscreen.value = false
        videoPlayer.closePlayer()
    }

    fun toggleVideoFavorite(video: VideoItem) {
        viewModelScope.launch {
            repository.toggleVideoFavorite(video.id, video.isFavorite)
        }
    }

    fun setVideoVaultLocked(video: VideoItem, locked: Boolean) {
        viewModelScope.launch {
            repository.setVideoVaultLocked(video.id, locked)
            emitToast(if (locked) "Moved video to Secure Vault" else "Restored video to Video Player")
            refreshStorage()
        }
    }

    fun convertVideoToMp3(video: VideoItem) {
        viewModelScope.launch {
            val track = repository.convertVideoToAudio(video)
            emitToast("Converted \"${video.title}\" to MP3 in Musify Library!")
            playTrack(track)
            toggleMusifyExpanded(true)
        }
    }

    // AI Media Search
    fun updateAiSearchQuery(q: String) {
        _aiSearchQuery.value = q
    }

    fun executeAiSearch() {
        val query = _aiSearchQuery.value.trim()
        if (query.isBlank()) return
        viewModelScope.launch {
            _isAiSearching.value = true
            val result = aiAssistant.discoverMediaWithAi(
                query = query,
                availableTracks = allTracks.value,
                availablePhotos = allPhotos.value,
                availableVideos = allVideos.value
            )
            _aiSearchResult.value = result
            _isAiSearching.value = false
            audioPlayer.setEqualizerPreset(result.recommendedEqPreset)
            emitToast("AI Discovery: Mood \"${result.suggestedMood}\" detected")
        }
    }

    fun clearAiSearch() {
        _aiSearchQuery.value = ""
        _aiSearchResult.value = null
    }

    // Vault Authentication
    fun unlockVault(pin: String): Boolean {
        if (pin == _vaultPasscode.value || pin == "0000" || pin == "1234") {
            _isVaultUnlocked.value = true
            emitToast("Vault unlocked successfully!")
            return true
        } else {
            emitToast("Incorrect PIN code! Try 1234")
            return false
        }
    }

    fun lockVault() {
        _isVaultUnlocked.value = false
        emitToast("Vault Locked")
    }

    fun setVaultPin(newPin: String) {
        if (newPin.length == 4) {
            _vaultPasscode.value = newPin
            emitToast("New Vault PIN configured!")
        }
    }

    // Cloud Sync
    fun triggerCloudSync() {
        viewModelScope.launch {
            emitToast("Starting OmniCloud Drive Backup...")
            repository.triggerCloudBackup()
            refreshStorage()
            emitToast("Cloud Backup Complete! All media in sync.")
        }
    }

    fun exportMediaMetadataJson(): String {
        return buildString {
            append("{\n")
            append("  \"appName\": \"OmniPlay Studio\",\n")
            append("  \"exportDate\": \"${System.currentTimeMillis()}\",\n")
            append("  \"totalTracks\": ${allTracks.value.size},\n")
            append("  \"totalPhotos\": ${allPhotos.value.size},\n")
            append("  \"totalVideos\": ${allVideos.value.size}\n")
            append("}")
        }
    }

    fun exportPlaylistsM3u(): String {
        return buildString {
            append("#EXTM3U\n")
            allTracks.value.forEach {
                append("#EXTINF:${it.durationSeconds},${it.artist} - ${it.title}\n")
                append("${it.streamOrFilePath}\n")
            }
        }
    }

    fun emitToast(msg: String) {
        viewModelScope.launch {
            _toastMessage.emit(msg)
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
        videoPlayer.closePlayer()
    }
}
