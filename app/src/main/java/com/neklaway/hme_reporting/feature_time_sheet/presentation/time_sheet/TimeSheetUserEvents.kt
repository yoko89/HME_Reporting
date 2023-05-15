package com.neklaway.hme_reporting.feature_time_sheet.presentation.time_sheet

import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.model.IBAUCode
import com.neklaway.hme_reporting.common.domain.model.TimeSheet
import java.io.File

sealed class TimeSheetUserEvents {
    object Sign : TimeSheetUserEvents()
    object CreateTimeSheet : TimeSheetUserEvents()
    object OpenTimeSheets : TimeSheetUserEvents()
    object ShowMoreFABClicked : TimeSheetUserEvents()
    class CustomerSelected(val customer: Customer) : TimeSheetUserEvents()
    class HmeSelected(val hmeCode: HMECode) : TimeSheetUserEvents()
    class IbauSelected(val ibauCode: IBAUCode) : TimeSheetUserEvents()
    class SelectAll(val checked: Boolean) : TimeSheetUserEvents()
    class SheetSelectedChanged(val timeSheet: TimeSheet, val checked: Boolean) :
        TimeSheetUserEvents()

    class SignatureDone(val signerName: String?) : TimeSheetUserEvents()
    object SignatureCanceled : TimeSheetUserEvents()
    class TimesheetClicked(val timeSheet: TimeSheet):TimeSheetUserEvents()
    class FileSelected(val file: File):TimeSheetUserEvents()
    object FileSelectionCanceled:TimeSheetUserEvents()
}