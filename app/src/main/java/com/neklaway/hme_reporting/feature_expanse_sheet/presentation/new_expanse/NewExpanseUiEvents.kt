package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.new_expanse

import android.net.Uri

sealed class NewExpanseUiEvents {
    class UserMessage(val message: String) : NewExpanseUiEvents()
    class TakePicture(val uri: Uri) : NewExpanseUiEvents()
    object PickPicture:NewExpanseUiEvents()
}
