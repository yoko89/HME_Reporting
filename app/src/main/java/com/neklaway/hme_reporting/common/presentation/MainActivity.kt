package com.neklaway.hme_reporting.common.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.neklaway.hme_reporting.common.presentation.common.component.ComposableScreenAnimation
import com.neklaway.hme_reporting.common.ui.theme.HMEReportingTheme
import com.neklaway.hme_reporting.feature_car_mileage.presentation.CarMileageScreen
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.main.ExpanseMainScreen
import com.neklaway.hme_reporting.feature_settings.presentation.SettingsScreen
import com.neklaway.hme_reporting.feature_settings.presentation.SettingsViewModel
import com.neklaway.hme_reporting.feature_time_sheet.presentation.main.TimeSheetMainScreen
import com.neklaway.hme_reporting.feature_time_sheet.presentation.main.TimeSheetMainViewModel
import com.neklaway.hme_reporting.feature_visa.presentation.VisaScreen
import com.neklaway.hme_reporting.feature_visa.presentation.VisaViewModel
import com.neklaway.hmereporting.BuildConfig
import com.neklaway.hmereporting.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity @Inject constructor(

) : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: MainActivityViewModel = hiltViewModel()
            Log.d(TAG, "onCreate: ${viewModel.darkThemeState} ${viewModel.themeState}")
            val themeState = viewModel.themeState.collectAsState()
            val darkThemeState = viewModel.darkThemeState.collectAsState()


            HMEReportingTheme(darkThemeState.value, themeState.value) {
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                val items =
                    listOf(
                        Screen.TimeSheetMain,
                        Screen.ExpanseMain,
                        Screen.Visa,
                        Screen.CarMileage,
                        Screen.Settings
                    )
                val selectedItem = remember {
                    mutableStateOf(items.find { it.route == navController.currentDestination?.route }
                        ?: items[0])
                }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            Icon(
                                painter = painterResource(id = R.drawable.hb_logo),
                                contentDescription = "HB Logo",
                                modifier = Modifier.padding(8.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            items.forEach { item ->
                                NavigationDrawerItem(
                                    icon = {
                                        item.imageVector?.let { image ->
                                            Icon(
                                                imageVector = image,
                                                contentDescription = item.name
                                            )
                                        }
                                        item.imageId?.let {
                                            Icon(
                                                imageVector = ImageVector.vectorResource(id = it),
                                                contentDescription = item.name,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    },
                                    label = { Text(item.name) },
                                    selected = item == selectedItem.value,
                                    onClick = {
                                        scope.launch { drawerState.close() }
                                        selectedItem.value = item
                                        navController.popBackStack()
                                        navController.navigate(item.route)
                                    },
                                    modifier = Modifier
                                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                                        .padding(vertical = 5.dp)
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(all = 5.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(text = BuildConfig.VERSION_NAME)
                            }
                        }
                    },
                    content = {
                        Navigation(navController = navController) {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                    }
                )
            }

        }
    }
}

@Composable
private fun Navigation(
    navController: NavHostController,
    showDrawer: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.TimeSheetMain.route
    ) {


        composable(route = Screen.TimeSheetMain.route) {
            val viewModel: TimeSheetMainViewModel = hiltViewModel()
            ComposableScreenAnimation {
                TimeSheetMainScreen(
                    viewModel.state.collectAsState().value,
                    viewModel::userEvent,
                    showDrawer
                )
            }
        }

        composable(route = Screen.ExpanseMain.route) {
            ComposableScreenAnimation {
                ExpanseMainScreen(showNavigationMenu = showDrawer)
            }
        }

        composable(route = Screen.Visa.route) {
            val viewModel:VisaViewModel = hiltViewModel()
            ComposableScreenAnimation {
                VisaScreen(viewModel.state.collectAsState().value,viewModel.userMessage,showDrawer,viewModel::userEvent)
            }
        }

        composable(route = Screen.Settings.route) {
            val viewModel:SettingsViewModel = hiltViewModel()
            ComposableScreenAnimation {
                SettingsScreen(viewModel.state.collectAsState().value,viewModel.userMessage,viewModel::userEvent,showDrawer)
            }
        }

        composable(route = Screen.CarMileage.route) {
            ComposableScreenAnimation {
                CarMileageScreen(showNavigationMenu = showDrawer)
            }
        }

    }
}
