package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.new_expanse

import android.net.Uri
import com.neklaway.hme_reporting.feature_time_sheet.presentation.time_sheet.TimeSheetEvents

sealed class NewExpanseEvents{
    class UserMessage(val message:String): NewExpanseEvents()
    class TakePicture(val uri: Uri):NewExpanseEvents()
}
