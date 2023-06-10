package com.neklaway.hme_reporting.feature_time_sheet.presentation.customer

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
import kotlinx.coroutines.flow.Flow


@Composable
fun CustomerScreen(
    state: CustomerState,
    userMessage: Flow<String>,
    userEvent: (CustomerUserEvents) -> Unit,
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
                    state.selectedCustomer != null,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    Row {
                        FloatingActionButton(onClick = {
                            userEvent(CustomerUserEvents.UpdateCustomer)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Customer"
                            )
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                    }
                }

                FloatingActionButton(onClick = {
                    userEvent(CustomerUserEvents.SaveCustomer)
                }) {

                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Customer")
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

            OutlinedTextField(
                value = state.customerName,
                onValueChange = { name ->
                    userEvent(CustomerUserEvents.CustomerNameChange(name))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Customer Name") },
                singleLine = true,
                maxLines = 1
            )



            OutlinedTextField(
                value = state.customerCity, onValueChange = { city ->
                    userEvent(CustomerUserEvents.CustomerCityChange(city))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Customer City") },
                singleLine = true,
                maxLines = 1
            )

            OutlinedTextField(
                value = state.customerCountry, onValueChange = { country ->
                    userEvent(CustomerUserEvents.CustomerCountryChange(country))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Customer Country") },
                singleLine = true,
                maxLines = 1
            )

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
                            text = "Name",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "City",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Country",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.weight(0.5f))
                    }
                }

                items(items = state.customers) { customer ->
                    val visibility = remember {
                        mutableStateOf(false)
                    }

                    var deleteDialogVisible by remember {
                        mutableStateOf(false)
                    }

                    AnimatedVisibility(visible = deleteDialogVisible) {
                        DeleteDialog(item = customer,
                            onConfirm = {
                                userEvent(CustomerUserEvents.DeleteCustomer(customer))
                                deleteDialogVisible = false
                            },
                            onDismiss = { deleteDialogVisible = false }
                        )
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
                                .padding(vertical = 2.dp)
                                .clickable { userEvent(CustomerUserEvents.CustomerSelected(customer)) }
                        ) {

                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = customer.name,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = customer.city,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = customer.country,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f)
                                )



                                OutlinedIconButton(
                                    onClick = {
                                        deleteDialogVisible = true
                                    },
                                    modifier = Modifier.weight(0.5f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete customer",
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



