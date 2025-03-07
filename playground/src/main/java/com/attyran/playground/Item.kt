package com.attyran.playground

import kotlinx.serialization.Serializable

@Serializable
data class Item(val id: Int, val name: String, val description: String)
