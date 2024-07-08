package com.attyran.flickrsearch

import android.app.Application
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.attyran.flickrsearch.network.AccessTokenResponse
import com.attyran.flickrsearch.network.OAuthService
import com.attyran.flickrsearch.network.OAuthTokenResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

@HiltViewModel
class OAuthViewModel @Inject constructor(
    private val oAuthService: OAuthService,
    private val application: Application
): ViewModel() {
    private var _authState = MutableStateFlow<AuthState>(AuthState.Error(""))
    val authState: StateFlow<AuthState> = _authState

    init {
        viewModelScope.launch {
            if (hasAccessToken()) {
                _authState.value = AuthState.TokenSuccess()
            }
        }
    }

    fun requestToken() {
        viewModelScope.launch {
            try {
                val oauthNonce = UUID.randomUUID().toString()
                val oauthTimestamp = (System.currentTimeMillis() / 1000).toString()
                val oauthConsumerKey = BuildConfig.FLICKR_API_KEY
                val oauthCallback = "com.attyran.flickrsearch://oauth_callback"
                val oauthSignatureMethod = "HMAC-SHA1"
                val oauthVersion = "1.0"
                val consumerSecret = BuildConfig.FLICKR_API_SECRET_KEY
                val url = "https://www.flickr.com/services/oauth/request_token"
                val params = mapOf(
                    "oauth_nonce" to oauthNonce,
                    "oauth_timestamp" to oauthTimestamp,
                    "oauth_consumer_key" to oauthConsumerKey,
                    "oauth_signature_method" to oauthSignatureMethod,
                    "oauth_version" to oauthVersion,
                    "oauth_callback" to oauthCallback
                )
                val oauthSignature = generateSignature(
                    "GET",
                    url,
                    params,
                    consumerSecret
                )
                val getRequestTokenResult = oAuthService.getRequestToken(
                    oauthNonce = oauthNonce,
                    oauthTimestamp = oauthTimestamp,
                    oauthConsumerKey = oauthConsumerKey,
                    oauthSignatureMethod = oauthSignatureMethod,
                    oauthVersion = oauthVersion,
                    oauthCallback = oauthCallback,
                    oauthSignature = oauthSignature
                )
                if (getRequestTokenResult.isSuccessful) {
                    val response = parseOAuthTokenResponse(getRequestTokenResult.body()!!)
                    val masterKey = MasterKey.Builder(application)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build()
                    val sharedPreferences = EncryptedSharedPreferences.create(
                        application,
                        "secret_shared_prefs",
                        masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    )

                    with(sharedPreferences.edit()) {
                        putString("oauth_token_secret", response.oauthTokenSecret)
                        apply()
                    }
                    _authState.value = AuthState.Success("https://www.flickr.com/services/oauth/authorize?oauth_token=${response.oauthToken}&perms=write")
                } else {
                    _authState.value = AuthState.Error("Failed to get request token")
                }

            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun requestAccessToken(oauthVerifier: String, oauthToken: String) {
        viewModelScope.launch {
            try {
                val oauthNonce = UUID.randomUUID().toString()
                val oauthTimestamp = (System.currentTimeMillis() / 1000).toString()
                val oauthConsumerKey = BuildConfig.FLICKR_API_KEY
                val oauthSignatureMethod = "HMAC-SHA1"
                val oauthVersion = "1.0"
                val consumerSecret = BuildConfig.FLICKR_API_SECRET_KEY
                val url = "https://www.flickr.com/services/oauth/access_token"
                val params = mapOf(
                    "oauth_nonce" to oauthNonce,
                    "oauth_timestamp" to oauthTimestamp,
                    "oauth_verifier" to oauthVerifier,
                    "oauth_consumer_key" to oauthConsumerKey,
                    "oauth_signature_method" to oauthSignatureMethod,
                    "oauth_version" to oauthVersion,
                    "oauth_token" to oauthToken
                )
                val masterKey = MasterKey.Builder(application)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

                val sharedPreferences = EncryptedSharedPreferences.create(
                    application,
                    "secret_shared_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
                val oauthTokenSecret = sharedPreferences.getString("oauth_token_secret", null)
                val oauthSignature = generateSignature(
                    "GET",
                    url,
                    params,
                    consumerSecret,
                    oauthTokenSecret
                )
                val getAccessTokenResult = oAuthService.getAccessToken(
                    oauthNonce = oauthNonce,
                    oauthTimestamp = oauthTimestamp,
                    oauthVerifier = oauthVerifier,
                    oauthConsumerKey = oauthConsumerKey,
                    oauthSignatureMethod = oauthSignatureMethod,
                    oauthVersion = oauthVersion,
                    oauthToken = oauthToken,
                    oauthSignature = oauthSignature
                )
                if (getAccessTokenResult.isSuccessful) {
                    val response = parseAccessTokenResponse(getAccessTokenResult.body()!!)
                    storeTokens(response.oauthToken, response.oauthTokenSecret)
                    _authState.value = AuthState.TokenSuccess()
                } else {
                    // Handle error
                }
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }

    private fun storeTokens(oauthToken: String, oauthTokenSecret: String) {
        val masterKey = MasterKey.Builder(application)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val sharedPreferences = EncryptedSharedPreferences.create(
            application,
            "secret_shared_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        with(sharedPreferences.edit()) {
            putString("oauth_token", oauthToken)
            putString("oauth_token_secret", oauthTokenSecret)
            apply()
        }
    }

    private fun parseOAuthTokenResponse(response: String): OAuthTokenResponse {
        val params = response.split("&").associate {
            val (key, value) = it.split("=")
            key to value
        }
        return OAuthTokenResponse(
            oauthCallbackConfirmed = params["oauth_callback_confirmed"]?.toBoolean() ?: false,
            oauthToken = params["oauth_token"] ?: "",
            oauthTokenSecret = params["oauth_token_secret"] ?: ""
        )
    }

    private fun parseAccessTokenResponse(response: String): AccessTokenResponse {
        val params = response.split("&").associate {
            val (key, value) = it.split("=")
            key to value
        }
        return AccessTokenResponse(
            fullname = params["fullname"] ?: "",
            oauthToken = params["oauth_token"] ?: "",
            oauthTokenSecret = params["oauth_token_secret"] ?: "",
            userNsid = params["user_nsid"] ?: "",
            username = params["username"] ?: ""
        )
    }

    private fun hasAccessToken(): Boolean {
        val masterKey = MasterKey.Builder(application)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            application,
            "secret_shared_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val oauthToken = sharedPreferences.getString("oauth_token", null)
        val oauthTokenSecret = sharedPreferences.getString("oauth_token_secret", null)

        return oauthToken != null && oauthTokenSecret != null
    }

    private fun generateSignature(
        method: String,
        url: String,
        params: Map<String, String>,
        consumerSecret: String,
        oauthTokenSecret: String? = null
    ): String {
        val baseString = generateSignatureBaseString(method, url, params)
        val signingKey = "${URLEncoder.encode(consumerSecret, "UTF-8")}&${oauthTokenSecret?.let { URLEncoder.encode(it, "UTF-8") } ?: ""}"

        val mac = Mac.getInstance("HmacSHA1")
        val secretKey = SecretKeySpec(signingKey.toByteArray(), "HmacSHA1")
        mac.init(secretKey)
        val rawHMAC = mac.doFinal(baseString.toByteArray())

        return Base64.encodeToString(rawHMAC, Base64.NO_WRAP)
    }

    private fun generateSignatureBaseString(
        method: String,
        url: String,
        params: Map<String, String>
    ): String {
        val encodedParams = params.toSortedMap().map { (key, value) ->
            "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value, "UTF-8")}"
        }.joinToString("&")

        val encodedUrl = URLEncoder.encode(url, "UTF-8")
        val encodedParamsString = URLEncoder.encode(encodedParams, "UTF-8")

        return "$method&$encodedUrl&$encodedParamsString"
    }

    sealed class AuthState {
        data class Success(val url: String): AuthState()
        data class Error(val message: String): AuthState()
        data class TokenSuccess(val timestamp: Long = System.currentTimeMillis()): AuthState()
    }
}