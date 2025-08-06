package com.example.dogs.networking.viewModel

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogs.koin.DogsRepository
import com.example.dogs.networking.model.Dogs
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
) : ViewModel() {

    private val _dogsState = MutableStateFlow<DogsState>(DogsState.Error("Unknown Error"))
    private val dogsState: StateFlow<DogsState> = _dogsState.asStateFlow()

    var dogsList = ArrayList<Dogs>()

    var isLoading = mutableStateOf(false)
    var isSuccess = mutableStateOf(false)
    var isError = mutableStateOf(false)
    var errorMessage = mutableStateOf("")

    private var currentPage = mutableIntStateOf(2)

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
                observeResponse()
            }
        }
    }

    private fun observeResponse() {
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
                        isSuccess.value = true
                    }
                }
            }
        }
    }
}