package com.example.dogs.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.dogs.core.Typography
import com.example.dogs.utils.size.ScreenSizeUtils

@Composable
fun ErrorBar(message: String) {
    val titleSize = ScreenSizeUtils.calculateCustomWidth(baseSize = 25).sp
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            style = Typography.titleLarge.copy(fontSize = titleSize),
            text = message,
        )
    }
}