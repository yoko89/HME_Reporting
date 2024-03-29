package com.neklaway.hme_reporting.feature_time_sheet.presentation.hme_code

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.neklaway.hme_reporting.common.presentation.common.component.DeleteDialog
import com.neklaway.hme_reporting.common.presentation.common.component.DropDown
import kotlinx.coroutines.flow.Flow


@Composable
fun HMECodeScreen(
    state: HMECodeState,
    userMessage: Flow<String>,
    userEvent: (HMECodeUserEvents) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }


    LaunchedEffect(key1 = userMessage) {
        userMessage.collect {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        floatingActionButton = {
            Row {
                AnimatedVisibility(
                    state.selectedHMECode != null,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    Row {
                        FloatingActionButton(onClick = {
                            userEvent(HMECodeUserEvents.UpdateHMECode)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit HME Code"
                            )
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                    }
                }
                FloatingActionButton(onClick = {
                    userEvent(HMECodeUserEvents.SaveHMECode)
                }) {

                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add HME Code")
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(5.dp)
        ) {

            DropDown(
                dropDownList = state.customers,
                selectedValue = state.selectedCustomer?.name ?: "No Customer Selected",
                label = "Customer",
                dropDownContentDescription = "Select Customer",
                modifier = Modifier.padding(bottom = 5.dp)
            ) { customer ->
                userEvent(HMECodeUserEvents.CustomerSelected(customer))
            }

            OutlinedTextField(
                value = state.hmeCode,
                onValueChange = { hmeCode ->
                    userEvent(HMECodeUserEvents.HmeCodeChanged(hmeCode))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "HME Code") },
                singleLine = true,
                maxLines = 1
            )


            AnimatedVisibility(visible = !state.isIbau) {

                OutlinedTextField(
                    value = state.machineType, onValueChange = { machineType ->
                        userEvent(HMECodeUserEvents.MachineTypeChanged(machineType))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Machine Type") },
                    singleLine = true,
                    maxLines = 1
                )
            }

            AnimatedVisibility(visible = !state.isIbau) {

                OutlinedTextField(
                    value = state.machineNumber, onValueChange = { machineNumber ->
                        userEvent(HMECodeUserEvents.MachineNumberChanged(machineNumber))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Machine Number") },
                    singleLine = true,
                    maxLines = 1
                )
            }

            AnimatedVisibility(visible = !state.isIbau) {

                OutlinedTextField(
                    value = state.workDescription, onValueChange = { workDescription ->
                        userEvent(HMECodeUserEvents.WorkDescriptionChanged(workDescription))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Work Description") },
                    singleLine = true,
                    maxLines = 1
                )
            }

            LazyColumn(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                item {
                    AnimatedVisibility(
                        visible = state.loading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        CircularProgressIndicator()
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "HME\nCode",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )

                        AnimatedVisibility(
                            visible = !state.isIbau,
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "Machine\nType",
                                textAlign = TextAlign.Center,
                            )
                        }

                        AnimatedVisibility(
                            visible = !state.isIbau,
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "Machine\nNumber",
                                textAlign = TextAlign.Center,
                            )
                        }
                        Spacer(modifier = Modifier.weight(0.5f))
                    }
                }

                items(items = state.hmeCodes) { hmeCode ->
                    val visibility = remember {
                        mutableStateOf(false)
                    }

                    SideEffect {
                        visibility.value = true
                    }

                    var deleteDialogVisible by remember {
                        mutableStateOf(false)
                    }

                    AnimatedVisibility(visible = deleteDialogVisible) {
                        DeleteDialog(item = hmeCode,
                            onConfirm = {
                                userEvent(HMECodeUserEvents.DeleteHMECode(it))
                                deleteDialogVisible = false
                            },
                            onDismiss = { deleteDialogVisible = false }
                        )
                    }

                    AnimatedVisibility(
                        visible = visibility.value,
                        enter = slideInHorizontally(),
                        exit = slideOutHorizontally()
                    ) {

                        Card(
                            modifier = Modifier
                                .padding(all = 2.dp)
                                .clickable { userEvent(HMECodeUserEvents.HmeCodeSelected(hmeCode)) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Column(Modifier.weight(1f)) {


                                    Row(
                                        Modifier
                                            .padding(5.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            text = hmeCode.code,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.weight(1f)
                                        )

                                        AnimatedVisibility(
                                            visible = !state.isIbau,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = hmeCode.machineType.orEmpty(),
                                                textAlign = TextAlign.Center,
                                            )
                                        }

                                        AnimatedVisibility(
                                            visible = !state.isIbau,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = hmeCode.machineNumber.orEmpty(),
                                                textAlign = TextAlign.Center,

                                                )
                                        }
                                    }

                                    AnimatedVisibility(visible = !state.isIbau) {

                                        Text(
                                            text = hmeCode.workDescription.orEmpty(),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(5.dp)
                                        )
                                    }
                                }

                                OutlinedIconButton(
                                    onClick = {
                                        deleteDialogVisible = true
                                    },
                                    modifier = Modifier
                                        .weight(0.15f)
                                        .padding(all = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete HME",
                                        tint = Color.Red,
                                        modifier = Modifier.alpha(.6f)
                                    )
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}




