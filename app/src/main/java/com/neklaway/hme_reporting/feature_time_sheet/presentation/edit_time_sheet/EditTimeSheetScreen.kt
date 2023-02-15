@file:OptIn(ExperimentalMaterial3Api::class)

package com.neklaway.hme_reporting.feature_time_sheet.presentation.edit_time_sheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.neklaway.hme_reporting.common.presentation.Screen
import com.neklaway.hme_reporting.common.presentation.common.component.CustomDatePicker
import com.neklaway.hme_reporting.common.presentation.common.component.CustomTimePicker
import com.neklaway.hme_reporting.common.presentation.common.component.Selector
import com.neklaway.hme_reporting.common.ui.theme.HMEReportingTheme
import com.neklaway.hme_reporting.utils.toDate
import com.neklaway.hme_reporting.utils.toTime
import java.util.*


@Composable
fun EditTimeSheetScreen(
    navController: NavController,
    viewModel: EditTimeSheetViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val event = viewModel.event

    val dateInteractionSource = remember { MutableInteractionSource() }
    val travelStartInteractionSource = remember { MutableInteractionSource() }
    val travelEndInteractionSource = remember { MutableInteractionSource() }
    val workStartInteractionSource = remember { MutableInteractionSource() }
    val workEndInteractionSource = remember { MutableInteractionSource() }



    LaunchedEffect(key1 = event) {
        event.collect { event ->
            when(event){
                EditTimeSheetEvents.PopBackStack -> navController.popBackStack(Screen.TimeSheet.route,false)
                is EditTimeSheetEvents.UserMessage -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    LaunchedEffect(key1 = dateInteractionSource) {
        dateInteractionSource.interactions.collect {
            when (it) {
                is PressInteraction.Release -> viewModel.dateClicked()
            }
        }
    }

    LaunchedEffect(key1 = travelStartInteractionSource) {
        travelStartInteractionSource.interactions.collect {
            when (it) {
                is PressInteraction.Release -> state.date?.let { viewModel.travelStartClicked() }
            }
        }
    }

    LaunchedEffect(key1 = workStartInteractionSource) {
        workStartInteractionSource.interactions.collect {
            when (it) {
                is PressInteraction.Release -> state.date?.let { viewModel.workStartClicked() }
            }
        }
    }
    LaunchedEffect(key1 = workEndInteractionSource) {
        workEndInteractionSource.interactions.collect {
            when (it) {
                is PressInteraction.Release -> state.date?.let { viewModel.workEndClicked() }
            }
        }
    }
    LaunchedEffect(key1 = travelEndInteractionSource) {
        travelEndInteractionSource.interactions.collect {
            when (it) {
                is PressInteraction.Release -> state.date?.let { viewModel.travelEndClicked() }
            }
        }
    }


    LaunchedEffect(key1 = true) {
        dateInteractionSource.interactions.collect {
            when (it) {
                is PressInteraction.Release -> viewModel.dateClicked()
            }
        }
    }

    if (state.showDatePicker) {
        CustomDatePicker(
            year = state.date?.get(Calendar.YEAR),
            month = state.date?.get(Calendar.MONTH),
            day = state.date?.get(Calendar.DAY_OF_MONTH),
            dateSet =
            { year, month, day ->
                viewModel.datePicked(year, month, day)
            },
            canceled = {
                viewModel.dateShown()
            }
        )
    }


    if (state.showTimePickerTravelStart) {
        CustomTimePicker(
            hour = state.travelStart?.get(Calendar.HOUR_OF_DAY),
            minute = state.travelStart?.get(Calendar.MINUTE),
            timeSet = { hour, minute ->
                viewModel.travelStartPicked(hour, minute)
            },
            canceled = {
                viewModel.timePickerShown()
            }
        )
    }


    if (state.showTimePickerTravelEnd) {
        CustomTimePicker(
            hour = state.travelEnd?.get(Calendar.HOUR_OF_DAY),
            minute = state.travelEnd?.get(Calendar.MINUTE),
            timeSet = { hour, minute ->
                viewModel.travelEndPicked(hour, minute)
            },
            canceled = {
                viewModel.timePickerShown()
            }
        )
    }


    if (state.showTimePickerWorkStart) {
        CustomTimePicker(
            hour = state.workStart?.get(Calendar.HOUR_OF_DAY),
            minute = state.workStart?.get(Calendar.MINUTE),
            timeSet = { hour, minute ->
                viewModel.workStartPicked(hour, minute)
            },
            canceled = {
                viewModel.timePickerShown()
            }
        )
    }


    if (state.showTimePickerWorkEnd) {
        CustomTimePicker(
            hour = state.workEnd?.get(Calendar.HOUR_OF_DAY),
            minute = state.workEnd?.get(Calendar.MINUTE),
            timeSet = { hour, minute ->
                viewModel.workEndPicked(hour, minute)
            },
            canceled = {
                viewModel.timePickerShown()
            }
        )
    }

    HMEReportingTheme {
        Scaffold(
            floatingActionButton = {
                Row {
                    FloatingActionButton(onClick = { viewModel.deleteTimeSheet() }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Time Sheet"
                        )
                    }

                    Spacer(modifier = Modifier.width(5.dp))

                FloatingActionButton(onClick = { viewModel.updateTimeSheet() }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Update Time Sheet"
                    )
                }
            }},
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) { paddingValues ->
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues)
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {


                AnimatedVisibility(
                    visible = state.loading,
                    enter = slideInHorizontally(initialOffsetX = {
                        -it
                    }),
                    exit = slideOutHorizontally(targetOffsetX = { -it })
                ) {
                    CircularProgressIndicator()
                }

                AnimatedVisibility(
                    visible = !state.noWorkday,
                    enter = slideInHorizontally(initialOffsetX = {
                        -it
                    }),
                    exit = slideOutHorizontally(targetOffsetX = { -it })
                ) {
                    Selector(
                        text = "Travel Day",
                        checked = state.travelDay,
                        onCheckedChange = { viewModel.travelDayChanged(it) })
                }

                AnimatedVisibility(
                    visible = !state.travelDay,
                    enter = slideInHorizontally(initialOffsetX = {
                        -it
                    }),
                    exit = slideOutHorizontally(targetOffsetX = { -it })
                ) {
                    Selector(
                        text = "Weekend/Day off ",
                        checked = state.noWorkday,
                        onCheckedChange = { viewModel.noWorkDayChanged(it) })
                }
                OutlinedTextField(
                    value = state.date.toDate(),
                    onValueChange = {},
                    label = { Text(text = "Date") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    readOnly = true,
                    interactionSource = dateInteractionSource
                )



                AnimatedVisibility(
                    visible = !state.noWorkday,
                    enter = slideInHorizontally(initialOffsetX = {
                        -it
                    }),
                    exit = slideOutHorizontally(targetOffsetX = { -it })
                ) {
                    OutlinedTextField(
                        value = state.travelStart.toTime(),
                        onValueChange = {},
                        label = { Text(text = "Travel Start") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        readOnly = true,
                        enabled = state.date != null,
                        interactionSource = travelStartInteractionSource
                    )
                }

                AnimatedVisibility(
                    visible = !state.travelDay and !state.noWorkday,
                    enter = slideInHorizontally(initialOffsetX = {
                        -it
                    }),
                    exit = slideOutHorizontally(targetOffsetX = { -it })
                ) {
                    OutlinedTextField(
                        value = state.workStart.toTime(),
                        onValueChange = {},
                        label = { Text(text = "Work Start") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        readOnly = true,
                        interactionSource = workStartInteractionSource
                    )
                }

                AnimatedVisibility(
                    visible = !state.travelDay and !state.noWorkday,
                    enter = slideInHorizontally(initialOffsetX = {
                        -it
                    }),
                    exit = slideOutHorizontally(targetOffsetX = { -it })
                ) {
                    OutlinedTextField(
                        value = state.workEnd.toTime(),
                        onValueChange = {},
                        label = { Text(text = "Work End") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        readOnly = true,
                        interactionSource = workEndInteractionSource
                    )
                }

                AnimatedVisibility(
                    visible = !state.noWorkday,
                    enter = slideInHorizontally(initialOffsetX = {
                        -it
                    }),
                    exit = slideOutHorizontally(targetOffsetX = { -it })
                ) {
                    OutlinedTextField(
                        value = state.travelEnd.toTime(),
                        onValueChange = {},
                        label = { Text(text = "Travel End") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        readOnly = true,
                        interactionSource = travelEndInteractionSource
                    )
                }


                AnimatedVisibility(
                    visible = !state.travelDay and !state.noWorkday,
                    enter = slideInHorizontally(initialOffsetX = {
                        -it
                    }),
                    exit = slideOutHorizontally(targetOffsetX = { -it })
                ) {
                    OutlinedTextField(
                        value = state.breakDuration,
                        onValueChange = {
                            viewModel.breakDurationChanged(it)
                        },
                        label = { Text(text = "Break Duration") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }

                AnimatedVisibility(
                    visible = !state.noWorkday,
                    enter = slideInHorizontally(initialOffsetX = {
                        -it
                    }),
                    exit = slideOutHorizontally(targetOffsetX = { -it })
                ) {
                    OutlinedTextField(
                        value = state.traveledDistance,
                        onValueChange = {
                            viewModel.travelDistanceChanged(it)
                        },
                        label = { Text(text = "Travel Distance") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        }
    }
}



