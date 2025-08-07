package com.example.dogs.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.dogs.R
import com.example.dogs.components.ErrorBar
import com.example.dogs.components.LoadingBar
import com.example.dogs.components.TopBar
import com.example.dogs.core.Primary
import com.example.dogs.core.Typography
import com.example.dogs.networking.model.dogs.Dogs
import com.example.dogs.networking.viewModel.DetailScreenViewModel
import com.example.dogs.utils.network.ConnectivityObserver
import com.example.dogs.utils.size.ScreenSizeUtils
import org.koin.androidx.compose.koinViewModel

private var viewModel: DetailScreenViewModel? = null

@Composable
fun DetailScreen(navHostController: NavHostController, id: String) {
    viewModel = koinViewModel<DetailScreenViewModel>()
    var isConnected = remember { mutableStateOf(false) }
    val networkStatus = viewModel?.networkStatus?.collectAsState()
    if (networkStatus?.value == ConnectivityObserver.Status.Available && !isConnected.value) {
        isConnected.value = true
        viewModel?.fetchSpecificDog(id)
    } else if (networkStatus?.value == ConnectivityObserver.Status.Unavailable && !isConnected.value) {
        viewModel?.errorMessage?.value = stringResource(R.string.no_internet_connection)
        viewModel?.isError?.value = true
    }

    val isSuccess = viewModel?.isSuccess?.value
    val isLoading = viewModel?.isLoading?.value
    val isError = viewModel?.isError?.value
    val errorMessage = viewModel?.errorMessage?.value

    val dog = viewModel?.dog

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        topBar = {
            TopBar(
                title = stringResource(R.string.detail_screen),
                isBackEnabled = true,
                isFilterEnabled = false,
                filterContent = null,
                navHostController = navHostController,
                viewModel = null
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
                    isSuccess == true -> {
                        DetailContent(dog)
                    }

                    isLoading == true -> {
                        LoadingBar()
                    }

                    isError == true -> {
                        ErrorBar(errorMessage ?: "Unknown Error")
                    }
                }
            }
        }
    )
}

@Composable
private fun DetailContent(dog: Dogs?) {
    val scrollState = rememberScrollState()
    val parallaxFactor = 0.5f
    val imageSize = ScreenSizeUtils.calculateCustomWidth(200).dp
    val titleSize = ScreenSizeUtils.calculateCustomWidth(20).sp

    val breedFor = if (dog?.breeds?.first()?.bred_for?.isNotEmpty() == true) {
        dog.breeds.first().bred_for
    } else {
        stringResource(R.string.detail_unknown)
    }
    val breedGroup = if (dog?.breeds?.first()?.breed_group?.isNotEmpty() == true) {
        dog.breeds.first().breed_group
    } else {
        stringResource(R.string.detail_unknown)
    }
    val lifeSpan = if (dog?.breeds?.first()?.life_span?.isNotEmpty() == true) {
        dog.breeds.first().life_span
    } else {
        stringResource(R.string.detail_unknown)
    }
    val temperament = if (dog?.breeds?.first()?.temperament?.isNotEmpty() == true) {
        dog.breeds.first().temperament
    } else {
        stringResource(R.string.detail_unknown)
    }
    val origin = if (dog?.breeds?.first()?.origin?.isNotEmpty() == true) {
        dog.breeds.first().origin
    } else {
        stringResource(R.string.detail_unknown)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(imageSize)
                .clipToBounds(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = dog?.url,
                placeholder = painterResource(R.drawable.ic_launcher_background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f)
                    .graphicsLayer {
                        translationY = scrollState.value * parallaxFactor
                    }
            )
        }
        Spacer(Modifier.padding(15.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    style = Typography.titleLarge.copy(fontSize = titleSize),
                    text = dog?.breeds?.first()?.name ?: "",
                )
            }
            Spacer(Modifier.padding(15.dp))
            Content(
                title = stringResource(R.string.detail_breed_for_subtitle),
                description = breedFor
            )
            Content(
                title = stringResource(R.string.detail_breed_group_subtitle),
                description = breedGroup
            )
            Content(
                title = stringResource(R.string.detail_life_span_subtitle),
                description = lifeSpan
            )
            Content(
                title = stringResource(R.string.detail_temperament_subtitle),
                description = temperament
            )
            Content(
                title = stringResource(R.string.detail_origin_subtitle),
                description = origin
            )
        }
    }
}

@Composable
private fun Content(title: String, description: String) {
    val titleSize = ScreenSizeUtils.calculateCustomWidth(20).sp
    val subTitleSize = ScreenSizeUtils.calculateCustomWidth(18).sp
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            style = Typography.titleLarge.copy(fontSize = titleSize),
            text = title,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.padding(15.dp))
        Text(
            style = Typography.headlineLarge.copy(fontSize = subTitleSize),
            text = description,
            modifier = Modifier.weight(1f)
        )
    }
    Spacer(Modifier.padding(15.dp))
}