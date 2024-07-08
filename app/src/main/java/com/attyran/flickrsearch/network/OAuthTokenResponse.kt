package com.attyran.flickrsearch.network

data class OAuthTokenResponse(
    val oauthCallbackConfirmed: Boolean,
    val oauthToken: String,
    val oauthTokenSecret: String
)

data class AccessTokenResponse(
    val fullname: String,
    val oauthToken: String,
    val oauthTokenSecret: String,
    val userNsid: String,
    val username: String
)