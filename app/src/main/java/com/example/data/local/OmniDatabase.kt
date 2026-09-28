package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.data.model.AudioTrack
import com.example.data.model.PhotoItem
import com.example.data.model.Playlist
import com.example.data.model.SyncLog
import com.example.data.model.UserAnalytics
import com.example.data.model.VaultItem
import com.example.data.model.VideoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioDao {
    @Query("SELECT * FROM audio_tracks WHERE isVaultLocked = 0 ORDER BY addedTimestamp DESC")
    fun getAllTracks(): Flow<List<AudioTrack>>

    @Query("SELECT * FROM audio_tracks WHERE isFavorite = 1 AND isVaultLocked = 0")
    fun getFavoriteTracks(): Flow<List<AudioTrack>>

    @Query("SELECT * FROM audio_tracks WHERE isVaultLocked = 1")
    fun getVaultTracks(): Flow<List<AudioTrack>>

    @Query("SELECT * FROM audio_tracks WHERE id = :id")
    suspend fun getTrackById(id: String): AudioTrack?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: AudioTrack)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tracks: List<AudioTrack>)

    @Update
    suspend fun updateTrack(track: AudioTrack)

    @Delete
    suspend fun deleteTrack(track: AudioTrack)

    @Query("UPDATE audio_tracks SET isFavorite = :isFav WHERE id = :id")
    suspend fun setFavorite(id: String, isFav: Boolean)

    @Query("UPDATE audio_tracks SET isVaultLocked = :isVault WHERE id = :id")
    suspend fun setVaultLocked(id: String, isVault: Boolean)

    @Query("UPDATE audio_tracks SET playCount = playCount + 1 WHERE id = :id")
    suspend fun incrementPlayCount(id: String)
}

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photo_items WHERE isTrash = 0 AND isVaultLocked = 0 ORDER BY dateTaken DESC")
    fun getAllPhotos(): Flow<List<PhotoItem>>

    @Query("SELECT * FROM photo_items WHERE isFavorite = 1 AND isTrash = 0 AND isVaultLocked = 0")
    fun getFavoritePhotos(): Flow<List<PhotoItem>>

    @Query("SELECT * FROM photo_items WHERE isTrash = 1")
    fun getTrashPhotos(): Flow<List<PhotoItem>>

    @Query("SELECT * FROM photo_items WHERE isVaultLocked = 1")
    fun getVaultPhotos(): Flow<List<PhotoItem>>

    @Query("SELECT * FROM photo_items WHERE id = :id")
    suspend fun getPhotoById(id: String): PhotoItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(photos: List<PhotoItem>)

    @Update
    suspend fun updatePhoto(photo: PhotoItem)

    @Delete
    suspend fun deletePhoto(photo: PhotoItem)

    @Query("UPDATE photo_items SET isFavorite = :isFav WHERE id = :id")
    suspend fun setFavorite(id: String, isFav: Boolean)

    @Query("UPDATE photo_items SET isTrash = :isTrash WHERE id = :id")
    suspend fun setTrash(id: String, isTrash: Boolean)

    @Query("UPDATE photo_items SET isVaultLocked = :isVault WHERE id = :id")
    suspend fun setVaultLocked(id: String, isVault: Boolean)

    @Query("UPDATE photo_items SET filterApplied = :filter WHERE id = :id")
    suspend fun updateFilter(id: String, filter: String)
}

@Dao
interface VideoDao {
    @Query("SELECT * FROM video_items WHERE isVaultLocked = 0 ORDER BY addedTimestamp DESC")
    fun getAllVideos(): Flow<List<VideoItem>>

    @Query("SELECT * FROM video_items WHERE isFavorite = 1 AND isVaultLocked = 0")
    fun getFavoriteVideos(): Flow<List<VideoItem>>

    @Query("SELECT * FROM video_items WHERE isVaultLocked = 1")
    fun getVaultVideos(): Flow<List<VideoItem>>

    @Query("SELECT * FROM video_items WHERE id = :id")
    suspend fun getVideoById(id: String): VideoItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(videos: List<VideoItem>)

    @Update
    suspend fun updateVideo(video: VideoItem)

    @Delete
    suspend fun deleteVideo(video: VideoItem)

    @Query("UPDATE video_items SET isFavorite = :isFav WHERE id = :id")
    suspend fun setFavorite(id: String, isFav: Boolean)

    @Query("UPDATE video_items SET isVaultLocked = :isVault WHERE id = :id")
    suspend fun setVaultLocked(id: String, isVault: Boolean)

    @Query("UPDATE video_items SET lastWatchProgressSeconds = :progress, playCount = playCount + 1 WHERE id = :id")
    suspend fun updateWatchProgress(id: String, progress: Int)
}

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY createdTimestamp DESC")
    fun getAllPlaylists(): Flow<List<Playlist>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist)

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)
}

@Dao
interface SyncLogDao {
    @Query("SELECT * FROM sync_logs ORDER BY timestamp DESC LIMIT 50")
    fun getRecentLogs(): Flow<List<SyncLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: SyncLog)

    @Query("DELETE FROM sync_logs")
    suspend fun clearLogs()
}

@Dao
interface AnalyticsDao {
    @Query("SELECT * FROM user_analytics WHERE id = 1")
    fun getAnalytics(): Flow<UserAnalytics?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAnalytics(analytics: UserAnalytics)

    @Query("UPDATE user_analytics SET totalMusicSeconds = totalMusicSeconds + :seconds WHERE id = 1")
    suspend fun addMusicTime(seconds: Long)

    @Query("UPDATE user_analytics SET totalVideoSeconds = totalVideoSeconds + :seconds WHERE id = 1")
    suspend fun addVideoTime(seconds: Long)

    @Query("UPDATE user_analytics SET totalPhotosViewed = totalPhotosViewed + 1 WHERE id = 1")
    suspend fun incrementPhotoView()

    @Query("UPDATE user_analytics SET totalAiSearches = totalAiSearches + 1 WHERE id = 1")
    suspend fun incrementAiSearch()
}

@Database(
    entities = [
        AudioTrack::class,
        PhotoItem::class,
        VideoItem::class,
        Playlist::class,
        VaultItem::class,
        SyncLog::class,
        UserAnalytics::class
    ],
    version = 1,
    exportSchema = false
)
abstract class OmniDatabase : RoomDatabase() {
    abstract fun audioDao(): AudioDao
    abstract fun photoDao(): PhotoDao
    abstract fun videoDao(): VideoDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun syncLogDao(): SyncLogDao
    abstract fun analyticsDao(): AnalyticsDao

    companion object {
        @Volatile
        private var INSTANCE: OmniDatabase? = null

        fun getDatabase(context: Context): OmniDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OmniDatabase::class.java,
                    "omniplay_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
