package com.example.dogs.koin

import com.example.dogs.networking.model.breeds.Breeds
import com.example.dogs.networking.model.dogs.Dogs
import com.example.dogs.networking.requests.Requests
import retrofit2.Response

class DogsRepositoryImpl(
    private val requests: Requests
): DogsRepository {
    override suspend fun getDogsImages(pageNumber: Int): Response<List<Dogs>> {
        return requests.getDogsImages(pageNumber)
    }

    override suspend fun getSpecificDog(id: String): Response<Dogs> {
        return requests.getSpecificDog(id)
    }

    override suspend fun getAllBreeds(): Response<List<Breeds>> {
        return requests.getAllBreeds()
    }

    override suspend fun filterByBreed(breedName: String): Response<List<Breeds>> {
        return requests.filterByBreed(breedName)
    }
}