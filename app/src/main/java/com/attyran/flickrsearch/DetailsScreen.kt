package com.attyran.flickrsearch

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest

@Composable
fun DetailsScreen(
    photoURL: String,
) {
    Column {
        Text(text = "Details Screen")

        val context = LocalContext.current
        val request = ImageRequest.Builder(context)
            .data(photoURL)
            .build()
        val painter = rememberAsyncImagePainter(request)

        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
        )
    }
}