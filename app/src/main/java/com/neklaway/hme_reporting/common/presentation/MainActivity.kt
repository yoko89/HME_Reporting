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
import com.neklaway.hme_reporting.common.ui.theme.HMEReportingTheme
import com.neklaway.hme_reporting.feature_settings.presentation.settings.SettingsScreen
import com.neklaway.hme_reporting.feature_time_sheet.presentation.main.TimeSheetMainScreen
import com.neklaway.hme_reporting.feature_visa.presentation.VisaScreen
import com.neklaway.hmereporting.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor(

) : ComponentActivity() {


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            HMEReportingTheme {

                val navController = rememberNavController()

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                ) {


                    val drawerState = rememberDrawerState(DrawerValue.Closed)
                    val scope = rememberCoroutineScope()

                    val items =
                        listOf(Screen.TimeSheetMainScreen, Screen.VisaScreen, Screen.SettingsScreen)
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
                                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding).padding(vertical = 5.dp)
                                    )
                                }
                            }
                        },
                        content = {
                            Navigation(navController = navController)
                        }
                    )
                }

            }
        }
    }

}

@Composable
fun Navigation(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.TimeSheetMainScreen.route
    ) {


        composable(route = Screen.TimeSheetMainScreen.route) {
            TimeSheetMainScreen()
        }

        composable(route = Screen.VisaScreen.route) {
            VisaScreen()
        }

        composable(route = Screen.SettingsScreen.route) {
            SettingsScreen()
        }

    }
}
