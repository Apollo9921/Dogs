package com.example.dogs.networking.viewModel

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogs.koin.DogsRepository
import com.example.dogs.networking.interfaces.BreedSelected
import com.example.dogs.networking.model.breeds.Breeds
import com.example.dogs.networking.model.dogs.Dogs
import com.example.dogs.utils.network.ConnectivityObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val repository: DogsRepository,
    connectivityObserver: ConnectivityObserver
) : ViewModel(), BreedSelected {

    private val _dogsState = MutableStateFlow<DogsState>(DogsState.Error("Unknown Error"))
    private val dogsState: StateFlow<DogsState> = _dogsState.asStateFlow()

    private val _breedState = MutableStateFlow<BreedsState>(BreedsState.Error("Unknown Error"))
    private val breedState: StateFlow<BreedsState> = _breedState.asStateFlow()

    private val _filterState = MutableStateFlow<BreedsState>(BreedsState.Error("Unknown Error"))
    private val filterState: StateFlow<BreedsState> = _filterState.asStateFlow()

    private val _filterDogState =
        MutableStateFlow<FilterDogsState>(FilterDogsState.Error("Unknown Error"))
    private val filterDogState: StateFlow<FilterDogsState> = _filterDogState.asStateFlow()

    var dogsList = ArrayList<Dogs>()
    var breedsList = ArrayList<Breeds>()
    var dogsFiltered = ArrayList<Dogs>()

    var isLoading = mutableStateOf(false)
    var isSuccess = mutableStateOf(false)
    var isFilteredSuccess = mutableStateOf(false)
    var isError = mutableStateOf(false)
    var errorMessage = mutableStateOf("")

    private var currentPage = mutableIntStateOf(2)

    private var maxFilterItemNumber = 19

    val networkStatus: StateFlow<ConnectivityObserver.Status> =
        connectivityObserver.observe()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = ConnectivityObserver.Status.Unavailable
            )

    sealed class DogsState {
        data class Success(val dogs: List<Dogs>) : DogsState()
        data class Error(val message: String) : DogsState()
    }

    sealed class FilterDogsState {
        data class Success(val dog: Dogs) : FilterDogsState()
        data class Error(val message: String) : FilterDogsState()
    }

    sealed class BreedsState {
        data class Success(val breeds: List<Breeds>) : BreedsState()
        data class Error(val message: String) : BreedsState()
    }

    fun fetchDogs() {
        viewModelScope.launch {
            try {
                isLoading.value = true
                if (networkStatus.value == ConnectivityObserver.Status.Unavailable) {
                    _dogsState.value = DogsState.Error("No Internet Connection")
                    isLoading.value = false
                    return@launch
                }
                val page = currentPage.intValue++
                val response = repository.getDogsImages(page)
                if (response.isSuccessful && response.body() != null) {
                    _dogsState.value = DogsState.Success(response.body() ?: emptyList())
                } else {
                    _dogsState.value = DogsState.Error(response.message())
                }
            } catch (e: Exception) {
                _dogsState.value = DogsState.Error(e.message ?: "Unknown Error")
            } finally {
                observeDogsResponse()
            }
        }
    }

    private fun observeDogsResponse() {
        viewModelScope.launch {
            dogsState.collect { it ->
                when (it) {
                    is DogsState.Error -> {
                        isLoading.value = false
                        isSuccess.value = false
                        errorMessage.value = it.message
                        isError.value = true
                    }

                    is DogsState.Success -> {
                        val newData = ArrayList<Dogs>()
                        newData.addAll(dogsList)
                        newData.addAll(it.dogs)
                        dogsList = newData.distinctBy { it.id } as ArrayList<Dogs>

                        isLoading.value = false
                        errorMessage.value = ""
                        isError.value = false
                        if (breedsList.isEmpty()) {
                            fetchAllBreeds()
                        } else {
                            isSuccess.value = true
                        }
                    }
                }
            }
        }
    }

    private fun fetchAllBreeds() {
        viewModelScope.launch {
            try {
                isLoading.value = true
                if (networkStatus.value == ConnectivityObserver.Status.Unavailable) {
                    _breedState.value = BreedsState.Error("No Internet Connection")
                    isLoading.value = false
                    return@launch
                }
                val response = repository.getAllBreeds()
                if (response.isSuccessful && response.body() != null) {
                    _breedState.value = BreedsState.Success(response.body() ?: emptyList())
                } else {
                    _breedState.value = BreedsState.Error(response.message())
                }
            } catch (e: Exception) {
                _breedState.value = BreedsState.Error(e.message ?: "Unknown Error")
            } finally {
                observeBreedResponse()
            }
        }
    }

    private fun observeBreedResponse() {
        viewModelScope.launch {
            breedState.collect { it ->
                when (it) {
                    is BreedsState.Error -> {
                        isLoading.value = false
                        isSuccess.value = false
                        errorMessage.value = it.message
                        isError.value = true
                    }

                    is BreedsState.Success -> {
                        breedsList = it.breeds as ArrayList<Breeds>
                        isLoading.value = false
                        errorMessage.value = ""
                        isError.value = false
                        isSuccess.value = true
                    }
                }
            }
        }
    }

    private fun filterByBreedType(breedType: String) {
        viewModelScope.launch {
            try {
                isLoading.value = false
                if (networkStatus.value == ConnectivityObserver.Status.Unavailable) {
                    _filterState.value = BreedsState.Error("No Internet Connection")
                    isLoading.value = false
                    return@launch
                }
                val response = repository.filterByBreed(breedType)
                if (response.isSuccessful && response.body() != null) {
                    _filterState.value = BreedsState.Success(response.body() ?: emptyList())
                } else {
                    _filterState.value = BreedsState.Error(response.message())
                }
            } catch (e: Exception) {
                _filterState.value = BreedsState.Error(e.message ?: "Unknown Error")
            } finally {
                observeFilterResponse()
            }
        }
    }

    private fun observeFilterResponse() {
        viewModelScope.launch {
            filterState.collect { it ->
                when (it) {
                    is BreedsState.Error -> {
                        isLoading.value = false
                        isFilteredSuccess.value = false
                        errorMessage.value = it.message
                        isError.value = true
                    }

                    is BreedsState.Success -> {
                        it.breeds.forEachIndexed { index, dog ->
                            if (index <= maxFilterItemNumber) {
                                getSpecificDog(dog.reference_image_id, index, it.breeds.size)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getSpecificDog(id: String, index: Int, size: Int) {
        viewModelScope.launch {
            try {
                isLoading.value = false
                if (networkStatus.value == ConnectivityObserver.Status.Unavailable) {
                    _filterDogState.value = FilterDogsState.Error("No Internet Connection")
                    isLoading.value = false
                    return@launch
                }
                val response = repository.getSpecificDog(id)
                if (response.isSuccessful && response.body() != null) {
                    _filterDogState.value = FilterDogsState.Success(response.body()!!)
                } else {
                    _filterDogState.value = FilterDogsState.Error(response.message())
                }
            } catch (e: Exception) {
                _filterDogState.value = FilterDogsState.Error(e.message ?: "Unknown Error")
            } finally {
                observeFilterDogsResponse(index, size)
            }
        }
    }

    private fun observeFilterDogsResponse(index: Int, size: Int) {
        viewModelScope.launch {
            filterDogState.collect { it ->
                when (it) {
                    is FilterDogsState.Error -> {
                        isLoading.value = false
                        isFilteredSuccess.value = false
                        errorMessage.value = it.message
                        isError.value = true
                    }

                    is FilterDogsState.Success -> {
                        dogsFiltered.add(it.dog)
                        if (index == size - 1) {
                            isLoading.value = false
                            isError.value = false
                            errorMessage.value = ""
                            isFilteredSuccess.value = true
                        }
                    }
                }
            }
        }
    }

    override suspend fun onBreedSelected(breedType: String) {
        isFilteredSuccess.value = false
        isSuccess.value = false
        dogsFiltered = arrayListOf()
        filterByBreedType(breedType)
    }
}