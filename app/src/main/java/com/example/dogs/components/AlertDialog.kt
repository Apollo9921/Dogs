package com.example.dogs.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.dogs.R
import com.example.dogs.core.Black
import com.example.dogs.core.Typography
import com.example.dogs.core.White
import com.example.dogs.networking.model.breeds.Breeds
import com.example.dogs.networking.viewModel.HomeScreenViewModel
import com.example.dogs.utils.size.ScreenSizeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private var selectedBreedIndex = mutableIntStateOf(-1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ShowFilterOptions(
    breedList: SnapshotStateList<Breeds>,
    viewModel: HomeScreenViewModel?,
    openDialog: MutableState<Boolean>
) {
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedBreedIndex.intValue =
                                        if (selectedBreedIndex.intValue == it) -1 else it
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Checkbox(
                                checked = selectedBreedIndex.intValue == it,
                                onCheckedChange = { isChecked ->
                                    selectedBreedIndex.intValue = if (isChecked) it else -1
                                }
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
                    onClick = {
                        filterByBreedName(viewModel, breedList, selectedBreedIndex)
                        openDialog.value = false
                    },
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
                Spacer(Modifier.padding(10.dp))
                Button(
                    onClick = {
                        selectedBreedIndex.intValue = -1
                        viewModel?.isFilteredSuccess?.value = false
                        viewModel?.dogsFiltered?.clear()
                        viewModel?.isSuccess?.value = true
                        openDialog.value = false
                    },
                    shape = RoundedCornerShape(percent = 30),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        style = Typography.titleLarge.copy(fontSize = optionSize),
                        text = stringResource(R.string.filter_reset),
                    )
                }
            }
        }
    )
}

private fun filterByBreedName(
    viewModel: HomeScreenViewModel?,
    breedList: SnapshotStateList<Breeds>,
    selectedBreedIndex: MutableIntState
) {
    CoroutineScope(Dispatchers.IO).launch {
        viewModel?.onBreedSelected(breedList[selectedBreedIndex.intValue].name)
    }
}