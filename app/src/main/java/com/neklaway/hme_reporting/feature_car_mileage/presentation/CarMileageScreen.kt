@file:OptIn(ExperimentalMaterial3Api::class)

package com.neklaway.hme_reporting.feature_car_mileage.presentation

import androidx.compose.animation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.neklaway.hme_reporting.common.presentation.common.component.CustomDatePicker
import com.neklaway.hme_reporting.common.presentation.common.component.CustomTimePicker
import com.neklaway.hme_reporting.common.ui.theme.HMEReportingTheme
import com.neklaway.hme_reporting.feature_time_sheet.presentation.new_time_sheet.component.CarMileageItemCard
import com.neklaway.hme_reporting.utils.toDate
import com.neklaway.hme_reporting.utils.toTime
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarMileageScreen(
    viewModel: CarMileageViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val userMessage = viewModel.userMessage


    val startDateInteractionSource = remember { MutableInteractionSource() }
    val startTimeInteractionSource = remember { MutableInteractionSource() }
    val endDateInteractionSource = remember { MutableInteractionSource() }
    val endTimeInteractionSource = remember { MutableInteractionSource() }

    LaunchedEffect(key1 = userMessage) {
        userMessage.collect {
            snackbarHostState.showSnackbar(it)
        }
    }

    LaunchedEffect(key1 = startDateInteractionSource) {
        startDateInteractionSource.interactions.collect {
            when (it) {
                is PressInteraction.Release -> viewModel.startDateClicked()
            }
        }
    }
    LaunchedEffect(key1 = endDateInteractionSource) {
        endDateInteractionSource.interactions.collect {
            when (it) {
                is PressInteraction.Release -> viewModel.endDateClicked()
            }
        }
    }
    LaunchedEffect(key1 = startTimeInteractionSource) {
        startTimeInteractionSource.interactions.collect {
            when (it) {
                is PressInteraction.Release -> viewModel.startTimeClicked()
            }
        }
    }

    LaunchedEffect(key1 = endTimeInteractionSource) {
        endTimeInteractionSource.interactions.collect {
            when (it) {
                is PressInteraction.Release -> viewModel.endTimeClicked()
            }
        }
    }

    if (state.showStartDatePicker) {
        CustomDatePicker(
            year = state.startDate?.get(Calendar.YEAR),
            month = state.startDate?.get(Calendar.MONTH),
            day = state.startDate?.get(Calendar.DAY_OF_MONTH),
            dateSet = { year, month, day ->
                viewModel.startDatePicked(year, month, day)
            },
            canceled = viewModel::dateTimePickedHide
        )
    }
    if (state.showStartTimePicker) {
        CustomTimePicker(
            hour = state.startTime?.get(Calendar.HOUR_OF_DAY),
            minute = state.startTime?.get(Calendar.MINUTE),
            timeSet = { hour, minute ->
                viewModel.startTimePicked(hour, minute)
            },
            canceled = viewModel::dateTimePickedHide
        )
    }

    if (state.showEndDatePicker) {
        CustomDatePicker(
            year = state.endDate?.get(Calendar.YEAR),
            month = state.endDate?.get(Calendar.MONTH),
            day = state.endDate?.get(Calendar.DAY_OF_MONTH),
            dateSet = { year, month, day ->
                viewModel.endDatePicked(year, month, day)
            },
            canceled = viewModel::dateTimePickedHide
        )
    }
    if (state.showEndTimePicker) {
        CustomTimePicker(
            hour = state.endTime?.get(Calendar.HOUR_OF_DAY),
            minute = state.endTime?.get(Calendar.MINUTE),
            timeSet = { hour, minute ->
                viewModel.endTimePicked(hour, minute)
            },
            canceled = viewModel::dateTimePickedHide
        )
    }


    HMEReportingTheme {
        Scaffold(floatingActionButton = {
            Row {
                AnimatedVisibility(
                    state.selectedCarMileage != null,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    Row {
                        FloatingActionButton(onClick = {
                            viewModel.updateCarMileage()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Car Mileage"
                            )
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                    }
                }
                FloatingActionButton(onClick = {
                    viewModel.saveCarMileage()
                }) {

                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Car Mileage")
                }
            }
        }, snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .padding(5.dp)
            ) {

                OutlinedTextField(
                    value = state.startDate.toDate(),
                    onValueChange = {},
                    label = { Text(text = "Start Date") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    interactionSource = startDateInteractionSource
                )

                OutlinedTextField(
                    value = state.startTime.toTime(),
                    onValueChange = {},
                    label = { Text(text = "Start Time") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    interactionSource = startTimeInteractionSource
                )

                OutlinedTextField(
                    value = state.startMileage,
                    onValueChange = { mileage ->
                        viewModel.startMileageChanged(mileage)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Start Mileage") },
                    singleLine = true,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = state.endDate.toDate(),
                    onValueChange = {},
                    label = { Text(text = "End Date") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    interactionSource = endDateInteractionSource
                )

                OutlinedTextField(
                    value = state.endTime.toTime(),
                    onValueChange = {},
                    label = { Text(text = "End Time") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    interactionSource = endTimeInteractionSource
                )

                OutlinedTextField(
                    value = state.endMileage,
                    onValueChange = { mileage ->
                        viewModel.endMileageChanged(mileage)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "End Mileage") },
                    singleLine = true,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                val onSurfaceColor = MaterialTheme.colorScheme.onSurface

                LazyColumn(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    item {
                        AnimatedVisibility(
                            visible = state.loading, enter = fadeIn(), exit = fadeOut()
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    item {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .drawBehind {

                                        val strokeWidth = Dp.Hairline.value
                                        val y = size.height

                                        drawLine(
                                            onSurfaceColor,
                                            Offset(0f, y),
                                            Offset((size.width * 0.85f), y),
                                            strokeWidth
                                        )
                                    },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Start Date",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f)
                                )

                                Text(
                                    text = "Start Time",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f)
                                )

                                Text(
                                    text = "Start Mileage",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f)
                                )

                                Spacer(modifier = Modifier.weight(0.5f))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "End Date",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f)
                                )

                                Text(
                                    text = "End Time",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f)
                                )

                                Text(
                                    text = "End Mileage",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f)
                                )

                                Spacer(modifier = Modifier.weight(0.5f))
                            }
                        }
                    }

                    items(items = state.carMileageList) { carMileage ->
                        var visibility by remember {
                            mutableStateOf(false)
                        }

                        LaunchedEffect(key1 = Unit) {
                            visibility = true
                        }
                        AnimatedVisibility(visible = visibility) {

                            CarMileageItemCard(carMileage = carMileage, cardClicked = {
                                viewModel.carMileageClicked(carMileage)
                            }, onDeleteClicked = {
                                viewModel.deleteCarMileage(carMileage)
                            })
                        }


                    }
                }
            }
        }
    }
}





