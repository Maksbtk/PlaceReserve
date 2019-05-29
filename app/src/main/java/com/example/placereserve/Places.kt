package com.example.placereserve

import java.util.*


data class Places(
    var name: String,
    var address: String,
    var image: Int
)

data class PlacesFavorite(
    val nameF: String,
    val addressF: String,
    val imageF: Int
)

data class PlacesHistory(
    val nameH: String,
    val addressH: String,
    val dateH: String,
    val timeH: String,
    val imageH: Int
)