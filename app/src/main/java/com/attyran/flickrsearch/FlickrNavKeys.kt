package com.attyran.flickrsearch

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** Nav3 typed key for the Search (home) screen. */
@Serializable
object SearchKey : NavKey

/** Nav3 typed key for the Details screen. Carries the photo URL as a typed field — no URL encoding needed. */
@Serializable
data class DetailsKey(val photoURL: String) : NavKey