package com.neklaway.hme_reporting.feature_time_sheet.presentation.time_sheet

import java.io.File

sealed class TimeSheetEvents{
    class UserMessage(val message:String): TimeSheetEvents()
    class NavigateToTimeSheet(val id:Long): TimeSheetEvents()
    class ShowFile(val file: File): TimeSheetEvents()
}
