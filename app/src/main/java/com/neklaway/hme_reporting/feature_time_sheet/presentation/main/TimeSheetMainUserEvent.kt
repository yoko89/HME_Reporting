package com.neklaway.hme_reporting.feature_time_sheet.presentation.main

sealed class TimeSheetMainUserEvent{
    class ScreenSelected(val route:String):TimeSheetMainUserEvent()
}
