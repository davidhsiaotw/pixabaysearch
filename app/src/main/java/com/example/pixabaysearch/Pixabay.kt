package com.example.pixabaysearch

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Pixabay(
    val total: Long, val totalHits: Long,
    @Json(name = "hits") val photos: List<Photo>
)