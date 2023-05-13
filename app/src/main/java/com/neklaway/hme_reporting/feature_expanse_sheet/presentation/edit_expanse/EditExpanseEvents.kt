package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.edit_expanse

import android.net.Uri

sealed class EditExpanseEvents {
    class UserMessage(val message:String): EditExpanseEvents()
    class TakePicture(val uri: Uri): EditExpanseEvents()

    object PopBackStack: EditExpanseEvents()
    object PickPicture : EditExpanseEvents()
}
