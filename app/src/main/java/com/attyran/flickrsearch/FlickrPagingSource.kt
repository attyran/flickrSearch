package com.attyran.flickrsearch

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.attyran.flickrsearch.network.BackendService
import com.attyran.flickrsearch.network.PhotoItem

private const val STARTING_PAGE_INDEX = 1

class FlickrPagingSource(
    private val backendService: BackendService,
    private val query: String
) : PagingSource<Int, PhotoItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoItem> {
        return try {
            val page = params.key ?: STARTING_PAGE_INDEX
            Log.d("FlickrPagingSource", "Loading page $page for query: $query")
            
            val response = backendService.search(
                tag = query,
                page = page,
                perPage = params.loadSize
            )

            Log.d("FlickrPagingSource", "Response: stat=${response.stat}, " +
                    "total=${response.photos.total}, current page=${response.photos.page}, " +
                    "photos size=${response.photos.photo.size}")

            if (response.stat != "ok") {
                return LoadResult.Error(Exception("Failed to load photos"))
            }

            LoadResult.Page(
                data = response.photos.photo,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (page < response.photos.pages) page + 1 else null
            )
        } catch (e: Exception) {
            Log.e("FlickrPagingSource", "Error loading page", e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PhotoItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
} 