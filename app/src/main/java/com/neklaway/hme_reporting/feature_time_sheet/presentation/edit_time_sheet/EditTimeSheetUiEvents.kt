package com.neklaway.hme_reporting.feature_time_sheet.presentation.edit_time_sheet

sealed class EditTimeSheetUiEvents {
    class UserMessage(val message:String): EditTimeSheetUiEvents()
    object PopBackStack: EditTimeSheetUiEvents()
}
