package com.example.dogs.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.dogs.R
import com.example.dogs.core.Secondary
import com.example.dogs.core.Typography
import com.example.dogs.core.White
import com.example.dogs.networking.model.breeds.Breeds
import com.example.dogs.networking.viewModel.HomeScreenViewModel
import com.example.dogs.utils.size.ScreenSizeUtils

private var openDialog = mutableStateOf(false)

@Composable
fun TopBar(
    title: String,
    isBackEnabled: Boolean,
    isFilterEnabled: Boolean,
    filterContent: SnapshotStateList<Breeds>,
    navHostController: NavHostController,
    viewModel: HomeScreenViewModel?
) {
    val titleSize = ScreenSizeUtils.calculateCustomWidth(baseSize = 20).sp
    if (!isBackEnabled) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Secondary)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                style = Typography.titleLarge.copy(fontSize = titleSize),
                text = title,
            )
            if (isFilterEnabled) {
                if (openDialog.value) {
                    ShowFilterOptions(filterContent, viewModel, openDialog)
                }
                val interactionSource = remember { MutableInteractionSource() }
                val filterImageSize = ScreenSizeUtils.calculateCustomWidth(baseSize = 30).dp
                Image(
                    painter = painterResource(id = R.drawable.filter),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(White),
                    modifier = Modifier
                        .size(filterImageSize)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            openDialog.value = true
                        }
                )
            }
        }
    }
}