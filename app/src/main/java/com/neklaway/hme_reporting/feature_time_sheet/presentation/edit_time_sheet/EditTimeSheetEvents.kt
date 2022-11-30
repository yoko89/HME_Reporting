package com.neklaway.hme_reporting.feature_time_sheet.presentation.edit_time_sheet

sealed class EditTimeSheetEvents(){
    class UserMessage(val message:String): EditTimeSheetEvents()
    object PopBackStack: EditTimeSheetEvents()
}
