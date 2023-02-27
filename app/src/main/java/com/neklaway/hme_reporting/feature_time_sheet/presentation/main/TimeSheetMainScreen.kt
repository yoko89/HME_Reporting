package com.neklaway.hme_reporting.feature_time_sheet.presentation.main

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.neklaway.hme_reporting.common.presentation.Screen
import com.neklaway.hme_reporting.common.presentation.common.component.BottomNavigationBar
import com.neklaway.hme_reporting.common.presentation.common.component.ComposableScreenAnimation
import com.neklaway.hme_reporting.common.ui.theme.HMEReportingTheme
import com.neklaway.hme_reporting.feature_time_sheet.presentation.customer.CustomerScreen
import com.neklaway.hme_reporting.feature_time_sheet.presentation.edit_time_sheet.EditTimeSheetScreen
import com.neklaway.hme_reporting.feature_time_sheet.presentation.edit_time_sheet.EditTimeSheetViewModel
import com.neklaway.hme_reporting.feature_time_sheet.presentation.hme_code.HMECodeScreen
import com.neklaway.hme_reporting.feature_time_sheet.presentation.ibau_code.IBAUCodeScreen
import com.neklaway.hme_reporting.feature_time_sheet.presentation.new_time_sheet.NewTimeSheetScreen
import com.neklaway.hme_reporting.feature_time_sheet.presentation.time_sheet.TimeSheetScreen

private const val TAG = "TimeSheetMainScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSheetMainScreen(
    viewModel: TimeSheetMainViewModel = hiltViewModel(),
            showNavigationMenu: () ->Unit,
) {
    val state by viewModel.state.collectAsState()
    val navController = rememberNavController()

    HMEReportingTheme {

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                     TopAppBar(title = { Text(text = "Time Sheet")},
                     navigationIcon = {
                         IconButton(onClick = showNavigationMenu) {
                             Icon(Icons.Default.Menu, contentDescription = "Menu")
                         }
                     })
            },
            bottomBar = {
                val screens = mutableListOf(
                    Screen.TimeSheet,
                    Screen.NewTimeSheet,
                    Screen.Customer,
                    Screen.HMECode
                )

                if (state.isIbau) screens.add(Screen.IBAUCode)

                Log.d(TAG, "TimeSheetMainScreen: Screens are $screens")

                BottomNavigationBar(
                    screenList = screens, navController = navController
                ) {
                    viewModel.screenSelected(it.route)
                    navController.popBackStack()
                    navController.navigate(it.route)
                }
            }
        ) { paddingValues ->
            Surface(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                AnimatedVisibility(visible = state.startupRoute == null) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(50.dp)
                                .align(Alignment.Center)
                        )
                    }
                }

                AnimatedVisibility(visible = state.startupRoute != null) {

                    Navigation(
                        navController = navController,
                        state.startupRoute!!
                    )
                }
            }

        }
    }
}


@Composable
private fun Navigation(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(route = Screen.TimeSheet.route) {
            ComposableScreenAnimation {
                TimeSheetScreen(navController = navController)
            }
        }

        composable(route = Screen.NewTimeSheet.route) {
            ComposableScreenAnimation {
                NewTimeSheetScreen()
            }
        }

        composable(route = Screen.EditTimeSheet.route + "?" + EditTimeSheetViewModel.TIME_SHEET_ID + "={" + EditTimeSheetViewModel.TIME_SHEET_ID + "}",
            arguments = listOf(
                navArgument(EditTimeSheetViewModel.TIME_SHEET_ID) {
                    type = NavType.LongType
                    defaultValue = -1
                }
            )) {
            ComposableScreenAnimation {
                EditTimeSheetScreen(navController)
            }
        }

        composable(route = Screen.Customer.route) {
            ComposableScreenAnimation {
                CustomerScreen()
            }
        }

        composable(route = Screen.HMECode.route) {
            ComposableScreenAnimation {
                HMECodeScreen()
            }
        }

        composable(route = Screen.IBAUCode.route) {
            ComposableScreenAnimation {
                IBAUCodeScreen()
            }
        }


    }
}

