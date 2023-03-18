@file:OptIn(ExperimentalMaterial3Api::class)

package com.neklaway.hme_reporting.feature_visa.presentation

import androidx.compose.animation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.neklaway.hme_reporting.common.presentation.common.component.CustomDatePicker
import com.neklaway.hme_reporting.feature_visa.presentation.component.VisaItemCard
import com.neklaway.hme_reporting.utils.NotificationPermissionRequest
import com.neklaway.hme_reporting.utils.toDate
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisaScreen(
    showNavigationMenu: () -> Unit,
    viewModel: VisaViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val userMessage = viewModel.userMessage

    val context = LocalContext.current

    val dateInteractionSource = remember { MutableInteractionSource() }

    LaunchedEffect(key1 = userMessage) {
        userMessage.collect {
            snackbarHostState.showSnackbar(it)
        }
    }

    LaunchedEffect(key1 = dateInteractionSource) {
        dateInteractionSource.interactions.collect {
            when (it) {
                is PressInteraction.Release -> viewModel.dateClicked()
            }
        }
    }

    if (state.showDatePicker) {
        CustomDatePicker(year = state.date?.get(Calendar.YEAR),
            month = state.date?.get(Calendar.MONTH),
            day = state.date?.get(Calendar.DAY_OF_MONTH),
            dateSet = { year, month, day ->
                viewModel.datePicked(year, month, day)
            },
            canceled = {
                viewModel.datePickedCanceled()
            })
    }

    //Notification permission check

    NotificationPermissionRequest(context = context)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Visa") },
                navigationIcon = {
                    IconButton(onClick = showNavigationMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                })
        },
        floatingActionButton = {
            Row {
                AnimatedVisibility(
                    state.selectedVisa != null,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    Row {
                        FloatingActionButton(onClick = {
                            viewModel.updateVisa()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Visa"
                            )
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                    }
                }
                FloatingActionButton(onClick = {
                    viewModel.saveVisa()
                }) {

                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Visa")
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
                value = state.country,
                onValueChange = { country ->
                    viewModel.countryChanged(country)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Country") },
                singleLine = true,
                maxLines = 1
            )

            OutlinedTextField(
                value = state.date.toDate(),
                onValueChange = {},
                label = { Text(text = "Date") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                interactionSource = dateInteractionSource
            )



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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(0.5f))

                        Text(
                            text = "Country",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "Date",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.weight(0.5f))
                    }
                }

                items(items = state.visas) { visa ->
                    var visibility by remember {
                        mutableStateOf(false)
                    }

                    LaunchedEffect(key1 = Unit) {
                        visibility = true
                    }
                    AnimatedVisibility(visible = visibility) {

                        VisaItemCard(visa = visa, cardClicked = {
                            viewModel.visaClicked(visa)
                        }, onDeleteClicked = {
                            viewModel.deleteVisa(visa)
                        }, onCheckedChanged = { checked ->
                            viewModel.visaSelected(visa, checked)
                        }, visaReminderWarning = state.warningDays
                        )
                    }


                }
            }
        }
    }
}