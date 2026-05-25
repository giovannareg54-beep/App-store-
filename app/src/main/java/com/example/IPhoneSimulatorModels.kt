package com.example

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class AppItem(
    val id: String,
    val name: String,
    val iconEmoji: String,
    val iconBgColor: Color,
    val isAppStoreOffer: Boolean = false,
    val category: String = "Utilidades",
    val description: String = "",
    val rating: Double = 4.8,
    var isInstalled: Boolean = true,
    var downloadProgress: Float = 0f,
    var isDownloading: Boolean = false
)

data class NoteItem(
    val id: Int,
    val title: String,
    val content: String,
    val time: String
)

data class SongItem(
    val id: Int,
    val title: String,
    val artist: String,
    val album: String,
    val durationSeconds: Int,
    val lyrics: List<String>
)

data class PhotoItem(
    val id: Int,
    val emoji: String,
    val location: String,
    val time: String
)

data class MessageItem(
    val text: String,
    val isUser: Boolean,
    val timestamp: String
)

data class ContactItem(
    val name: String,
    val statusText: String,
    val emoji: String,
    val activeMessages: List<MessageItem>,
    val automatedReplies: List<String>
)

data class WallpaperItem(
    val id: Int,
    val name: String,
    val gradient: Brush,
    val isDark: Boolean
)

data class WeatherInfo(
    val city: String,
    val temp: Int,
    val condition: String,
    val emoji: String,
    val forecast: List<Pair<String, Int>>
)
