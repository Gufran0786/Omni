package com.example.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.data.local.OmniDatabase
import com.example.data.model.AudioTrack
import com.example.data.model.MediaType
import com.example.data.model.PhotoItem
import com.example.data.model.Playlist
import com.example.data.model.StorageBreakdown
import com.example.data.model.SyncLog
import com.example.data.model.SyncState
import com.example.data.model.UserAnalytics
import com.example.data.model.VideoItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class MediaRepository(private val context: Context) {

    private val db = OmniDatabase.getDatabase(context)
    private val audioDao = db.audioDao()
    private val photoDao = db.photoDao()
    private val videoDao = db.videoDao()
    private val playlistDao = db.playlistDao()
    private val syncLogDao = db.syncLogDao()
    private val analyticsDao = db.analyticsDao()

    val allTracks: Flow<List<AudioTrack>> = audioDao.getAllTracks()
    val favoriteTracks: Flow<List<AudioTrack>> = audioDao.getFavoriteTracks()
    val vaultTracks: Flow<List<AudioTrack>> = audioDao.getVaultTracks()

    val allPhotos: Flow<List<PhotoItem>> = photoDao.getAllPhotos()
    val favoritePhotos: Flow<List<PhotoItem>> = photoDao.getFavoritePhotos()
    val trashPhotos: Flow<List<PhotoItem>> = photoDao.getTrashPhotos()
    val vaultPhotos: Flow<List<PhotoItem>> = photoDao.getVaultPhotos()

    val allVideos: Flow<List<VideoItem>> = videoDao.getAllVideos()
    val favoriteVideos: Flow<List<VideoItem>> = videoDao.getFavoriteVideos()
    val vaultVideos: Flow<List<VideoItem>> = videoDao.getVaultVideos()

    val allPlaylists: Flow<List<Playlist>> = playlistDao.getAllPlaylists()
    val syncLogs: Flow<List<SyncLog>> = syncLogDao.getRecentLogs()
    val analytics: Flow<UserAnalytics?> = analyticsDao.getAnalytics()

    private val _isCloudSyncing = MutableStateFlow(false)
    val isCloudSyncing = _isCloudSyncing.asStateFlow()

    private val _syncProgress = MutableStateFlow(100)
    val syncProgress = _syncProgress.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialMediaIfEmpty()
        }
    }

    private suspend fun seedInitialMediaIfEmpty() = withContext(Dispatchers.IO) {
        val tracks = audioDao.getAllTracks().first()
        if (tracks.isEmpty()) {
            val initialTracks = listOf(
                AudioTrack(
                    id = "track_1",
                    title = "Midnight Cyber City (Synthwave)",
                    artist = "Neon Skyline",
                    album = "Retroverse Vol. 1",
                    durationSeconds = 214,
                    streamOrFilePath = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                    coverArtUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=500&q=80",
                    genre = "Synthwave / Electronic",
                    bitrateKbps = 320,
                    lyrics = """[00:00.00] (Instrumental Neon Beats)
[00:15.00] Electric shadows cast upon the rain
[00:25.00] Flying high across the neon plain
[00:40.00] Feel the rhythm surging through the wire
[00:55.00] Burning through the endless city fire
[01:15.00] Midnight speed, we never look back now
[01:30.00] Into tomorrow, we ignite the sound"""
                ),
                AudioTrack(
                    id = "track_2",
                    title = "Acoustic Horizon Waves",
                    artist = "Luna & The Woods",
                    album = "Nordic Pine",
                    durationSeconds = 186,
                    streamOrFilePath = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
                    coverArtUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500&q=80",
                    genre = "Indie Acoustic",
                    bitrateKbps = 320,
                    lyrics = """[00:00.00] Soft wind through the pine trees
[00:18.00] Golden sun over the mountain ridge
[00:35.00] Step by step beside the river bridge
[00:52.00] We will find where the journey leads
[01:10.00] Clear blue skies and warm melodies"""
                ),
                AudioTrack(
                    id = "track_3",
                    title = "Quantum Echoes (Bass Boost)",
                    artist = "Hyperdrive Beats",
                    album = "Dimensions",
                    durationSeconds = 245,
                    streamOrFilePath = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
                    coverArtUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500&q=80",
                    genre = "EDM / Bass",
                    bitrateKbps = 320,
                    lyrics = """[00:00.00] Drop the frequency to 30Hz
[00:12.00] Subwoofer vibration starts to rumble
[00:28.00] 3... 2... 1... Hyperdrive engaged!
[00:45.00] (Heavy Bass Drop Pulse)
[01:20.00] Echoes bouncing through quantum space"""
                ),
                AudioTrack(
                    id = "track_4",
                    title = "Lo-Fi Coffee & Raindrops",
                    artist = "Chilled Sakura",
                    album = "Sunday Study",
                    durationSeconds = 175,
                    streamOrFilePath = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
                    coverArtUrl = "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?w=500&q=80",
                    genre = "Lo-Fi Hip Hop",
                    bitrateKbps = 256,
                    lyrics = """[00:00.00] Warm steam rising from the ceramic cup
[00:20.00] Vinyl crackle spinning on the record player
[00:42.00] Raindrops tapping gently against the window pane
[01:05.00] Peaceful moments in a bustling world"""
                ),
                AudioTrack(
                    id = "track_5",
                    title = "Celestial Symphony in D Minor",
                    artist = "Vortex Philharmonic",
                    album = "Cosmic Odyssey",
                    durationSeconds = 290,
                    streamOrFilePath = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3",
                    coverArtUrl = "https://images.unsplash.com/photo-1465847899084-d164df4dedc6?w=500&q=80",
                    genre = "Orchestral Cinematic",
                    bitrateKbps = 320,
                    lyrics = """[00:00.00] (Grand Violins & Timpani Opening)
[00:30.00] Brass crescendo reaching into the cosmos
[01:05.00] Harp arpeggios descending like stardust
[01:45.00] Full orchestra soaring across infinity"""
                )
            )
            audioDao.insertAll(initialTracks)

            val initialPlaylist = Playlist(
                id = "pl_favorites_mix",
                name = "Cyberpunk Chill Lounge",
                description = "Ultra high-fidelity synth and lo-fi tracks for coding and chilling",
                coverArtUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=500&q=80",
                trackIdsJson = """["track_1", "track_4", "track_2"]"""
            )
            playlistDao.insertPlaylist(initialPlaylist)
        }

        val photos = photoDao.getAllPhotos().first()
        if (photos.isEmpty()) {
            val initialPhotos = listOf(
                PhotoItem(
                    id = "photo_1",
                    title = "Neon Tokyo Skyline Night",
                    urlOrPath = "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?w=1080&q=80",
                    tags = "Urban, City, Neon, Japan, Night",
                    albumName = "Travels",
                    locationName = "Shinjuku, Tokyo",
                    iso = "ISO 400",
                    aperture = "f/1.4"
                ),
                PhotoItem(
                    id = "photo_2",
                    title = "Alpine Sunset Golden Hour",
                    urlOrPath = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=1080&q=80",
                    tags = "Nature, Mountains, Sunset, Golden Hour",
                    albumName = "Nature & Landscapes",
                    locationName = "Swiss Alps",
                    iso = "ISO 100",
                    aperture = "f/2.8"
                ),
                PhotoItem(
                    id = "photo_3",
                    title = "Emerald Aurora Borealis",
                    urlOrPath = "https://images.unsplash.com/photo-1531366936337-7c912a4589a7?w=1080&q=80",
                    tags = "Aurora, Night Sky, Stars, Arctic",
                    albumName = "Nature & Landscapes",
                    locationName = "Tromsø, Norway",
                    iso = "ISO 1600",
                    aperture = "f/1.8"
                ),
                PhotoItem(
                    id = "photo_4",
                    title = "Minimalist Cyberpunk Architecture",
                    urlOrPath = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=1080&q=80",
                    tags = "Architecture, Geometry, Modern, Glass",
                    albumName = "Design Inspiration",
                    locationName = "Singapore",
                    iso = "ISO 200",
                    aperture = "f/4.0"
                ),
                PhotoItem(
                    id = "photo_5",
                    title = "Tropical Coral Reef Dive",
                    urlOrPath = "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=1080&q=80",
                    tags = "Ocean, Underwater, Animals, Summer",
                    albumName = "Travels",
                    locationName = "Great Barrier Reef",
                    iso = "ISO 250",
                    aperture = "f/2.0"
                ),
                PhotoItem(
                    id = "photo_6",
                    title = "Cosmic Galaxy Nebula Telescope",
                    urlOrPath = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=1080&q=80",
                    tags = "Space, Stars, Galaxy, Sci-Fi",
                    albumName = "Wallpapers",
                    locationName = "Deep Space",
                    iso = "ISO 3200",
                    aperture = "f/1.2"
                )
            )
            photoDao.insertAll(initialPhotos)
        }

        val videos = videoDao.getAllVideos().first()
        if (videos.isEmpty()) {
            val initialVideos = listOf(
                VideoItem(
                    id = "vid_1",
                    title = "Big Buck Bunny - 4K 60FPS Showcase",
                    durationSeconds = 596,
                    urlOrPath = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    thumbnailUri = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=800&q=80",
                    resolution = "4K UHD 60FPS",
                    sizeBytes = 158_000_000L
                ),
                VideoItem(
                    id = "vid_2",
                    title = "Elephants Dream - Open Movie Project",
                    durationSeconds = 653,
                    urlOrPath = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                    thumbnailUri = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&q=80",
                    resolution = "1080p FHD HDR",
                    sizeBytes = 142_000_000L
                ),
                VideoItem(
                    id = "vid_3",
                    title = "Cyber City Drone Hyperlapse",
                    durationSeconds = 180,
                    urlOrPath = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    thumbnailUri = "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?w=800&q=80",
                    resolution = "1080p 60FPS",
                    sizeBytes = 48_000_000L
                ),
                VideoItem(
                    id = "vid_4",
                    title = "Ultra High Speed Racing Showcase",
                    durationSeconds = 120,
                    urlOrPath = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                    thumbnailUri = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800&q=80",
                    resolution = "1080p 60FPS",
                    sizeBytes = 32_000_000L
                )
            )
            videoDao.insertAll(initialVideos)
        }

        val analytics = analyticsDao.getAnalytics().first()
        if (analytics == null) {
            analyticsDao.saveAnalytics(
                UserAnalytics(
                    id = 1,
                    totalMusicSeconds = 4820,
                    totalVideoSeconds = 2430,
                    totalPhotosViewed = 42,
                    totalAiSearches = 8
                )
            )
        }
    }

    suspend fun scanDeviceMedia() = withContext(Dispatchers.IO) {
        val newTracks = mutableListOf<AudioTrack>()
        val newPhotos = mutableListOf<PhotoItem>()
        val newVideos = mutableListOf<VideoItem>()

        // 1. Scan Audio
        try {
            val audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val audioProjection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA
            )
            val audioCursor = context.contentResolver.query(
                audioUri,
                audioProjection,
                "${MediaStore.Audio.Media.IS_MUSIC} != 0",
                null,
                "${MediaStore.Audio.Media.DATE_ADDED} DESC"
            )
            audioCursor?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

                while (cursor.moveToNext() && newTracks.size < 50) {
                    val id = cursor.getLong(idCol)
                    val contentUri = ContentUris.withAppendedId(audioUri, id)
                    val title = cursor.getString(titleCol) ?: "Unknown Track"
                    val artist = cursor.getString(artistCol) ?: "Unknown Artist"
                    val album = cursor.getString(albumCol) ?: "Local Music"
                    val durationMs = cursor.getInt(durationCol)
                    val filePath = cursor.getString(dataCol) ?: contentUri.toString()

                    newTracks.add(
                        AudioTrack(
                            id = "device_audio_$id",
                            title = title,
                            artist = artist,
                            album = album,
                            durationSeconds = (durationMs / 1000).coerceAtLeast(1),
                            streamOrFilePath = filePath,
                            coverArtUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500&q=80",
                            genre = "Device Audio"
                        )
                    )
                }
            }
            if (newTracks.isNotEmpty()) {
                audioDao.insertAll(newTracks)
            }
        } catch (_: Exception) {}

        // 2. Scan Photos
        try {
            val imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val imageProjection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATA
            )
            val imgCursor = context.contentResolver.query(
                imageUri,
                imageProjection,
                null,
                null,
                "${MediaStore.Images.Media.DATE_ADDED} DESC"
            )
            imgCursor?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

                while (cursor.moveToNext() && newPhotos.size < 50) {
                    val id = cursor.getLong(idCol)
                    val contentUri = ContentUris.withAppendedId(imageUri, id)
                    val name = cursor.getString(nameCol) ?: "Photo"
                    val date = cursor.getLong(dateCol) * 1000L
                    val size = cursor.getLong(sizeCol)
                    val path = cursor.getString(dataCol) ?: contentUri.toString()

                    newPhotos.add(
                        PhotoItem(
                            id = "device_photo_$id",
                            title = name,
                            urlOrPath = path,
                            dateTaken = date,
                            sizeBytes = size,
                            tags = "Device, Camera, Gallery",
                            albumName = "Device Photos"
                        )
                    )
                }
            }
            if (newPhotos.isNotEmpty()) {
                photoDao.insertAll(newPhotos)
            }
        } catch (_: Exception) {}

        // 3. Scan Videos
        try {
            val videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            val videoProjection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATA
            )
            val vidCursor = context.contentResolver.query(
                videoUri,
                videoProjection,
                null,
                null,
                "${MediaStore.Video.Media.DATE_ADDED} DESC"
            )
            vidCursor?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val durCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)

                while (cursor.moveToNext() && newVideos.size < 30) {
                    val id = cursor.getLong(idCol)
                    val contentUri = ContentUris.withAppendedId(videoUri, id)
                    val name = cursor.getString(nameCol) ?: "Video"
                    val durationMs = cursor.getInt(durCol)
                    val size = cursor.getLong(sizeCol)
                    val path = cursor.getString(dataCol) ?: contentUri.toString()

                    newVideos.add(
                        VideoItem(
                            id = "device_video_$id",
                            title = name,
                            durationSeconds = (durationMs / 1000).coerceAtLeast(1),
                            urlOrPath = path,
                            thumbnailUri = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=800&q=80",
                            sizeBytes = size
                        )
                    )
                }
            }
            if (newVideos.isNotEmpty()) {
                videoDao.insertAll(newVideos)
            }
        } catch (_: Exception) {}
    }

    // Musify Audio operations
    suspend fun toggleAudioFavorite(trackId: String, currentFav: Boolean) {
        audioDao.setFavorite(trackId, !currentFav)
    }

    suspend fun setAudioVaultLocked(trackId: String, locked: Boolean) {
        audioDao.setVaultLocked(trackId, locked)
    }

    suspend fun recordAudioPlay(trackId: String, secondsPlayed: Long) {
        audioDao.incrementPlayCount(trackId)
        analyticsDao.addMusicTime(secondsPlayed)
    }

    suspend fun createPlaylist(name: String, desc: String, trackIds: List<String>) {
        val playlist = Playlist(
            id = "pl_${UUID.randomUUID().toString().take(8)}",
            name = name,
            description = desc,
            coverArtUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=500&q=80",
            trackIdsJson = "[${trackIds.joinToString(",") { "\"$it\"" }}]"
        )
        playlistDao.insertPlaylist(playlist)
    }

    // Google Photos operations
    suspend fun togglePhotoFavorite(photoId: String, currentFav: Boolean) {
        photoDao.setFavorite(photoId, !currentFav)
    }

    suspend fun moveToTrash(photoId: String, isTrash: Boolean) {
        photoDao.setTrash(photoId, isTrash)
    }

    suspend fun setPhotoVaultLocked(photoId: String, locked: Boolean) {
        photoDao.setVaultLocked(photoId, locked)
    }

    suspend fun applyPhotoFilter(photoId: String, filterName: String) {
        photoDao.updateFilter(photoId, filterName)
    }

    suspend fun recordPhotoView() {
        analyticsDao.incrementPhotoView()
    }

    // PLAYit Video operations
    suspend fun toggleVideoFavorite(videoId: String, currentFav: Boolean) {
        videoDao.setFavorite(videoId, !currentFav)
    }

    suspend fun setVideoVaultLocked(videoId: String, locked: Boolean) {
        videoDao.setVaultLocked(videoId, locked)
    }

    suspend fun recordVideoWatch(videoId: String, seconds: Long, progress: Int) {
        videoDao.updateWatchProgress(videoId, progress)
        analyticsDao.addVideoTime(seconds)
    }

    // Video to MP3 Conversion Tool
    suspend fun convertVideoToAudio(video: VideoItem): AudioTrack = withContext(Dispatchers.IO) {
        val newTrack = AudioTrack(
            id = "audio_extracted_${UUID.randomUUID().toString().take(8)}",
            title = "[Extracted Audio] ${video.title}",
            artist = "PLAYit Extractor",
            album = "Extracted Soundtracks",
            durationSeconds = video.durationSeconds,
            streamOrFilePath = video.urlOrPath,
            coverArtUrl = video.thumbnailUri,
            genre = "Soundtrack / Extracted",
            bitrateKbps = 320
        )
        audioDao.insertTrack(newTrack)
        syncLogDao.insertLog(
            SyncLog(
                mediaName = newTrack.title,
                mediaType = MediaType.AUDIO,
                action = "EXTRACTED_AUDIO",
                status = "SUCCESS",
                cloudProvider = "Local PLAYit Engine"
            )
        )
        newTrack
    }

    // Cloud Sync Simulation
    suspend fun triggerCloudBackup() = withContext(Dispatchers.IO) {
        _isCloudSyncing.value = true
        _syncProgress.value = 0

        for (i in 1..10) {
            delay(150)
            _syncProgress.value = i * 10
        }

        syncLogDao.insertLog(
            SyncLog(
                mediaName = "Full Studio Media Vault Backup",
                mediaType = MediaType.AUDIO,
                action = "CLOUD_SYNC",
                status = "SUCCESS",
                cloudProvider = "OmniPlay Cloud Drive"
            )
        )
        _isCloudSyncing.value = false
    }

    // Storage Calculation
    suspend fun calculateStorage(): StorageBreakdown = withContext(Dispatchers.IO) {
        val tracks = audioDao.getAllTracks().first()
        val photos = photoDao.getAllPhotos().first()
        val videos = videoDao.getAllVideos().first()
        val vaultTracks = audioDao.getVaultTracks().first()
        val vaultPhotos = photoDao.getVaultPhotos().first()
        val vaultVideos = videoDao.getVaultVideos().first()

        val musicBytes = tracks.sumOf { it.durationSeconds * 40_000L }
        val photoBytes = photos.sumOf { it.sizeBytes }
        val videoBytes = videos.sumOf { it.sizeBytes }
        val vaultBytes = (vaultTracks.sumOf { it.durationSeconds * 40_000L }) +
                (vaultPhotos.sumOf { it.sizeBytes }) +
                (vaultVideos.sumOf { it.sizeBytes })

        val totalUsed = musicBytes + photoBytes + videoBytes + vaultBytes
        val freeBytes = 64_000_000_000L - totalUsed

        StorageBreakdown(
            musicBytes = musicBytes,
            photoBytes = photoBytes,
            videoBytes = videoBytes,
            vaultBytes = vaultBytes,
            totalUsedBytes = totalUsed,
            freeDeviceBytes = freeBytes.coerceAtLeast(1_000_000_000L)
        )
    }
}
