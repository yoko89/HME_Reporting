package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.edit_expanse

sealed class EditExpanseEvents {
    class UserMessage(val message:String): EditExpanseEvents()
    object PopBackStack: EditExpanseEvents()
}
