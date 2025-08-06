package com.example.dogs.networking.requests

import com.example.dogs.networking.model.Dogs
import com.example.dogs.networking.model.breeds.Breeds
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Requests {

    @GET("images/search")
    suspend fun getDogsImages(@Query("limit") limit: Int): Response<List<Dogs>>

    @GET("breeds")
    suspend fun getAllBreeds(): Response<List<Breeds>>

}