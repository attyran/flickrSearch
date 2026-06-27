package com.attyran.flickrsearch

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.NavKey
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main entry point activity of the application, managing edge-to-edge layouts,
 * initializing the Navigation 3 back stack, and handling deep links for the OAuth callback.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val authViewModel: OAuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val backStack = mutableStateListOf<NavKey>(SearchKey)
        setContent {
            FlickrNavGraph(
                backStack = backStack,
                authViewModel = authViewModel
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val data: Uri? = intent.data
        if (data != null && data.toString().startsWith("com.attyran.flickrsearch://oauth_callback")) {
            val oauthVerifier = data.getQueryParameter("oauth_verifier")
            val oauthToken = data.getQueryParameter("oauth_token")!!
            if (oauthVerifier != null) {
                authViewModel.requestAccessToken(oauthVerifier, oauthToken)
            }
        }
    }
}
