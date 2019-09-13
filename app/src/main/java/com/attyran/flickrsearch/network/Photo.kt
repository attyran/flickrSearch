package com.attyran.flickrsearch.network

data class Photo(val id: String,
                 val owner: String,
                 val secret: String,
                 val server: String,
                 val farm: String,
                 val title: String,
                 val ispublic: String,
                 val isfriend: String,
                 val isFamily: String)