package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.new_expense

import android.net.Uri

sealed class NewExpenseUiEvents {
    class UserMessage(val message: String) : NewExpenseUiEvents()
    class TakePicture(val uri: Uri) : NewExpenseUiEvents()
    object PickPicture:NewExpenseUiEvents()
}
