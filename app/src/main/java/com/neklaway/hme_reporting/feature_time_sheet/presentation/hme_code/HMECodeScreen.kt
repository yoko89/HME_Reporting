@file:OptIn(ExperimentalMaterial3Api::class)

package com.neklaway.hme_reporting.feature_time_sheet.presentation.hme_code

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.neklaway.hme_reporting.common.presentation.common.component.DropDown


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HMECodeScreen(
    viewModel: HMECodeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val userMessage = viewModel.userMessage


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
                            viewModel.updateHMECode()
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
                    viewModel.saveHMECode()
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
                modifier = Modifier.padding(bottom = 5.dp),
                dropDownList = state.customers,
                selectedValue = state.selectedCustomer?.name ?: "No Customer Selected",
                label = "Customer",
                dropDownContentDescription = "Select Customer",
                onSelect = { customer ->
                    viewModel.customerSelected(customer)
                }
            )

            OutlinedTextField(
                value = state.hmeCode,
                onValueChange = { hmeCode ->
                    viewModel.hmeCodeChanged(hmeCode)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "HME Code") },
                singleLine = true,
                maxLines = 1
            )


            AnimatedVisibility(visible = !state.isIbau) {

                OutlinedTextField(
                    value = state.machineType, onValueChange = { machineType ->
                        viewModel.machineTypeChanged(machineType)
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
                        viewModel.machineNumberChanged(machineNumber)
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
                        viewModel.workDescriptionChanged(workDescription)
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

                    AnimatedVisibility(
                        visible = visibility.value,
                        enter = slideInHorizontally(),
                        exit = slideOutHorizontally()
                    ) {

                        Card(
                            modifier = Modifier
                                .padding(all = 2.dp)
                                .clickable { viewModel.hmeCodeSelected(hmeCode) }
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
                                        viewModel.deleteHMECode(hmeCode)
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




