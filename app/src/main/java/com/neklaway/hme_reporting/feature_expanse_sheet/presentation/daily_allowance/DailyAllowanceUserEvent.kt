package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.daily_allowance

import com.neklaway.hme_reporting.common.data.entity.AllowanceType
import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.model.TimeSheet

sealed class DailyAllowanceUserEvent{
    object AutoCalculate:DailyAllowanceUserEvent()
    class CustomerSelected(val customer: Customer):DailyAllowanceUserEvent()
    class HmeSelected(val hmeCode: HMECode):DailyAllowanceUserEvent()
    class TimeSheetClicked(val timeSheet: TimeSheet,val allowanceType: AllowanceType):DailyAllowanceUserEvent()
    class ExpanseSelectedChanged(val timeSheet: TimeSheet,val checked:Boolean):DailyAllowanceUserEvent()
    class SelectAll(val checked:Boolean):DailyAllowanceUserEvent()
}
