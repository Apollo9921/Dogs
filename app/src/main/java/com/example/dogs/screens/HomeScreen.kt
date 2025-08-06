package com.example.dogs.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.example.dogs.R
import com.example.dogs.components.TopBar
import com.example.dogs.core.Primary

@Composable
fun HomeScreen(navHostController: NavHostController) {
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
                    .padding(top = it.calculateTopPadding())
            ) {

            }
        }
    )
}