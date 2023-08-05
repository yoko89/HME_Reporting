package com.neklaway.hme_reporting.feature_visa.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.neklaway.hme_reporting.common.presentation.common.component.CustomDatePicker
import com.neklaway.hme_reporting.feature_visa.presentation.component.VisaItemCard
import com.neklaway.hme_reporting.utils.NotificationPermissionRequest
import com.neklaway.hme_reporting.utils.toDate
import kotlinx.coroutines.flow.Flow
import java.util.Calendar


@Composable
fun VisaScreen(
    state: VisaState,
    userMessage: Flow<String>,
    showNavigationMenu: () -> Unit,
    userEvents: (VisaUserEvents) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

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
                is PressInteraction.Release -> userEvents(VisaUserEvents.DateClicked)
            }
        }
    }

    if (state.showDatePicker) {
        CustomDatePicker(year = state.date?.get(Calendar.YEAR),
            month = state.date?.get(Calendar.MONTH),
            day = state.date?.get(Calendar.DAY_OF_MONTH),
            dateSet = { year, month, day ->
                userEvents(VisaUserEvents.DatePicked(year, month, day))
            },
            canceled = {
                userEvents(VisaUserEvents.DatePickedCanceled)
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
                },
                scrollBehavior = scrollBehavior)
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
                            userEvents(VisaUserEvents.UpdateVisa)
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
                    userEvents(VisaUserEvents.SaveVisa)
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
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {

            OutlinedTextField(
                value = state.country,
                onValueChange = { country ->
                    userEvents(VisaUserEvents.CountryChanged(country))
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
                            userEvents(VisaUserEvents.VisaClicked(visa))
                        }, onDeleteClicked = {
                            userEvents(VisaUserEvents.DeleteVisa(visa))
                        }, onCheckedChanged = { checked ->
                            userEvents(VisaUserEvents.VisaSelected(visa, checked))
                        }, visaReminderWarning = state.warningDays
                        )
                    }


                }
            }
        }
    }
}