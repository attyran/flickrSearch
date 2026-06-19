package com.attyran.flickrsearch.network

/**
 * Data class representing the intermediate OAuth request token response from Flickr.
 */
data class OAuthTokenResponse(
    val oauthCallbackConfirmed: Boolean,
    val oauthToken: String,
    val oauthTokenSecret: String
)

/**
 * Data class representing the final authorized OAuth access token response from Flickr,
 * containing the user credentials and authentication details.
 */
data class AccessTokenResponse(
    val fullname: String,
    val oauthToken: String,
    val oauthTokenSecret: String,
    val userNsid: String,
    val username: String
)