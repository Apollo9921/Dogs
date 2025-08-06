package com.example.dogs.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.example.dogs.R
import com.example.dogs.core.Black
import com.example.dogs.core.Secondary
import com.example.dogs.core.Typography
import com.example.dogs.core.White
import com.example.dogs.networking.model.breeds.Breeds
import com.example.dogs.utils.size.ScreenSizeUtils

private var openDialog = mutableStateOf(false)

@Composable
fun TopBar(
    title: String,
    isBackEnabled: Boolean,
    isFilterEnabled: Boolean,
    filterContent: SnapshotStateList<Breeds>,
    navHostController: NavHostController
) {
    val titleSize = ScreenSizeUtils.calculateCustomWidth(baseSize = 20).sp

    if (openDialog.value) {
        ShowFilterOptions(filterContent)
    }

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShowFilterOptions(breedList: SnapshotStateList<Breeds>) {
    val optionSize = ScreenSizeUtils.calculateCustomWidth(15).sp
    val listSize = ScreenSizeUtils.getScreenHeightDp() / 2
    BasicAlertDialog(
        modifier = Modifier
            .wrapContentSize()
            .background(White)
            .padding(20.dp),
        onDismissRequest = { openDialog.value = false },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            decorFitsSystemWindows = false
        ),
        content = {
            Column {
                LazyColumn(
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(listSize),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(breedList.size) { it ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Checkbox(
                                checked = false,
                                onCheckedChange = { }
                            )
                            Spacer(Modifier.padding(10.dp))
                            Text(
                                style = Typography.titleLarge.copy(fontSize = optionSize),
                                text = breedList[it].name,
                                color = Black
                            )
                        }
                    }
                }
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(percent = 30),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        style = Typography.titleLarge.copy(fontSize = optionSize),
                        text = stringResource(R.string.filter_confirm),
                    )
                }
            }
        }
    )
}