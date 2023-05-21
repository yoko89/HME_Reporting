package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.edit_expanse

import android.net.Uri

sealed class EditExpanseUiEvents {
    class UserMessage(val message:String): EditExpanseUiEvents()
    class TakePicture(val uri: Uri): EditExpanseUiEvents()

    object PopBackStack: EditExpanseUiEvents()
    object PickPicture : EditExpanseUiEvents()
}
