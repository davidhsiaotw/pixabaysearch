package com.example.pixabaysearch

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PhotoViewModel() : ViewModel() {
    private val _photos = MutableLiveData<MutableList<Photo>>()
    val photos: LiveData<MutableList<Photo>> = _photos

    private var _test = MutableLiveData<PagingData<Photo>>()
    val test: LiveData<PagingData<Photo>> = _test

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> = _status

    init {
        viewModelScope.launch {
            searchPhotos(
                mapOf(
                    "key" to BuildConfig.API_KEY, "q" to "", "lang" to "en", "page" to 1
                )
            )
        }
    }

    suspend fun searchPhotos(query: Map<String, Any?>) {
        try {
            _status.value = "Loading"
            val currentPhotos = withContext(Dispatchers.IO) {
                val call = PixabayApi.retrofitService.getPhotos(
                    query["key"] as String, query["q"] as String,
                    query["lang"] as String, query["page"] as Int
                )

                val response = call.execute()
                if (response.isSuccessful) {
                    val result = response.body()?.get("hits") as List<*>
                    val photos = mutableListOf<Photo>()
                    for (item in result) {
                        val json = item as Map<*, *>
                        val p = Photo(
                            json["id"] as Double, json["webformatURL"] as String,
                            json["tags"] as String, json["userImageURL"] as String,
                            json["webformatWidth"] as Double, json["webformatHeight"] as Double,
                        )
                        photos.add(p)
                    }
                    photos
                } else {
                    _status.value = "Error"
                    throw Exception("Network request failed with code: ${response.code()}")
                }
            }

            _photos.value = currentPhotos
            _status.value = "Success"
            Log.d("SearchPhotos", "Success: ${_photos.value?.size}")
        } catch (e: Exception) {
            Log.d("SearchPhotos", e.toString())
        }

    }

    @Deprecated("not completed", ReplaceWith("searchPhotos()"))
    fun testPhotos(query: Map<String, Any>) {
        _test = Pager(
            config = PagingConfig(
                pageSize = 100, enablePlaceholders = false, initialLoadSize = 100
            ), pagingSourceFactory = { PhotosPagingSource(query, PixabayApi.retrofitService) }
        ).liveData.cachedIn(viewModelScope) as MutableLiveData<PagingData<Photo>>
    }
}