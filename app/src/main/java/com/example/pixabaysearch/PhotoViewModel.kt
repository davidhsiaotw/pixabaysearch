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
    private val _photos = MutableLiveData<List<Photo>>()
    val photos: LiveData<List<Photo>> = _photos

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> = _status

//    init {
//        viewModelScope.launch {
//            searchPhotos(
//                mapOf("key" to BuildConfig.API_KEY, "q" to "")
//            )
//        }
//    }

    suspend fun searchPhotos(query: Map<String, Any?>) {
        try {
            _status.value = "Loading"
            val currentPhotos = withContext(Dispatchers.Default) {
                val response = PixabayApi.retrofitService.getPhotos(
                    key = query["key"] as String, keyword = query["q"] as String,
                    language = query["lang"] as String?, page = query["page"] as Int?
                )

                Log.d("SearchPhotos", "${response.body()?.photos?.size}")

                if (response.isSuccessful) {
                    response.body()?.photos?: listOf()
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

    fun testPhotos(query: Map<String, Any>): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(pageSize = 100, enablePlaceholders = false, initialLoadSize = 100),
            pagingSourceFactory = { PhotosPagingSource(query, PixabayApi.retrofitService) }
        ).flow.cachedIn(viewModelScope)
    }
}