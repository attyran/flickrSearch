package com.attyran.flickrsearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.attyran.flickrsearch.network.BackendService
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: FlickrViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = FlickrViewModel(BackendService())
        setContent {
            PhotoSearchScreen(viewModel = viewModel) {
                lifecycleScope.launch {
                    viewModel.search(it)
                }
            }
        }
    }
}
