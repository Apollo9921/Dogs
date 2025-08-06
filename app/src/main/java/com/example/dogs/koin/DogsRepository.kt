package com.example.dogs.koin

import com.example.dogs.networking.model.Dogs
import retrofit2.Response

interface DogsRepository {
    suspend fun getDogsImages(pageNumber: Int): Response<List<Dogs>>
}