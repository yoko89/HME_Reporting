package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.main

sealed class ExpanseSheetMainUserEvent{
    class ScreenSelected(val route:String):ExpanseSheetMainUserEvent()
}
