package com.attyran.flickrsearch

data class PhotoSearchResponse(val photos: Result)

data class Result(val photo: List<Photo>)