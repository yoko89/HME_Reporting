package com.neklaway.hme_reporting.feature_time_sheet.presentation.edit_time_sheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.navigation.NavController
import com.neklaway.hme_reporting.common.presentation.Screen
import com.neklaway.hme_reporting.common.presentation.common.component.CustomDatePicker
import com.neklaway.hme_reporting.common.presentation.common.component.CustomTimePicker
import com.neklaway.hme_reporting.common.presentation.common.component.DropDown
import com.neklaway.hme_reporting.common.presentation.common.component.Selector
import com.neklaway.hme_reporting.utils.toDate
import com.neklaway.hme_reporting.utils.toTime
import kotlinx.coroutines.flow.Flow
import java.util.Calendar


@Composable
fun EditTimeSheetScreen(
    navController: NavController,
    state: EditTimeSheetState,
    uiEvent: Flow<EditTimeSheetUiEvents>,
    userEvents: (EditTimeSheetUserEvents) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val dateInteractionSource = remember { MutableInteractionSource() }
    val travelStartInteractionSource = remember { MutableInteractionSource() }
    val travelEndInteractionSource = remember { MutableInteractionSource() }
    val workStartInteractionSource = remember { MutableInteractionSource() }
    val workEndInteractionSource = remember { MutableInteractionSource() }



    LaunchedEffect(key1 = uiEvent) {
        uiEvent.collect { event ->
            when (event) {
                EditTimeSheetUiEvents.PopBackStack -> navController.popBackStack(
                    Screen.TimeSheet.route,
                    false
                )

                is EditTimeSheetUiEvents.UserMessage -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    LaunchedEffect(key1 = dateInteractionSource) {
        dateInteractionSource.interactions.collect {
            when (it) {
                is PressInteraction.Release -> userEvents(EditTimeSheetUserEvents.DateClicked)
            }
        }
    }

    LaunchedEffect(key1 = travelStartInteractionSource) {
        travelStartInteractionSource.interactions.collect {
            when (it) {
                is PressInteraction.Release -> state.date?.let { userEvents(EditTimeSheetUserEvents.TravelStartClicked) }
            }
        }
    }

    LaunchedEffect(key1 = workStartInteractionSource) {
        workStartInteractionSource.interactions.collect {
            when (it) {
                is PressInteraction.Release -> state.date?.let { userEvents(EditTimeSheetUserEvents.WorkStartClicked) }
            }
        }
    }
    LaunchedEffect(key1 = workEndInteractionSource) {
        workEndInteractionSource.interactions.collect {
            when (it) {
                is PressInteraction.Release -> state.date?.let { userEvents(EditTimeSheetUserEvents.WorkEndClicked) }
            }
        }
    }
    LaunchedEffect(key1 = travelEndInteractionSource) {
        travelEndInteractionSource.interactions.collect {
            when (it) {
                is PressInteraction.Release -> state.date?.let { userEvents(EditTimeSheetUserEvents.TravelEndClicked) }
            }
        }
    }


    LaunchedEffect(key1 = true) {
        dateInteractionSource.interactions.collect {
            when (it) {
                is PressInteraction.Release -> userEvents(EditTimeSheetUserEvents.DateClicked)
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
                userEvents(EditTimeSheetUserEvents.DatePicked(year, month, day))
            },
            canceled = {
                userEvents(EditTimeSheetUserEvents.DateShown)
            }
        )
    }


    if (state.showTimePickerTravelStart) {
        CustomTimePicker(
            hour = state.travelStart?.get(Calendar.HOUR_OF_DAY),
            minute = state.travelStart?.get(Calendar.MINUTE),
            timeSet = { hour, minute ->
                userEvents(EditTimeSheetUserEvents.TravelStartPicked(hour, minute))
            },
            canceled = {
                userEvents(EditTimeSheetUserEvents.TimePickerShown)
            }
        )
    }


    if (state.showTimePickerTravelEnd) {
        CustomTimePicker(
            hour = state.travelEnd?.get(Calendar.HOUR_OF_DAY),
            minute = state.travelEnd?.get(Calendar.MINUTE),
            timeSet = { hour, minute ->
                userEvents(EditTimeSheetUserEvents.TravelEndPicked(hour, minute))
            },
            canceled = {
                userEvents(EditTimeSheetUserEvents.TimePickerShown)
            }
        )
    }


    if (state.showTimePickerWorkStart) {
        CustomTimePicker(
            hour = state.workStart?.get(Calendar.HOUR_OF_DAY),
            minute = state.workStart?.get(Calendar.MINUTE),
            timeSet = { hour, minute ->
                userEvents(EditTimeSheetUserEvents.WorkStartPicked(hour, minute))
            },
            canceled = {
                userEvents(EditTimeSheetUserEvents.TimePickerShown)
            }
        )
    }


    if (state.showTimePickerWorkEnd) {
        CustomTimePicker(
            hour = state.workEnd?.get(Calendar.HOUR_OF_DAY),
            minute = state.workEnd?.get(Calendar.MINUTE),
            timeSet = { hour, minute ->
                userEvents(EditTimeSheetUserEvents.WorkEndPicked(hour, minute))
            },
            canceled = {
                userEvents(EditTimeSheetUserEvents.TimePickerShown)
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            Row {
                FloatingActionButton(onClick = { userEvents(EditTimeSheetUserEvents.DeleteTimeSheet) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Time Sheet"
                    )
                }

                Spacer(modifier = Modifier.width(5.dp))

                FloatingActionButton(onClick = { userEvents(EditTimeSheetUserEvents.UpdateTimeSheet) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Update Time Sheet"
                    )
                }
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
                dropDownList = state.hmeCodes,
                selectedValue = state.selectedHMECode?.code ?: "No HME Code Selected",
                label = "HME Code",
                dropDownContentDescription = "Select HME Code",
                modifier = Modifier.padding(bottom = 5.dp)
            ) { hmeCode ->
                userEvents(EditTimeSheetUserEvents.HmeSelected(hmeCode))
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
                    userEvents(EditTimeSheetUserEvents.IbauSelected(ibauCode))
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
                    onCheckedChange = { userEvents(EditTimeSheetUserEvents.TravelDayChanged(it)) })
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
                    onCheckedChange = { userEvents(EditTimeSheetUserEvents.NoWorkDayChanged(it)) })
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
                        userEvents(EditTimeSheetUserEvents.BreakDurationChanged(it))
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
                        userEvents(EditTimeSheetUserEvents.TravelDistanceChanged(it))
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



