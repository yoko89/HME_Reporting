@file:OptIn(ExperimentalMaterial3Api::class)

package com.neklaway.hme_reporting.feature_time_sheet.presentation.customer

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
import com.neklaway.hme_reporting.common.ui.theme.HMEReportingTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerScreen(
    viewModel: CustomerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val userMessage = viewModel.userMessage

    LaunchedEffect(key1 = userMessage) {
        userMessage.collect {
            snackbarHostState.showSnackbar(it)
        }
    }

    HMEReportingTheme {
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
                                viewModel.updateCustomer()
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
                        viewModel.saveCustomer()
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
                        viewModel.customerNameChange(name)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Customer Name") },
                    singleLine = true,
                    maxLines = 1
                )



                OutlinedTextField(
                    value = state.customerCity, onValueChange = { city ->
                        viewModel.customerCityChange(city)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Customer City") },
                    singleLine = true,
                    maxLines = 1
                )

                OutlinedTextField(
                    value = state.customerCountry, onValueChange = { country ->
                        viewModel.customerCountryChange(country)
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
                                    .clickable { viewModel.customerSelected(customer) }
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
                                            viewModel.deleteCustomer(customer)
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
}



