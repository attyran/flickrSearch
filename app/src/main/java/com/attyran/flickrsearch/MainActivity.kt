package com.attyran.flickrsearch

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val authViewModel: OAuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlickrNavGraph(authViewModel = authViewModel)
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
