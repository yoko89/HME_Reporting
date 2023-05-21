package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.expanse_sheet

import com.neklaway.hme_reporting.common.data.entity.Accommodation
import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.Expanse
import java.io.File

sealed class ExpanseSheetUserEvent {
    object CreateExpanseSheet : ExpanseSheetUserEvent()
    object OpenExpanseSheets : ExpanseSheetUserEvent()
    object ShowMoreFABClicked : ExpanseSheetUserEvent()
    class CustomerSelected(val customer: Customer) : ExpanseSheetUserEvent()
    class HmeSelected(val hmeCode: HMECode) : ExpanseSheetUserEvent()
    class AccommodationChanged(val accommodation: Accommodation) : ExpanseSheetUserEvent()
    class ExpanseClicked(val expanse:Expanse) : ExpanseSheetUserEvent()
    class FileSelected(val file: File) : ExpanseSheetUserEvent()
    object FileSelectionCanceled:ExpanseSheetUserEvent()
}
