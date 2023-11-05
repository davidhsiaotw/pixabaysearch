package com.example.pixabaysearch

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://pixabay.com/"
private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
private val retrofit = Retrofit.Builder().addConverterFactory(
    MoshiConverterFactory.create(moshi)
).baseUrl(BASE_URL).build()

interface PixabayApiService {

    @GET("api/")
    suspend fun getPhotos(
        @Query("key") key: String, @Query("q") keyword: String = "",
        @Query("lang") language: String? = "en", @Query("page") page: Int? = 1,
        @Query("per_page") perPage: Int? = 200,
    ): Response<Pixabay>
}

object PixabayApi {
    val retrofitService: PixabayApiService by lazy {
        retrofit.create(PixabayApiService::class.java)
    }
}