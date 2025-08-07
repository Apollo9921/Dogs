package com.example.dogs.koin

import com.example.dogs.networking.model.breeds.Breeds
import com.example.dogs.networking.model.dogs.Dogs
import retrofit2.Response

interface DogsRepository {
    suspend fun getDogsImages(pageNumber: Int): Response<List<Dogs>>
    suspend fun getSpecificDog(id: String): Response<Dogs>
    suspend fun getAllBreeds(): Response<List<Breeds>>
    suspend fun filterByBreed(breedName: String): Response<List<Breeds>>
}