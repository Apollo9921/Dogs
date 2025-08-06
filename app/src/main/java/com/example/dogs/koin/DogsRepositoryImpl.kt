package com.example.dogs.koin

import com.example.dogs.networking.model.Dogs
import com.example.dogs.networking.requests.Requests
import retrofit2.Response

class DogsRepositoryImpl(
    private val requests: Requests
): DogsRepository {
    override suspend fun getDogsImages(pageNumber: Int): Response<List<Dogs>> {
        return requests.getDogsImages(pageNumber)
    }
}