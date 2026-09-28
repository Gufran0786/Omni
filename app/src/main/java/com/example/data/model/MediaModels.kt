package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MediaType {
    AUDIO,
    PHOTO,
    VIDEO
}

enum class SyncState {
    SYNCED,
    SYNCING,
    PENDING,
    OFFLINE_ONLY,
    FAILED
}

@Entity(tableName = "audio_tracks")
data class AudioTrack(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationSeconds: Int,
    val streamOrFilePath: String,
    val coverArtUrl: String,
    val genre: String,
    val lyrics: String = "",
    val isFavorite: Boolean = false,
    val isVaultLocked: Boolean = false,
    val syncState: SyncState = SyncState.SYNCED,
    val playCount: Int = 0,
    val bitrateKbps: Int = 320,
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "photo_items")
data class PhotoItem(
    @PrimaryKey val id: String,
    val title: String,
    val urlOrPath: String,
    val dateTaken: Long = System.currentTimeMillis(),
    val width: Int = 1920,
    val height: Int = 1080,
    val sizeBytes: Long = 2_450_000L,
    val tags: String = "All, Memories", // Comma-separated
    val albumName: String = "Camera",
    val isFavorite: Boolean = false,
    val isVaultLocked: Boolean = false,
    val isTrash: Boolean = false,
    val filterApplied: String = "Normal",
    val syncState: SyncState = SyncState.SYNCED,
    val locationName: String = "Earth",
    val iso: String = "ISO 100",
    val aperture: String = "f/1.8"
)

@Entity(tableName = "video_items")
data class VideoItem(
    @PrimaryKey val id: String,
    val title: String,
    val durationSeconds: Int,
    val urlOrPath: String,
    val thumbnailUri: String,
    val resolution: String = "1080p FHD",
    val sizeBytes: Long = 45_000_000L,
    val isFavorite: Boolean = false,
    val isVaultLocked: Boolean = false,
    val syncState: SyncState = SyncState.SYNCED,
    val lastWatchProgressSeconds: Int = 0,
    val playCount: Int = 0,
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey val id: String,
    val name: String,
    val description: String = "",
    val coverArtUrl: String = "",
    val trackIdsJson: String = "[]",
    val createdTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "vault_items")
data class VaultItem(
    @PrimaryKey val id: String,
    val originalMediaId: String,
    val mediaType: MediaType,
    val title: String,
    val lockedAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "sync_logs")
data class SyncLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mediaName: String,
    val mediaType: MediaType,
    val action: String, // "UPLOADED", "BACKED_UP", "RESTORED", "DOWNLOADED"
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "SUCCESS",
    val cloudProvider: String = "OmniCloud Drive"
)

@Entity(tableName = "user_analytics")
data class UserAnalytics(
    @PrimaryKey val id: Int = 1,
    val totalMusicSeconds: Long = 0,
    val totalVideoSeconds: Long = 0,
    val totalPhotosViewed: Long = 0,
    val totalAiSearches: Int = 0,
    val lastActiveTimestamp: Long = System.currentTimeMillis()
)

data class LyricLine(
    val timeMs: Long,
    val text: String
)

enum class PhotoFilter(val filterName: String) {
    NORMAL("Original"),
    VIVID_NEON("Cyber Glow"),
    WARM_GOLD("Vivid Sun"),
    NOIR("Noir Mono"),
    CYBERPUNK("Neon Sunset"),
    SEPIA_VINTAGE("Vintage Sepia"),
    TEAL_ORANGE("Teal & Orange"),
    HDR_CRISP("HDR Crisp"),
    EMERALD_MINT("Aurora Mint")
}

data class StorageBreakdown(
    val musicBytes: Long,
    val photoBytes: Long,
    val videoBytes: Long,
    val vaultBytes: Long,
    val totalUsedBytes: Long,
    val freeDeviceBytes: Long
)
