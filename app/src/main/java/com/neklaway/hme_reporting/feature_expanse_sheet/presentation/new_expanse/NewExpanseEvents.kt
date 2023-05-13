package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.new_expanse

import android.net.Uri

sealed class NewExpanseEvents {
    class UserMessage(val message: String) : NewExpanseEvents()
    class TakePicture(val uri: Uri) : NewExpanseEvents()
    object PickPicture:NewExpanseEvents()
}
