package com.attyran.flickrsearch

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import com.attyran.flickrsearch.network.PhotoItem
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.flow.flowOf

@Composable
fun FlickrApp(
    onPhotoClicked: (String) -> Unit,
    viewModel: FlickrViewModel = hiltViewModel(),
    oAuthViewModel: OAuthViewModel
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val authState = oAuthViewModel.authState.collectAsState()
    val hasToken = remember { mutableStateOf(false) }
    val oAuthErrorMessage = rememberSaveable { mutableStateOf<String?>(null) }
    val uiState by viewModel.uiState.collectAsState()

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
                oAuthErrorMessage.value = (authState.value as OAuthViewModel.AuthState.Error).message
            }
        }
    }

    FlickrContent(
        uiState = uiState,
        hasToken = hasToken.value,
        oAuthErrorMessage = oAuthErrorMessage.value,
        onSearch = { query ->
            viewModel.processIntent(FlickrContract.Intent.Search(query))
            keyboardController?.hide()
        },
        onLoginClick = {
            oAuthViewModel.requestToken()
        },
        onPhotoClicked = onPhotoClicked
    )
}

@Composable
fun FlickrContent(
    uiState: FlickrContract.UiState,
    hasToken: Boolean,
    oAuthErrorMessage: String?,
    onSearch: (String) -> Unit,
    onLoginClick: () -> Unit,
    onPhotoClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQuery = rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
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
                onSearch(searchQuery.value)
                keyboardController?.hide()
            })
        )
        Button(
            onClick = {
                onSearch(searchQuery.value)
                keyboardController?.hide()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Search")
        }
        if (!hasToken) {
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(text = "Login")
            }
        }

        when (uiState) {
            FlickrContract.UiState.Idle -> Unit
            is FlickrContract.UiState.Success -> {
                val photos = uiState.photos.collectAsLazyPagingItems()
                when (val refreshState = photos.loadState.refresh) {
                    is LoadState.Loading -> {
                        ShimmerGrid(modifier = Modifier.fillMaxSize())
                    }
                    is LoadState.Error -> {
                        Text(
                            text = refreshState.error.message ?: "Failed to load photos",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    is LoadState.NotLoading -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                count = photos.itemCount,
                                key = { index ->
                                    val photo = photos[index]
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
                    }
                }
            }
            is FlickrContract.UiState.Error -> {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        if (oAuthErrorMessage != null) {
            Text(text = oAuthErrorMessage)
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

    SubcomposeAsyncImage(
        model = model,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .padding(4.dp)
            .size(180.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable { onPhotoClicked(imageUrl) },
        loading = {
            ShimmerBox(modifier = Modifier.fillMaxSize())
        }
    )
}

@Preview
@Composable
private fun FlickrAppPreview() {
    val fakePagingData = PagingData.from(
        listOf(
            PhotoItem(
                id = "1",
                owner = "owner1",
                secret = "secret1",
                server = "server1",
                farm = "1",
                title = "Title 1",
                isPublic = "1",
                isFriend = "0",
                isFamily = "0"
            ),
            PhotoItem(
                id = "2",
                owner = "owner2",
                secret = "secret2",
                server = "server2",
                farm = "2",
                title = "Title 2",
                isPublic = "1",
                isFriend = "0",
                isFamily = "0"
            )
        )
    )

    FlickrContent(
        uiState = FlickrContract.UiState.Success(flowOf(fakePagingData)),
        hasToken = true,
        oAuthErrorMessage = null,
        onSearch = {},
        onLoginClick = {},
        onPhotoClicked = {}
    )
}