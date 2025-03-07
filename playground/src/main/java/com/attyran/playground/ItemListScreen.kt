package com.attyran.playground

import android.R.string
import android.content.Context
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.InputStreamReader

@Composable
fun ItemListScreen() {
    val context = LocalContext.current
    loadItemsFromAssets(context)
    val items = remember { loadItemsFromAssets(context) }

    LazyColumn {
        items(items) { item ->
            Text(text = "${item.name}: ${item.description}")
        }
    }

}

fun loadItemsFromAssets(context: Context): List<Item> {
    val inputStream = context.assets.open("items.json")
    val reader = InputStreamReader(inputStream)
    val jsonString = reader.readText()
    return Json.decodeFromString<List<Item>>(jsonString)
}
