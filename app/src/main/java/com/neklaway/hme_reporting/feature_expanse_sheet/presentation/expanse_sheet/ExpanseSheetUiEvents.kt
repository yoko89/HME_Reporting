package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.expanse_sheet

import java.io.File

sealed class ExpanseSheetUiEvents{
    class UserMessage(val message:String): ExpanseSheetUiEvents()
    class NavigateToExpanseSheetUi(val id:Long): ExpanseSheetUiEvents()
    class ShowFile(val file: File): ExpanseSheetUiEvents()
}
