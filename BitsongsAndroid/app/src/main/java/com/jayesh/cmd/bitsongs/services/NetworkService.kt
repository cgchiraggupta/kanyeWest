package com.jayesh.cmd.bitsongs.services

import com.jayesh.cmd.bitsongs.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface BitsongsApi {
    @GET("api/mobile/health")
    suspend fun healthCheck(): Boolean
    
    @GET("api/mobile/search")
    suspend fun search(@Query("q") query: String): List<Song>
    
    @GET("api/mobile/chart")
    suspend fun getChart(): List<Song>
    
    @GET("api/mobile/play")
    suspend fun getStream(
        @Query("id") id: String,
        @Query("artist") artist: String,
        @Query("title") title: String,
        @Query("previous_song_id") previousSongId: String? = null
    ): StreamInfo
    
    @GET("api/mobile/lyrics")
    suspend fun getLyrics(
        @Query("artist") artist: String,
        @Query("title") title: String
    ): LyricsResponse
    
    @GET("api/mobile/recommend")
    suspend fun getRecommendations(@Query("song_id") songId: String): Recommendations
    
    @GET("api/mobile/up_next")
    suspend fun getUpNext(
        @Query("song_id") songId: String,
        @Query("limit") limit: Int = 10
    ): List<Song>
    
    @POST("api/mobile/cache_song")
    suspend fun cacheSong(@Query("id") id: String): Boolean
}

class NetworkService(baseUrl: String = "http://127.0.0.1:499") {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    private val api = retrofit.create(BitsongsApi::class.java)
    
    suspend fun healthCheck(): Boolean = withContext(Dispatchers.IO) {
        try {
            api.healthCheck()
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun search(query: String): List<Song> = withContext(Dispatchers.IO) {
        try {
            api.search(query)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getChart(): List<Song> = withContext(Dispatchers.IO) {
        try {
            api.getChart()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getStream(song: Song, previousSongId: String? = null): StreamInfo = withContext(Dispatchers.IO) {
        try {
            api.getStream(song.id, song.artist, song.title, previousSongId)
        } catch (e: Exception) {
            throw Exception("Failed to get stream: ${e.message}")
        }
    }
    
    suspend fun getLyrics(artist: String, title: String): LyricsResponse = withContext(Dispatchers.IO) {
        try {
            api.getLyrics(artist, title)
        } catch (e: Exception) {
            LyricsResponse(null, false)
        }
    }
    
    suspend fun getRecommendations(songId: String): Recommendations = withContext(Dispatchers.IO) {
        try {
            api.getRecommendations(songId)
        } catch (e: Exception) {
            Recommendations(emptyList(), emptyList())
        }
    }
    
    suspend fun getUpNext(songId: String, limit: Int = 10): List<Song> = withContext(Dispatchers.IO) {
        try {
            api.getUpNext(songId, limit)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun cacheSong(song: Song): Boolean = withContext(Dispatchers.IO) {
        try {
            api.cacheSong(song.id)
        } catch (e: Exception) {
            false
        }
    }
}