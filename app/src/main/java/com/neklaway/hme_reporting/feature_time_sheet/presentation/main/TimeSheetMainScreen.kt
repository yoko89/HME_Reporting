package com.neklaway.hme_reporting.feature_time_sheet.presentation.main

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.neklaway.hme_reporting.common.presentation.Screen
import com.neklaway.hme_reporting.common.presentation.common.component.ComposableScreenAnimation
import com.neklaway.hme_reporting.feature_time_sheet.presentation.customer.CustomerScreen
import com.neklaway.hme_reporting.feature_time_sheet.presentation.customer.CustomerViewModel
import com.neklaway.hme_reporting.feature_time_sheet.presentation.edit_time_sheet.EditTimeSheetScreen
import com.neklaway.hme_reporting.feature_time_sheet.presentation.edit_time_sheet.EditTimeSheetViewModel
import com.neklaway.hme_reporting.feature_time_sheet.presentation.hme_code.HMECodeScreen
import com.neklaway.hme_reporting.feature_time_sheet.presentation.hme_code.HMECodeViewModel
import com.neklaway.hme_reporting.feature_time_sheet.presentation.ibau_code.IBAUCodeScreen
import com.neklaway.hme_reporting.feature_time_sheet.presentation.ibau_code.IBAUCodeViewModel
import com.neklaway.hme_reporting.feature_time_sheet.presentation.new_time_sheet.NewTimeSheetScreen
import com.neklaway.hme_reporting.feature_time_sheet.presentation.new_time_sheet.NewTimeSheetViewModel
import com.neklaway.hme_reporting.feature_time_sheet.presentation.time_sheet.TimeSheetScreen
import com.neklaway.hme_reporting.feature_time_sheet.presentation.time_sheet.TimeSheetViewModel

private const val TAG = "TimeSheetMainScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSheetMainScreen(
    state: TimeSheetState,
    userEvents: (TimeSheetMainUserEvent) -> Unit,
    showNavigationMenu: () -> Unit,
) {
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    NavigationSuiteScaffold(
        navigationSuiteItems = {

            val screens = mutableListOf(
                Screen.TimeSheet,
                Screen.NewTimeSheet,
                Screen.Customer,
                Screen.HMECode
            )

            if (state.isIbau) screens.add(Screen.IBAUCode)

            Log.d(TAG, "TimeSheetMainScreen: Screens are $screens")


            screens.forEach { screen ->
                val selected =
                    screen.route == backStackEntry.value?.destination?.route?.split("?")?.get(0)

                item(
                    selected = selected,
                    onClick = {

                        if (!selected) {
                            userEvents(TimeSheetMainUserEvent.ScreenSelected(screen.route))
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        screen.imageVector?.let { image ->
                            Icon(imageVector = image, contentDescription = screen.name)
                        }
                        screen.imageId?.let {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = it),
                                contentDescription = screen.name,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    label = {
                        Text(text = screen.name, textAlign = TextAlign.Center)
                    },
                    alwaysShowLabel = false,
                )
            }
        }
    ) {

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(text = "Time Sheet") },
                    navigationIcon = {
                        IconButton(onClick = showNavigationMenu) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
        ) { paddingValues ->
            Surface(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
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
            val viewModel: TimeSheetViewModel = hiltViewModel()
            ComposableScreenAnimation {
                TimeSheetScreen(
                    navController = navController,
                    state = viewModel.state.collectAsState().value,
                    uiEvents = viewModel.uiEvent,
                    userEvents = viewModel::userEvents
                )
            }
        }

        composable(route = Screen.NewTimeSheet.route) {
            val viewModel: NewTimeSheetViewModel = hiltViewModel()
            ComposableScreenAnimation {
                NewTimeSheetScreen(
                    viewModel.state.collectAsState().value,
                    viewModel.userMessage,
                    viewModel::userEvents
                )
            }
        }

        composable(route = Screen.EditTimeSheet.route + "?" + EditTimeSheetViewModel.TIME_SHEET_ID + "={" + EditTimeSheetViewModel.TIME_SHEET_ID + "}",
            arguments = listOf(
                navArgument(EditTimeSheetViewModel.TIME_SHEET_ID) {
                    type = NavType.LongType
                    defaultValue = -1
                }
            )) {
            val viewModel: EditTimeSheetViewModel = hiltViewModel()
            ComposableScreenAnimation {
                EditTimeSheetScreen(
                    navController,
                    viewModel.state.collectAsState().value,
                    viewModel.uiEvent,
                    viewModel::userEvents
                )
            }
        }

        composable(route = Screen.Customer.route) {
            val viewModel: CustomerViewModel = hiltViewModel()
            ComposableScreenAnimation {
                CustomerScreen(
                    viewModel.state.collectAsState().value,
                    viewModel.userMessage,
                    viewModel::userEvent
                )
            }
        }

        composable(route = Screen.HMECode.route) {
            val viewModel: HMECodeViewModel = hiltViewModel()
            ComposableScreenAnimation {
                HMECodeScreen(
                    viewModel.state.collectAsState().value,
                    viewModel.userMessage,
                    viewModel::userEvent
                )
            }
        }

        composable(route = Screen.IBAUCode.route) {
            val viewModel: IBAUCodeViewModel = hiltViewModel()
            ComposableScreenAnimation {
                IBAUCodeScreen(
                    viewModel.state.collectAsState().value,
                    viewModel::userEvent,
                    viewModel.userMessage
                )
            }
        }
    }
}

