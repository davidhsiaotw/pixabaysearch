package com.example.pixabaysearch

import androidx.paging.PagingSource
import androidx.paging.PagingState

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
        val page = params.key ?: 1
        return try {
            val response = pixabayApiService.getPhotos(
                query["key"] as String, query["q"] as String,
                query["lang"] as String, page
            ).execute()

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

                LoadResult.Page(
                    data = photos,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (photos.isNotEmpty()) page + 1 else null
                )
            } else {
                LoadResult.Error(Exception("Network request failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }


}
