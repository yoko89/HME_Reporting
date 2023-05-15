package com.neklaway.hme_reporting.feature_time_sheet.presentation.hme_code

import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode

sealed class HMECodeUserEvents {
    object UpdateHMECode : HMECodeUserEvents()
    object SaveHMECode : HMECodeUserEvents()
    class CustomerSelected(val customer: Customer) : HMECodeUserEvents()
    class HmeCodeSelected(val hmeCode: HMECode) : HMECodeUserEvents()
    class HmeCodeChanged(val hmeCode: String) : HMECodeUserEvents()
    class MachineTypeChanged(val machineType: String) : HMECodeUserEvents()
    class MachineNumberChanged(val machineNumber: String) : HMECodeUserEvents()
    class WorkDescriptionChanged(val description: String) : HMECodeUserEvents()
    class DeleteHMECode(val hmeCode: HMECode) : HMECodeUserEvents()
}
