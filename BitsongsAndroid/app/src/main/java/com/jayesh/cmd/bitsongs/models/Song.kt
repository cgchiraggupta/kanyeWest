package com.jayesh.cmd.bitsongs.models

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val artistId: Int,
    val album: String,
    val cover: String,
    val coverXL: String,
    val duration: Int,
    val genre: String,
    val cached: Boolean = false,
    val reason: String? = null
)

data class StreamInfo(
    val url: String,
    val cached: Boolean
)

data class LyricsResponse(
    val lyrics: String?,
    val synced: Boolean
)

data class Recommendations(
    val behaviorBased: List<Song>,
    val contentBased: List<Song>
)