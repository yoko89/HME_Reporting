package com.neklaway.hme_reporting.common.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.neklaway.hmereporting.R

sealed class Screen(
    val name: String,
    val route: String,
    val imageVector: ImageVector? = null,
    val imageId: Int? = null
) {
    object SettingsScreen : Screen("Settings", "settings_screen", Icons.Default.Settings)
    object TimeSheetMainScreen : Screen("Timesheet", "time_sheet_main_screen", Icons.Default.Note)
    object VisaScreen : Screen("Visa","visa_screen",Icons.Default.AirplaneTicket)
    object CarMileageScreen : Screen("Car","car_mileage",Icons.Default.DirectionsCar)
    object TimeSheetScreen : Screen("Time Sheet", "time_sheet_screen", Icons.Default.Note)
    object CustomerScreen : Screen("Customer", "customer_screen", Icons.Default.Person)
    object HMECodeScreen : Screen("HME Code", "hme_code_screen", imageId = R.drawable.hb_logo)
    object IBAUCodeScreen : Screen("IBAU Code", "ibau_code_screen", imageId = R.drawable.ibau_logo)
    object NewTimeSheetScreen : Screen("New", "new_time_sheet_screen", Icons.Default.NoteAdd)
    object EditTimeSheetScreen : Screen("Edit", "edit_time_sheet_screen", Icons.Default.EditNote)

}


