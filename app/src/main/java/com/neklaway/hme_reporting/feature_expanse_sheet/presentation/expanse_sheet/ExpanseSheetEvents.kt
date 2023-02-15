package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.expanse_sheet

import java.io.File

sealed class ExpanseSheetEvents{
    class UserMessage(val message:String): ExpanseSheetEvents()
    class NavigateToExpanseSheet(val id:Long): ExpanseSheetEvents()
    class ShowFile(val file: File): ExpanseSheetEvents()
}
