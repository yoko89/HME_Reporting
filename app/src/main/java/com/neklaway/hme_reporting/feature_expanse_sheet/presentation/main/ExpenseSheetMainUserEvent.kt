package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.main

sealed class ExpenseSheetMainUserEvent{
    class ScreenSelected(val route:String):ExpenseSheetMainUserEvent()
}
