package com.attyran.flickrsearch

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.attyran.flickrsearch.network.BackendService
import com.attyran.flickrsearch.network.OAuthService
import androidx.core.net.toUri

@Composable
fun FlickrApp(
    onPhotoClicked: (String) -> Unit,
    viewModel: FlickrViewModel = hiltViewModel(),
    oAuthViewModel: OAuthViewModel
) {
    val searchQuery = rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val authState = oAuthViewModel.authState.collectAsState()
    val hasToken = remember { mutableStateOf(false) }
    val errorMessage = rememberSaveable { mutableStateOf<String?>(null) }
    val photos = viewModel.searchResults.collectAsLazyPagingItems()

    val context = LocalContext.current
    LaunchedEffect(authState.value) {
        when(authState.value) {
            is OAuthViewModel.AuthState.Success -> {
                val url = (authState.value as OAuthViewModel.AuthState.Success).url
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                try {
                    context.startActivity(intent)
                } catch (_: ActivityNotFoundException) {
                    // Handle exception
                }
            }
            is OAuthViewModel.AuthState.TokenSuccess -> {
                hasToken.value = true
            }
            is OAuthViewModel.AuthState.Error -> {
                errorMessage.value = (authState.value as OAuthViewModel.AuthState.Error).message
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            label = { Text(text = "Search") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                viewModel.searchTag(searchQuery.value)
                keyboardController?.hide()
            })
        )
        Button(
            onClick = {
                viewModel.searchTag(searchQuery.value)
                keyboardController?.hide()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Search")
        }
        if (!hasToken.value) {
            Button(
                onClick = {
                    oAuthViewModel.requestToken()
                },
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(text = "Login")
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                count = photos.itemCount,
                key = { index -> 
                    val photo = photos[index]
                    // Combine index and photo ID to ensure uniqueness
                    "${photo?.id}_$index"
                }
            ) { index ->
                val photo = photos[index]
                photo?.let {
                    val imageUrl = String.format(
                        "https://farm%s.staticflickr.com/%s/%s_%s.jpg",
                        it.farm, it.server, it.id, it.secret
                    )
                    ImageListItem(imageUrl = imageUrl, onPhotoClicked)
                }
            }
        }

        if (errorMessage.value != null) {
            Text(text = errorMessage.value!!)
        }
    }
}

@Composable
fun ImageListItem(imageUrl: String, onPhotoClicked: (String) -> Unit) {
    val context = LocalContext.current
    val size = with(LocalDensity.current) { 180.dp.roundToPx() }
    val model = ImageRequest.Builder(context)
        .data(imageUrl)
        .size(size)
        .build()

    AsyncImage(
        model = model,
        contentDescription = null,
        modifier = Modifier
            .padding(4.dp)
            .size(180.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable { onPhotoClicked(imageUrl) }
    )
}

@Preview
@Composable
private fun FlickrAppPreview() {
    val viewModel = FlickrViewModel(FlickrRepository(BackendService.create()))
    FlickrApp(
        onPhotoClicked = {},
        viewModel = viewModel,
        oAuthViewModel = OAuthViewModel(OAuthService.create(), application = Application())
    )
}