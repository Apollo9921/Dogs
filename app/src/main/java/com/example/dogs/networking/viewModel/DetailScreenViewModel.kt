package com.example.dogs.networking.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogs.koin.DogsRepository
import com.example.dogs.networking.model.dogs.Dogs
import com.example.dogs.utils.network.ConnectivityObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailScreenViewModel(
    private val repository: DogsRepository,
    connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _dogState = MutableStateFlow<DogState>(DogState.Error("Unknown Error"))
    private val dogState: StateFlow<DogState> = _dogState.asStateFlow()

    var dog: Dogs? = null

    var isLoading = mutableStateOf(false)
    var isSuccess = mutableStateOf(false)
    var isError = mutableStateOf(false)
    var errorMessage = mutableStateOf("")

    val networkStatus: StateFlow<ConnectivityObserver.Status> =
        connectivityObserver.observe()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = ConnectivityObserver.Status.Unavailable
            )

    sealed class DogState {
        data class Success(val dog: Dogs) : DogState()
        data class Error(val message: String) : DogState()
    }

    fun fetchSpecificDog(id: String) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                if (networkStatus.value == ConnectivityObserver.Status.Unavailable) {
                    _dogState.value = DogState.Error("No Internet Connection")
                    isLoading.value = false
                    return@launch
                }
                val response = repository.getSpecificDog(id)
                if (response.isSuccessful && response.body() != null) {
                    _dogState.value = DogState.Success(response.body()!!)
                } else {
                    _dogState.value = DogState.Error(response.message())
                }
            } catch (e: Exception) {
                _dogState.value = DogState.Error(e.message ?: "Unknown Error")
            } finally {
                observeResponse()
            }
        }
    }

    private fun observeResponse() {
        viewModelScope.launch {
            dogState.collect { it ->
                when (it) {
                    is DogState.Error -> {
                        isLoading.value = false
                        isSuccess.value = false
                        errorMessage.value = it.message
                        isError.value = true
                    }

                    is DogState.Success -> {
                        dog = it.dog
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