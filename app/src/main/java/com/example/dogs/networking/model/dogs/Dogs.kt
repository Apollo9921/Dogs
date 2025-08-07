package com.example.dogs.networking.model.dogs

data class Dogs(
    val breeds: List<Breed>?,
    val height: Int,
    val id: String,
    val url: String,
    val width: Int
)