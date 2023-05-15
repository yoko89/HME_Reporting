package com.neklaway.hme_reporting.feature_time_sheet.presentation.time_sheet

import java.io.File

sealed class TimeSheetUiEvents{
    class UserMessage(val message:String): TimeSheetUiEvents()
    class NavigateToTimeSheetUi(val id:Long): TimeSheetUiEvents()
    class ShowFile(val file: File): TimeSheetUiEvents()
}
