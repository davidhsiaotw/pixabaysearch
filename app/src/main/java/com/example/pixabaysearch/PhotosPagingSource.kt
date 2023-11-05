package com.example.pixabaysearch

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PhotosPagingSource(
    private val query: Map<String, Any>,
    private val pixabayApiService: PixabayApiService
) : PagingSource<Int, Photo>() {
    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val startKey = params.key ?: 1
        val pageSize = params.loadSize
        val page = startKey / params.loadSize + 1

        try {
            val response = withContext(Dispatchers.Default) {
                pixabayApiService.getPhotos(
                    query["key"] as String, keyword = query["q"] as String,
                    query["lang"] as String?, page = page, perPage = pageSize
                )
            }

            if (response.isSuccessful) {
                val photos = response.body()?.photos ?: listOf()

                return LoadResult.Page(
                    data = photos,
                    prevKey = if (page == 1) null else startKey - 1,
                    nextKey = startKey + pageSize
                )
            } else {
                Log.e("PhotosPagingSource.load", "api response: ${response.code()}")
                return LoadResult.Error(Exception("Network request failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("PhotosPagingSource.load", e.message ?: "paging load error")
            return LoadResult.Error(e)
        }
    }


}
