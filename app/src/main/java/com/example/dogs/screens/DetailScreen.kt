package com.example.dogs.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.example.dogs.R
import com.example.dogs.components.TopBar

@Composable
fun DetailScreen(navHostController: NavHostController, id: String) {
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

        }
    )
}