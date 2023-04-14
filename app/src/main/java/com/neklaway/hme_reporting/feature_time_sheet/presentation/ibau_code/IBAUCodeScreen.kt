@file:OptIn(ExperimentalMaterial3Api::class)

package com.neklaway.hme_reporting.feature_time_sheet.presentation.ibau_code

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
fun IBAUCodeScreen(
    viewModel: IBAUCodeViewModel = hiltViewModel(),
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
                    state.selectedIBAUCode != null,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    Row {
                        FloatingActionButton(onClick = {
                            viewModel.updateIBAUCode()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit IBAU"
                            )
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                    }
                }

                FloatingActionButton(onClick = {
                    viewModel.saveIBAUCode()
                }) {

                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add IBAU Code")
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
                viewModel.customerSelected(customer)
            }

            DropDown(
                dropDownList = state.hmeCodes,
                selectedValue = state.selectedHMECode?.code ?: "No HME Code Selected",
                label = "HME Code",
                dropDownContentDescription = "Select HME Code",
                modifier = Modifier.padding(bottom = 5.dp)
            ) { hmeCode ->
                viewModel.hmeCodeSelected(hmeCode)
            }

            OutlinedTextField(
                value = state.ibauCode,
                onValueChange = { ibauCode ->
                    viewModel.ibauCodeChanged(ibauCode)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "IBAU Code") },
                singleLine = true,
                maxLines = 1
            )



            OutlinedTextField(
                value = state.machineType, onValueChange = { machineType ->
                    viewModel.machineTypeChanged(machineType)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Machine Type") },
                singleLine = true,
                maxLines = 1
            )

            OutlinedTextField(
                value = state.machineNumber, onValueChange = { machineNumber ->
                    viewModel.machineNumberChanged(machineNumber)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Machine Number") },
                singleLine = true,
                maxLines = 1
            )

            OutlinedTextField(
                value = state.workDescription, onValueChange = { workDescription ->
                    viewModel.workDescriptionChanged(workDescription)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Work Description") },
                singleLine = true,
                maxLines = 1
            )

            LazyColumn(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "IBAU Code",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Machine Type",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Machine Number",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.weight(0.5f))
                    }
                }

                item {
                    AnimatedVisibility(
                        visible = state.loading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        CircularProgressIndicator()
                    }
                }

                items(items = state.ibauCodes) { ibauCode ->
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
                                .clickable { viewModel.ibauCodeSelected(ibauCode) }
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
                                            text = ibauCode.code,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Text(
                                            text = ibauCode.machineType,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = ibauCode.machineNumber,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }



                                    Text(
                                        text = ibauCode.workDescription,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(5.dp)
                                    )
                                }


                                OutlinedIconButton(
                                    onClick = {
                                        viewModel.deleteIBAUCode(ibauCode)
                                    },
                                    modifier = Modifier
                                        .weight(0.15f)
                                        .padding(all = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete IBAU",
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






