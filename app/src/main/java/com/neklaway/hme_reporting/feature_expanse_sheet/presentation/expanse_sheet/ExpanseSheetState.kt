package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.expanse_sheet

import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.model.IBAUCode
import com.neklaway.hme_reporting.common.domain.model.TimeSheet
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.Expanse

data class ExpanseSheetState(
    val isLoading: Boolean = false,
    val customers: List<Customer> = emptyList(),
    val selectedCustomer: Customer? = null,
    val hmeCodes: List<HMECode> = emptyList(),
    val selectedHMECode: HMECode? = null,
    val expanseList: List<Expanse> = emptyList(),
    val timeSheetList:List<TimeSheet> = emptyList(),
    val loading: Boolean = false,
    val navigateToExpanseId: Long? = null,
    val selectAll: Boolean = true,
    val fabVisible: Boolean = false,
    val showFileList: Boolean = false,
)

