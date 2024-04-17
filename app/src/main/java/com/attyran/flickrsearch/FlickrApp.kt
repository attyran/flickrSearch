package com.attyran.flickrsearch

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.attyran.flickrsearch.network.BackendService

@Composable
fun FlickApp() {
    val searchQuery = rememberSaveable { mutableStateOf("") }
    val viewModel = remember { FlickrViewModel(BackendService()) }
    val photoState = viewModel.photoState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column {
        TextField(
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
        // TODO is this efficient?
        when (photoState.value) {
            is FlickrViewModel.UIState.Success -> {
                PhotoGrid(images = (photoState.value as FlickrViewModel.UIState.Success).photos.map { photo ->
                    String.format("https://farm%s.staticflickr.com/%s/%s_%s.jpg",
                        photo.farm, photo.server, photo.id, photo.secret)
                })
            }
            is FlickrViewModel.UIState.Error -> {
                Text(text = (photoState.value as FlickrViewModel.UIState.Error).message)
            }
        }
    }
}

@Composable
fun PhotoGrid(images: List<String>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(images.chunked(2)) { imageRow ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (imageUrl in imageRow) {
                    ImageListItem(imageUrl = imageUrl)
                }
            }
        }
    }
}

@Composable
fun ImageListItem(imageUrl: String) {
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
    )
}