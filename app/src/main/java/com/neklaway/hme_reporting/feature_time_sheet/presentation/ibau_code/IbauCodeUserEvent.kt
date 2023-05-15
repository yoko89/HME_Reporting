package com.neklaway.hme_reporting.feature_time_sheet.presentation.ibau_code

import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.model.IBAUCode

sealed class IbauCodeUserEvent{
    object UpdateIBAUCode:IbauCodeUserEvent()
    object SaveIBAUCode:IbauCodeUserEvent()
    class CustomerSelected(val customer: Customer):IbauCodeUserEvent()
    class HmeCodeSelected(val hmeCode: HMECode):IbauCodeUserEvent()
    class IbauCodeChanged(val ibauCode: String):IbauCodeUserEvent()
    class MachineTypeChanged(val machineType:String):IbauCodeUserEvent()
    class MachineNumberChanged(val machineNumber:String):IbauCodeUserEvent()
    class WorkDescriptionChanged(val description:String):IbauCodeUserEvent()
    class IbauCodeSelected(val ibauCode: IBAUCode):IbauCodeUserEvent()
    class DeleteIBAUCode(val ibauCode: IBAUCode):IbauCodeUserEvent()
}
