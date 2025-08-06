package com.example.dogs.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.example.dogs.R
import com.example.dogs.components.ErrorBar
import com.example.dogs.components.LoadingBar
import com.example.dogs.components.TopBar
import com.example.dogs.core.Primary
import com.example.dogs.core.White
import com.example.dogs.networking.model.Dogs
import com.example.dogs.networking.viewModel.HomeScreenViewModel
import com.example.dogs.utils.network.ConnectivityObserver
import com.example.dogs.utils.size.ScreenSizeUtils
import org.koin.androidx.compose.koinViewModel

private var viewModel: HomeScreenViewModel? = null
private var isConnected = mutableStateOf(false)

@Composable
fun HomeScreen(navHostController: NavHostController) {
    viewModel = koinViewModel<HomeScreenViewModel>()
    val networkStatus = viewModel?.networkStatus?.collectAsState()
    if (networkStatus?.value == ConnectivityObserver.Status.Available && !isConnected.value) {
        isConnected.value = true
        viewModel?.fetchDogs()
    } else if (networkStatus?.value == ConnectivityObserver.Status.Unavailable && !isConnected.value) {
        viewModel?.errorMessage?.value = stringResource(R.string.no_internet_connection)
        viewModel?.isError?.value = true
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        topBar = {
            TopBar(
                title = stringResource(R.string.home_screen),
                isBackEnabled = false,
                isFilterEnabled = true,
                navHostController = navHostController
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Primary)
                    .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
            ) {
                when {
                    viewModel?.isLoading?.value == true || viewModel?.isSuccess?.value == true -> {
                        val dogsList = viewModel?.dogsList ?: ArrayList()
                        if (dogsList.isNotEmpty()) {
                            DogsImageList(dogsList)
                        } else {
                            LoadingBar()
                        }
                    }

                    viewModel?.isError?.value == true -> {
                        ErrorBar(viewModel?.errorMessage?.value ?: "Unknown Error")
                    }
                }
            }
        }
    )
}

@Composable
private fun DogsImageList(dogsList: ArrayList<Dogs>) {
    val imageSize = ScreenSizeUtils.calculateCustomWidth(150).dp
    val lazyGridState = rememberLazyGridState()
    val imageLoadingStates = remember { mutableStateMapOf<String, AsyncImagePainter.State>() }
    var allImagesLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(imageLoadingStates.toMap(), dogsList) {
        allImagesLoaded = if (dogsList.isNotEmpty()) {
            dogsList.all { dog ->
                val imageUrl = dog.url
                imageLoadingStates[imageUrl] is AsyncImagePainter.State.Success || imageLoadingStates[imageUrl] is AsyncImagePainter.State.Error
            }
        } else {
            true
        }
    }

    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        state = lazyGridState,
        columns = GridCells.Fixed(2),
        content = {
            items(dogsList.size) { it ->
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(horizontal = 15.dp, vertical = 15.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = dogsList[it].url,
                        placeholder = painterResource(R.drawable.ic_launcher_background),
                        contentDescription = null,
                        onError = { state ->
                            imageLoadingStates[dogsList[it].url] = state
                        },
                        onSuccess = { state ->
                            imageLoadingStates[dogsList[it].url] = state
                        },
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(imageSize)
                    )
                }
            }
        }
    )
    if (lazyGridState.isScrolledToTheEnd() && imageLoadingStates.size == dogsList.size) {
        if (viewModel?.isLoading?.value == false) {
            viewModel?.fetchDogs()
        }
    }
    CheckIfIsLoading(dogsList)
}

@Composable
private fun CheckIfIsLoading(dogsList: ArrayList<Dogs>) {
    if (viewModel?.isLoading?.value == true && dogsList.isNotEmpty()) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(color = White)
        }
    }
}

private fun LazyGridState.isScrolledToTheEnd() =
    layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1