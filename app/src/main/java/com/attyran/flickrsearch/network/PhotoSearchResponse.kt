package com.attyran.flickrsearch.network

data class PhotoSearchResponse(val photos: Result)

data class Result(val photo: List<Photo> = emptyList())