package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.main

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.neklaway.hme_reporting.common.presentation.Screen
import com.neklaway.hme_reporting.common.presentation.common.component.BottomNavigationBar
import com.neklaway.hme_reporting.common.ui.theme.HMEReportingTheme
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.currency_exchange_rate.CurrencyExchangeScreen
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.daily_allowance.DailyAllowanceScreen
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.edit_expanse.EditExpanseScreen
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.edit_expanse.EditExpanseViewModel
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.expanse_sheet.ExpanseSheetScreen
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.new_expanse.NewExpanseScreen

private const val TAG = "ExpanseMainScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpanseMainScreen() {
    val navController = rememberNavController()

    HMEReportingTheme {

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                val screens = mutableListOf(
                    Screen.ExpanseSheet,
                    Screen.DailyAllowance,
                    Screen.NewExpanse,
                    Screen.CurrencyExchange
                )

                Log.d(TAG, "ExpanseMainScreen: Screens are $screens")

                BottomNavigationBar(
                    screenList = screens, navController = navController
                ) {
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
                Navigation(navController = navController, Screen.ExpanseSheet.route)
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

        composable(route = Screen.ExpanseSheet.route) {
            ExpanseSheetScreen(navController)
        }

        composable(
            route =
            Screen.NewExpanse.route
        ) {
            NewExpanseScreen()
        }

        composable(route = Screen.DailyAllowance.route) {
            DailyAllowanceScreen()
        }
        composable(route = Screen.CurrencyExchange.route) {
            CurrencyExchangeScreen()
        }

        composable(route = Screen.EditExpanse.route + "?" + EditExpanseViewModel.EXPANSE_ID + "={" + EditExpanseViewModel.EXPANSE_ID + "}",
            arguments = listOf(
                navArgument(EditExpanseViewModel.EXPANSE_ID) {
                    type = NavType.LongType
                    defaultValue = -1
                }
            )) {
            EditExpanseScreen(navController)
        }
    }
}

