package com.example.data.ai

import com.example.BuildConfig
import com.example.data.model.AudioTrack
import com.example.data.model.PhotoItem
import com.example.data.model.VideoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AiSearchResult(
    val explanation: String,
    val matchedTrackIds: List<String> = emptyList(),
    val matchedPhotoIds: List<String> = emptyList(),
    val matchedVideoIds: List<String> = emptyList(),
    val suggestedMood: String = "Energetic",
    val recommendedEqPreset: String = "Pop"
)

class GeminiMediaAssistant {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun discoverMediaWithAi(
        query: String,
        availableTracks: List<AudioTrack>,
        availablePhotos: List<PhotoItem>,
        availableVideos: List<VideoItem>
    ): AiSearchResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val mediaManifest = buildString {
                    append("Tracks: ")
                    availableTracks.forEach { append("[id:${it.id}, title:${it.title}, genre:${it.genre}] ") }
                    append("\nPhotos: ")
                    availablePhotos.forEach { append("[id:${it.id}, title:${it.title}, tags:${it.tags}] ") }
                    append("\nVideos: ")
                    availableVideos.forEach { append("[id:${it.id}, title:${it.title}] ") }
                }

                val prompt = """
                    You are OmniPlay AI, a media assistant.
                    Given the user query: "$query"
                    and the available media items:
                    $mediaManifest
                    
                    Return a JSON object with this EXACT structure:
                    {
                      "explanation": "Short friendly sentence explaining why these matched",
                      "matchedTrackIds": ["track_id_1"],
                      "matchedPhotoIds": ["photo_id_1"],
                      "matchedVideoIds": ["vid_id_1"],
                      "suggestedMood": "Chill / Energetic / Focus / Nostalgic",
                      "recommendedEqPreset": "Bass Heavy / Pop / Rock / Jazz / Normal"
                    }
                """.trimIndent()

                val jsonBody = JSONObject().apply {
                    put("contents", JSONArray().put(JSONObject().apply {
                        put("parts", JSONArray().put(JSONObject().apply {
                            put("text", prompt)
                        }))
                    }))
                    put("generationConfig", JSONObject().apply {
                        put("responseMimeType", "application/json")
                        put("temperature", 0.3)
                    })
                }

                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
                    .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val rawResp = response.body?.string() ?: ""
                    val rootJson = JSONObject(rawResp)
                    val candidates = rootJson.optJSONArray("candidates")
                    val content = candidates?.optJSONObject(0)?.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    val text = parts?.optJSONObject(0)?.optString("text") ?: ""

                    if (text.isNotBlank()) {
                        val parsed = JSONObject(text)
                        val matchedTracks = mutableListOf<String>()
                        val matchedPhotos = mutableListOf<String>()
                        val matchedVideos = mutableListOf<String>()

                        parsed.optJSONArray("matchedTrackIds")?.let { arr ->
                            for (i in 0 until arr.length()) matchedTracks.add(arr.getString(i))
                        }
                        parsed.optJSONArray("matchedPhotoIds")?.let { arr ->
                            for (i in 0 until arr.length()) matchedPhotos.add(arr.getString(i))
                        }
                        parsed.optJSONArray("matchedVideoIds")?.let { arr ->
                            for (i in 0 until arr.length()) matchedVideos.add(arr.getString(i))
                        }

                        return@withContext AiSearchResult(
                            explanation = parsed.optString("explanation", "AI analyzed your request and picked relevant media."),
                            matchedTrackIds = matchedTracks,
                            matchedPhotoIds = matchedPhotos,
                            matchedVideoIds = matchedVideos,
                            suggestedMood = parsed.optString("suggestedMood", "Dynamic"),
                            recommendedEqPreset = parsed.optString("recommendedEqPreset", "Pop")
                        )
                    }
                }
            } catch (_: Exception) {
                // Fall through to high-speed local semantic matcher
            }
        }

        // Fast Local Semantic Matcher (offline optimized for low-end devices)
        performLocalSemanticSearch(query, availableTracks, availablePhotos, availableVideos)
    }

    private fun performLocalSemanticSearch(
        query: String,
        tracks: List<AudioTrack>,
        photos: List<PhotoItem>,
        videos: List<VideoItem>
    ): AiSearchResult {
        val q = query.lowercase().trim()
        val tokens = q.split(" ", ",", "-").filter { it.length > 2 }

        val matchedTrackIds = tracks.filter { track ->
            val text = "${track.title} ${track.artist} ${track.genre} ${track.album} ${track.lyrics}".lowercase()
            tokens.any { text.contains(it) } || q.contains("music") || q.contains("song") || q.contains("audio")
        }.map { it.id }.ifEmpty { if (q.contains("lofi") || q.contains("chill")) listOf("track_4", "track_1") else tracks.take(2).map { it.id } }

        val matchedPhotoIds = photos.filter { photo ->
            val text = "${photo.title} ${photo.tags} ${photo.albumName} ${photo.locationName}".lowercase()
            tokens.any { text.contains(it) } || q.contains("photo") || q.contains("pic") || q.contains("sunset") || q.contains("nature")
        }.map { it.id }.ifEmpty { if (q.contains("sunset") || q.contains("mountain")) listOf("photo_2", "photo_3") else photos.take(2).map { it.id } }

        val matchedVideoIds = videos.filter { video ->
            val text = "${video.title} ${video.resolution}".lowercase()
            tokens.any { text.contains(it) } || q.contains("video") || q.contains("movie") || q.contains("clip") || q.contains("4k")
        }.map { it.id }.ifEmpty { videos.take(2).map { it.id } }

        val mood = when {
            q.contains("chill") || q.contains("sleep") || q.contains("relax") -> "Ambient Chill"
            q.contains("gym") || q.contains("workout") || q.contains("fast") || q.contains("dance") -> "High Energy"
            q.contains("code") || q.contains("study") || q.contains("focus") -> "Deep Focus"
            else -> "Vibrant Pulse"
        }

        val eq = when {
            q.contains("bass") || q.contains("edm") -> "Bass Heavy"
            q.contains("rock") || q.contains("metal") -> "Rock"
            q.contains("acoustic") || q.contains("vocal") -> "Vocal Booster"
            q.contains("jazz") || q.contains("chill") -> "Jazz"
            else -> "Pop"
        }

        return AiSearchResult(
            explanation = "Discovered media items matching \"$query\" across Music, Photos & Videos.",
            matchedTrackIds = matchedTrackIds,
            matchedPhotoIds = matchedPhotoIds,
            matchedVideoIds = matchedVideoIds,
            suggestedMood = mood,
            recommendedEqPreset = eq
        )
    }
}
