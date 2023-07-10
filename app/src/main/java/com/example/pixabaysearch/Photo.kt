package com.example.pixabaysearch

import androidx.room.PrimaryKey

data class Photo(
    @PrimaryKey val id: Double,
    val webformatUrl: String,
    val tags: String,
    val userImageUrl: String,
    val webformatWidth: Double,
    val webformatHeight: Double
)
