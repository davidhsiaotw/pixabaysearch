package com.example.pixabaysearch

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Photo(
    val id: Long,
    @Json(name = "webformatURL")
    val webformatUrl: String,
    val tags: String,
    @Json(name = "userImageURL")
    val userImageUrl: String,
    val webformatWidth: Long,
    val webformatHeight: Long
)
