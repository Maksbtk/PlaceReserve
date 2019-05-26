package com.example.placereserve


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