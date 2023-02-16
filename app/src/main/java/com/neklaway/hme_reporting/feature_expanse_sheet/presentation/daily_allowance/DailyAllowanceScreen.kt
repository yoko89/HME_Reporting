@file:OptIn(ExperimentalMaterial3Api::class)

package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.daily_allowance

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.neklaway.hme_reporting.common.presentation.common.component.DropDown
import com.neklaway.hme_reporting.common.ui.theme.HMEReportingTheme
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.daily_allowance.component.DailyAllowanceHeader
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.daily_allowance.component.DailyAllowanceItemCard


@Composable
fun DailyAllowanceScreen(
    viewModel: DailyAllowanceViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val events = viewModel.event

    LaunchedEffect(
        key1 = events, key2 = state
    ) {
        events.collect { event ->
            snackbarHostState.showSnackbar(event)
        }
    }

    HMEReportingTheme {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
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
                    modifier = Modifier.padding(vertical = 5.dp),
                    dropDownList = state.customers,
                    selectedValue = state.selectedCustomer?.name ?: "No Customer Selected",
                    label = "Customer",
                    dropDownContentDescription = "Select Customer",
                    onSelect = { customer ->
                        viewModel.customerSelected(customer)
                    }
                )

                DropDown(
                    modifier = Modifier.padding(bottom = 5.dp),
                    dropDownList = state.hmeCodes,
                    selectedValue = state.selectedHMECode?.code ?: "No HME Code Selected",
                    label = "HME Code",
                    dropDownContentDescription = "Select HME Code",
                    onSelect = { hmeCode ->
                        viewModel.hmeSelected(hmeCode)
                    }
                )

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
                        DailyAllowanceHeader()
                    }

                    items(items = state.timeSheetList) { timeSheet ->
                        DailyAllowanceItemCard(
                            timeSheet = timeSheet,
                            dailyAllowanceChanged = { allowanceType ->
                                viewModel.timeSheetClicked(timeSheet, allowanceType)
                            },
                        )
                    }
                }

            }
        }
    }
}



