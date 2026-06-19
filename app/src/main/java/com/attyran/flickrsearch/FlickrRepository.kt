package com.attyran.flickrsearch

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.attyran.flickrsearch.network.BackendService
import com.attyran.flickrsearch.network.PhotoItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository class providing a paginated data stream of Flickr photo search results
 * based on a specific query tag.
 */
@Singleton
class FlickrRepository @Inject constructor(
    private val backendService: BackendService
) {
    fun searchTag(tag: String): Flow<PagingData<PhotoItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                initialLoadSize = 10
            ),
            pagingSourceFactory = { FlickrPagingSource(backendService, tag) }
        ).flow
    }
}