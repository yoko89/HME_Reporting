package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.daily_allowance

import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.model.TimeSheet
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.Expanse

data class DailyAllowanceState(
    val customers: List<Customer> = emptyList(),
    val selectedCustomer: Customer? = null,
    val hmeCodes: List<HMECode> = emptyList(),
    val selectedHMECode: HMECode? = null,
    val timeSheetList: List<TimeSheet> = emptyList(),
    val loading: Boolean = false,
)

