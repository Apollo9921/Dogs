package com.example.dogs.networking.interfaces

interface BreedSelected {

    suspend fun onBreedSelected(breedType: String)

}