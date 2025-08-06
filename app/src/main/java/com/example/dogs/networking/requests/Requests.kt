package com.example.dogs.networking.requests

import com.example.dogs.networking.model.Dogs
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Requests {

    @GET("images/search")
    suspend fun getDogsImages(@Query("limit") limit: Int): Response<List<Dogs>>

}