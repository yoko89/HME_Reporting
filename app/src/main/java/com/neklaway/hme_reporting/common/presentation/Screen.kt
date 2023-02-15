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
    object Settings : Screen("Settings", "settings_screen", Icons.Default.Settings)
    object TimeSheetMain : Screen("Timesheet", "time_sheet_main_screen", Icons.Default.Note)
    object Visa : Screen("Visa","visa_screen",Icons.Default.AirplaneTicket)
    object CarMileage : Screen("Car","car_mileage",Icons.Default.DirectionsCar)
    object TimeSheet : Screen("Time Sheet", "time_sheet_screen", Icons.Default.Note)
    object Customer : Screen("Customer", "customer_screen", Icons.Default.Person)
    object HMECode : Screen("HME Code", "hme_code_screen", imageId = R.drawable.hb_logo)
    object IBAUCode : Screen("IBAU Code", "ibau_code_screen", imageId = R.drawable.ibau_logo)
    object NewTimeSheet : Screen("New", "new_time_sheet_screen", Icons.Default.NoteAdd)
    object EditTimeSheet : Screen("Edit", "edit_time_sheet_screen", Icons.Default.EditNote)
    object ExpanseSheet : Screen("Expanse Sheet", "expanse_sheet_screen", Icons.Default.Payment)
    object NewExpanse : Screen("New Expanse","new_expanse_screen", Icons.Default.Receipt)
    object DailyAllowance : Screen("Daily Allowance","daily_allowance_screen", Icons.Default.AttachMoney)
    object EditExpanse : Screen("Edit Expanse","edit_expanse_screen", Icons.Default.Receipt)

}


