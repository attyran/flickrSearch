package com.attyran.flickrsearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.attyran.flickrsearch.network.BackendService

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: FlickrViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = FlickrViewModel(BackendService())
        setContent {
            FlickrNavGraph()
        }
    }
}
