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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import androidx.compose.ui.text.input.ImeAction

@Composable
fun PhotoSearchScreen(viewModel: FlickrViewModel, onSearchClicked: (String) -> Unit) {
    val searchQuery = rememberSaveable { mutableStateOf("") }
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
                onSearchClicked(searchQuery.value)
                keyboardController?.hide()
            })
        )
        Button(
            onClick = {
                onSearchClicked(searchQuery.value)
                keyboardController?.hide()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Search")
        }
        PhotoGrid(images = photoState.value.photos.photo.map { photo ->
            String.format("https://farm%s.staticflickr.com/%s/%s_%s.jpg",
                photo.farm, photo.server, photo.id, photo.secret)
        })
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
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current).data(data = imageUrl).apply(block = fun ImageRequest.Builder.() {
            scale(Scale.FILL)
        }).build()
    )

    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .padding(4.dp)
            .size(180.dp)
            .clip(MaterialTheme.shapes.medium)
    )
}