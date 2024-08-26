package com.attyran.flickrsearch

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.attyran.flickrsearch.network.BackendService
import com.attyran.flickrsearch.network.OAuthService

@Composable
fun FlickrApp(
    onPhotoClicked: (String) -> Unit,
    viewModel: FlickrViewModel = hiltViewModel(),
    oAuthViewModel: OAuthViewModel
) {
    val searchQuery = rememberSaveable { mutableStateOf("") }
    val photoState = viewModel.photoState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val imagesState = rememberSaveable { mutableStateOf(emptyList<String>()) }
    val authState = oAuthViewModel.authState.collectAsState()
    val hasToken = remember { mutableStateOf(false) }
    val errorMessage = rememberSaveable { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    LaunchedEffect(authState.value) {
        when(authState.value) {
            is OAuthViewModel.AuthState.Success -> {
                val url = (authState.value as OAuthViewModel.AuthState.Success).url
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                try {
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
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
                imagesState.value = emptyList()
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

        when (photoState.value) {
            is FlickrViewModel.UIState.Success -> {
                imagesState.value = emptyList()
                imagesState.value =
                    (photoState.value as FlickrViewModel.UIState.Success).photos.map { photo ->
                        String.format(
                            "https://farm%s.staticflickr.com/%s/%s_%s.jpg",
                            photo.farm, photo.server, photo.id, photo.secret
                        )
                    }
                PhotoGrid(imagesState.value, onPhotoClicked)
            }
            is FlickrViewModel.UIState.Error -> {
                Text(text = (photoState.value as FlickrViewModel.UIState.Error).message)
            }
        }

        if (errorMessage.value != null) {
            Text(text = errorMessage.value!!)
        }
    }
}

@Composable
fun PhotoGrid(images: List<String>, onPhotoClicked: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(images.chunked(2)) { imageRow ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (imageUrl in imageRow) {
                    ImageListItem(imageUrl = imageUrl, onPhotoClicked)
                }
            }
        }
    }
}

@Composable
fun ImageListItem(imageUrl: String, onPhotoClicked: (String) -> Unit) {
    val context = LocalContext.current
    val size = with(LocalDensity.current) { 180.dp.roundToPx() }
    val request = ImageRequest.Builder(context)
        .data(imageUrl)
        .size(size)
        .build()
    val painter = rememberAsyncImagePainter(request)

    Image(
        painter = painter,
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
    val viewModel = FlickrViewModel(BackendService.create())
    FlickrApp(
        onPhotoClicked = {},
        viewModel = viewModel,
        oAuthViewModel = OAuthViewModel(OAuthService.create(), application = Application())
    )
}