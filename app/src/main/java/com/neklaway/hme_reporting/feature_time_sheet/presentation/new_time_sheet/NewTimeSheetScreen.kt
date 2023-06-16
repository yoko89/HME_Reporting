package com.neklaway.hme_reporting.feature_time_sheet.presentation.new_time_sheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.neklaway.hme_reporting.common.presentation.common.component.CustomDatePicker
import com.neklaway.hme_reporting.common.presentation.common.component.CustomTimePicker
import com.neklaway.hme_reporting.common.presentation.common.component.DropDown
import com.neklaway.hme_reporting.common.presentation.common.component.Selector
import com.neklaway.hme_reporting.utils.toDate
import com.neklaway.hme_reporting.utils.toTime
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

@Composable
fun NewTimeSheetScreen(
    state: NewTimeSheetState,
    userMessage: Flow<String>,
    userEvents: (NewTimeSheetUserEvents) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val dateInteractionSource = remember { MutableInteractionSource() }
    val travelStartInteractionSource = remember { MutableInteractionSource() }
    val travelEndInteractionSource = remember { MutableInteractionSource() }
    val workStartInteractionSource = remember { MutableInteractionSource() }
    val workEndInteractionSource = remember { MutableInteractionSource() }



    LaunchedEffect(key1 = userMessage) {
        userMessage.collect {
            snackbarHostState.showSnackbar(it)
        }
    }


    LaunchedEffect(key1 = dateInteractionSource) {
        dateInteractionSource.interactions.collect {
            when (it) {
                is PressInteraction.Release -> userEvents(NewTimeSheetUserEvents.DateClicked)
            }
        }
    }

    LaunchedEffect(key1 = travelStartInteractionSource) {
        travelStartInteractionSource.interactions.collect {
            when (it) {
                is PressInteraction.Press -> userEvents(NewTimeSheetUserEvents.TravelStartClicked)
            }
        }
    }

    LaunchedEffect(key1 = workStartInteractionSource) {
        workStartInteractionSource.interactions.collect {
            when (it) {
                is PressInteraction.Release -> userEvents(NewTimeSheetUserEvents.WorkStartClicked)
            }
        }
    }
    LaunchedEffect(key1 = workEndInteractionSource) {
        workEndInteractionSource.interactions.collect {
            when (it) {
                is PressInteraction.Release ->  userEvents(NewTimeSheetUserEvents.WorkEndClicked)
            }
        }
    }
    LaunchedEffect(key1 = travelEndInteractionSource) {
        travelEndInteractionSource.interactions.collect {
            when (it) {
                is PressInteraction.Release -> userEvents(NewTimeSheetUserEvents.TravelEndClicked)
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
                userEvents(NewTimeSheetUserEvents.DatePicked(year, month, day))
            },
            canceled = {
                userEvents(NewTimeSheetUserEvents.DatePickedCanceled)
            }
        )
    }


    if (state.showTimePickerTravelStart) {
        CustomTimePicker(
            hour = state.travelStart?.get(Calendar.HOUR_OF_DAY),
            minute = state.travelStart?.get(Calendar.MINUTE),
            timeSet = { hour, minute ->
                userEvents(NewTimeSheetUserEvents.TravelStartPicked(hour, minute))
            },
            canceled = {
                userEvents(NewTimeSheetUserEvents.TimePickerShown)
            }
        )
    }


    if (state.showTimePickerTravelEnd) {
        CustomTimePicker(
            hour = state.travelEnd?.get(Calendar.HOUR_OF_DAY),
            minute = state.travelEnd?.get(Calendar.MINUTE),
            timeSet = { hour, minute ->
                userEvents(NewTimeSheetUserEvents.TravelEndPicked(hour, minute))
            },
            canceled = {
                userEvents(NewTimeSheetUserEvents.TimePickerShown)
            }
        )
    }


    if (state.showTimePickerWorkStart) {
        CustomTimePicker(
            hour = state.workStart?.get(Calendar.HOUR_OF_DAY),
            minute = state.workStart?.get(Calendar.MINUTE),
            timeSet = { hour, minute ->
                userEvents(NewTimeSheetUserEvents.WorkStartPicked(hour, minute))
            },
            canceled = {
                userEvents(NewTimeSheetUserEvents.TimePickerShown)
            }
        )
    }


    if (state.showTimePickerWorkEnd) {
        CustomTimePicker(
            hour = state.workEnd?.get(Calendar.HOUR_OF_DAY),
            minute = state.workEnd?.get(Calendar.MINUTE),
            timeSet = { hour, minute ->
                userEvents(NewTimeSheetUserEvents.WorkEndPicked(hour, minute))
            },
            canceled = {
                userEvents(NewTimeSheetUserEvents.TimePickerShown)
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { userEvents(NewTimeSheetUserEvents.InsertTimeSheet) }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Time Sheet"
                )
            }
        },
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
            DropDown(
                dropDownList = state.customers,
                selectedValue = state.selectedCustomer?.name ?: "No Customer Selected",
                label = "Customer",
                dropDownContentDescription = "Select Customer",
                modifier = Modifier.padding(vertical = 5.dp)
            ) { customer ->
                userEvents(NewTimeSheetUserEvents.CustomerSelected(customer))
            }

            DropDown(
                dropDownList = state.hmeCodes,
                selectedValue = state.selectedHMECode?.code ?: "No HME Code Selected",
                label = "HME Code",
                dropDownContentDescription = "Select HME Code",
                modifier = Modifier.padding(bottom = 5.dp)
            ) { hmeCode ->
                userEvents(NewTimeSheetUserEvents.HmeSelected(hmeCode))
            }

            AnimatedVisibility(
                visible = state.isIbau,
                enter = slideInHorizontally(initialOffsetX = {
                    -it
                }),
                exit = slideOutHorizontally(
                    targetOffsetX = {
                        -it
                    })
            ) {
                DropDown(
                    dropDownList = state.ibauCodes,
                    selectedValue = state.selectedIBAUCode?.code ?: "No IBAU Code Selected",
                    label = "IBAU Code",
                    dropDownContentDescription = "Select IBAU Code",
                    modifier = Modifier.padding(bottom = 5.dp)
                ) { ibauCode ->
                    userEvents(NewTimeSheetUserEvents.IbauSelected(ibauCode))
                }
            }


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
                    onCheckedChange = { userEvents(NewTimeSheetUserEvents.TravelDayChanged(it)) })
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
                    onCheckedChange = { userEvents(NewTimeSheetUserEvents.NoWorkDayChanged(it)) })
            }

            AnimatedVisibility(
                visible = !state.noWorkday and !state.travelDay,
                enter = slideInHorizontally(initialOffsetX = {
                    -it
                }),
                exit = slideOutHorizontally(targetOffsetX = { -it })
            ) {
                Selector(
                    text = "OverTime",
                    checked = state.overTimeDay,
                    onCheckedChange = { userEvents(NewTimeSheetUserEvents.OverTimeChanged(it)) })
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
                    interactionSource = travelStartInteractionSource,
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
                    enabled = state.date != null,
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
                    enabled = state.date != null,
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
                    enabled = state.date != null,
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
                        userEvents(NewTimeSheetUserEvents.BreakDurationChanged(it))
                    },
                    label = { Text(text = "Break Duration") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
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
                        userEvents(NewTimeSheetUserEvents.TravelDistanceChanged(it))
                    },
                    label = { Text(text = "Travel Distance") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        }
    }
}




