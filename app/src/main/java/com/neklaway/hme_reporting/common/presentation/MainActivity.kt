package com.neklaway.hme_reporting.common.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.neklaway.hme_reporting.common.presentation.common.component.ComposableScreenAnimation
import com.neklaway.hme_reporting.common.ui.theme.HMEReportingTheme
import com.neklaway.hme_reporting.feature_car_mileage.presentation.CarMileageScreen
import com.neklaway.hme_reporting.feature_expanse_sheet.presentation.main.ExpanseMainScreen
import com.neklaway.hme_reporting.feature_settings.presentation.SettingsScreen
import com.neklaway.hme_reporting.feature_time_sheet.presentation.main.TimeSheetMainScreen
import com.neklaway.hme_reporting.feature_visa.presentation.VisaScreen
import com.neklaway.hmereporting.BuildConfig
import com.neklaway.hmereporting.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor(

) : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            HMEReportingTheme{
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
                        Navigation(navController = navController){
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
            ComposableScreenAnimation {
                TimeSheetMainScreen(showNavigationMenu = showDrawer)
            }
        }

        composable(route = Screen.ExpanseMain.route) {
            ComposableScreenAnimation {
                ExpanseMainScreen(showNavigationMenu = showDrawer)
            }
        }

        composable(route = Screen.Visa.route) {
            ComposableScreenAnimation {
                VisaScreen(showNavigationMenu = showDrawer)
            }
        }

        composable(route = Screen.Settings.route) {
            ComposableScreenAnimation {
                SettingsScreen(showNavigationMenu = showDrawer)
            }
        }

        composable(route = Screen.CarMileage.route) {
            ComposableScreenAnimation {
                CarMileageScreen(showNavigationMenu = showDrawer)
            }
        }

    }
}
