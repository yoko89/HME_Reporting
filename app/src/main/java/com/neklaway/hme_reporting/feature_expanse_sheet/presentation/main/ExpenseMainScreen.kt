package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.main

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.neklaway.hme_reporting.common.presentation.Screen
import com.neklaway.hme_reporting.common.presentation.common.component.ComposableScreenAnimation
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.currency_exchange_rate.CurrencyExchangeScreen
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.currency_exchange_rate.CurrencyExchangeViewModel
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.daily_allowance.DailyAllowanceScreen
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.daily_allowance.DailyAllowanceViewModel
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.edit_expanse.EditExpanseScreen
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.edit_expanse.EditExpanseViewModel
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.expanse_sheet.ExpanseSheetViewModel
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.expanse_sheet.ExpenseSheetScreen
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.new_expense.NewExpanseScreen
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.new_expense.NewExpenseViewModel

private const val TAG = "ExpanseMainScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseMainScreen(
    state: ExpenseSheetState,
    userEvents: (ExpenseSheetMainUserEvent) -> Unit,
    showNavigationMenu: () -> Unit,
) {
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(text = "Expense Sheet") },
                    navigationIcon = {
                        IconButton(onClick = showNavigationMenu) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
        ) { paddingValues ->
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                val screens = mutableListOf(
                    Screen.ExpenseSheet,
                    Screen.DailyAllowance,
                    Screen.NewExpense,
                    Screen.CurrencyExchange
                )

                Log.d(TAG, "ExpanseMainScreen: Screens are $screens")

                screens.forEach { screen ->
                    val selected =
                        screen.route == backStackEntry.value?.destination?.route?.split("?")?.get(0)


                    item(
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                userEvents(ExpenseSheetMainUserEvent.ScreenSelected(screen.route))

                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
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
            Surface(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
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

        composable(route = Screen.ExpenseSheet.route) {
            val viewModel: ExpanseSheetViewModel = hiltViewModel()
            ComposableScreenAnimation {
                ExpenseSheetScreen(
                    navController,
                    viewModel.state.collectAsState().value,
                    viewModel.uiEvent,
                    viewModel::userEvent,
                    viewModel::getCurrencyExchangeName
                )
            }
        }

        composable(
            route =
            Screen.NewExpense.route
        ) {
            val viewModel: NewExpenseViewModel = hiltViewModel()
            ComposableScreenAnimation {
                NewExpanseScreen(
                    viewModel.state.collectAsState().value,
                    viewModel.uiEvent,
                    viewModel::userEvent
                )
            }
        }

        composable(route = Screen.DailyAllowance.route) {
            val viewModel: DailyAllowanceViewModel = hiltViewModel()
            ComposableScreenAnimation {
                DailyAllowanceScreen(
                    viewModel.state.collectAsState().value,
                    viewModel.userMessage,
                    viewModel::userEvent
                )
            }
        }
        composable(route = Screen.CurrencyExchange.route) {
            val viewModel: CurrencyExchangeViewModel = hiltViewModel()
            ComposableScreenAnimation {
                CurrencyExchangeScreen(
                    viewModel.state.collectAsState().value,
                    viewModel.userMessage,
                    viewModel::userEvent
                )
            }
        }

        composable(route = Screen.EditExpense.route + "?" + EditExpanseViewModel.EXPANSE_ID + "={" + EditExpanseViewModel.EXPANSE_ID + "}",
            arguments = listOf(
                navArgument(EditExpanseViewModel.EXPANSE_ID) {
                    type = NavType.LongType
                    defaultValue = -1
                }
            )) {
            val viewModel: EditExpanseViewModel = hiltViewModel()
            ComposableScreenAnimation {
                EditExpanseScreen(
                    navController,
                    viewModel.state.collectAsState().value,
                    viewModel.uiEvent,
                    viewModel::userEvent
                )
            }
        }
    }
}

