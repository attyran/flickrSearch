package com.attyran.flickrsearch

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@Composable
fun DetailsScreen(
    photoURL: String,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val listState = viewModel.listFlow.collectAsState()
    val lazyListState = rememberLazyListState()

    Column {
        Text(text = "Details Screen")
        LazyColumn {
            items(listState.value) {

                println(it)
                Text(text = it.toString())
            }
        }
        LaunchedEffect(lazyListState) {
            snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo }
                .collect { visibleItems ->
//                    val firstVisibleItemIndex = visibleItems.firstOrNull()?.index ?: return@collect
                    val lastVisibleItemIndex = visibleItems.lastOrNull()?.index ?: return@collect
//                    println("First visible item: $firstVisibleItemIndex")
                    println("Last visible item: $lastVisibleItemIndex")
                    viewModel.updateLastVisible(lastVisibleItemIndex)
//                    viewModel.lastVisible =    lastVisibleItemIndex
                }
        }
        viewModel.getFavorites()
    }
}

@Composable
fun Item() {

}