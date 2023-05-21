@file:OptIn(ExperimentalMaterial3Api::class)

package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.daily_allowance

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neklaway.hme_reporting.common.presentation.common.component.DropDown
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.daily_allowance.component.DailyAllowanceHeader
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.daily_allowance.component.DailyAllowanceItemCard
import kotlinx.coroutines.flow.Flow


@Composable
fun DailyAllowanceScreen(
    state: DailyAllowanceState,
    userMessage: Flow<String>,
    userEvent: (DailyAllowanceUserEvent) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(
        key1 = userMessage, key2 = state
    ) {
        userMessage.collect { event ->
            snackbarHostState.showSnackbar(event)
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                userEvent(DailyAllowanceUserEvent.AutoCalculate)
            }) {
                Icon(Icons.Default.AutoMode, contentDescription = "Auto calculate daily allowance")
            }
        }

    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = it)
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
                userEvent(DailyAllowanceUserEvent.CustomerSelected(customer))
            }

            DropDown(
                dropDownList = state.hmeCodes,
                selectedValue = state.selectedHMECode?.code ?: "No HME Code Selected",
                label = "HME Code",
                dropDownContentDescription = "Select HME Code",
                modifier = Modifier.padding(bottom = 5.dp)
            ) { hmeCode ->
                userEvent(DailyAllowanceUserEvent.HmeSelected(hmeCode))
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
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
                    DailyAllowanceHeader(
                        selectAll = state.selectAll,
                        onSelectAllChecked = { checked ->
                            userEvent(DailyAllowanceUserEvent.SelectAll(checked))
                        }
                    )
                }

                items(items = state.timeSheetList) { timeSheet ->
                    DailyAllowanceItemCard(
                        timeSheet = timeSheet,
                        dailyAllowanceChanged = { allowanceType ->
                            userEvent(
                                DailyAllowanceUserEvent.TimeSheetClicked(
                                    timeSheet,
                                    allowanceType
                                )
                            )
                        },
                        onCheckedChanged = { checked ->
                            userEvent(
                                DailyAllowanceUserEvent.ExpanseSelectedChanged(
                                    timeSheet,
                                    checked
                                )
                            )
                        }
                    )
                }
            }

        }
    }
}


