package com.neklaway.hme_reporting.common.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AirplaneTicket
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.automirrored.filled.NoteAdd
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
    object TimeSheetMain : Screen("Timesheet", "time_sheet_main_screen", Icons.AutoMirrored.Filled.Note)
    object Visa : Screen("Visa", "visa_screen", Icons.AutoMirrored.Filled.AirplaneTicket)
    object CarMileage : Screen("Car", "car_mileage", Icons.Default.DirectionsCar)
    object TimeSheet : Screen("Time Sheet", "time_sheet_screen", Icons.AutoMirrored.Filled.Note)
    object Customer : Screen("Customer", "customer_screen", Icons.Default.Person)
    object HMECode : Screen("HME Code", "hme_code_screen", imageId = R.drawable.hb_logo)
    object IBAUCode : Screen("IBAU Code", "ibau_code_screen", imageId = R.drawable.ibau_logo)
    object NewTimeSheet : Screen("New", "new_time_sheet_screen", Icons.AutoMirrored.Filled.NoteAdd)
    object EditTimeSheet : Screen("Edit", "edit_time_sheet_screen", Icons.Default.EditNote)
    object ExpenseMain : Screen("Expense","expanse_main",Icons.Default.Payment)
    object ExpenseSheet : Screen("Expense Sheet", "expanse_sheet_screen", Icons.Default.Payment)
    object NewExpense : Screen("New Expense", "new_expanse_screen", Icons.Default.Receipt)
    object DailyAllowance :
        Screen("Daily Allowance", "daily_allowance_screen", Icons.Default.AttachMoney)

    object EditExpense : Screen("Edit Expense", "edit_expanse_screen", Icons.Default.Receipt)
    object CurrencyExchange : Screen(
        "Currency Exchange Rate",
        "currency_exchange_rate_screen",
        Icons.Default.CurrencyExchange
    )

}


