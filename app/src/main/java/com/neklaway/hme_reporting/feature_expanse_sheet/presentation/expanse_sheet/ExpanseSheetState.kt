package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.expanse_sheet

import com.neklaway.hme_reporting.common.data.entity.Accommodation
import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.model.TimeSheet
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.Expense

data class ExpanseSheetState(
    val isLoading: Boolean = false,
    val customers: List<Customer> = emptyList(),
    val selectedCustomer: Customer? = null,
    val hmeCodes: List<HMECode> = emptyList(),
    val selectedHMECode: HMECode? = null,
    val expenseList: List<Expense> = emptyList(),
    val timeSheetList: List<TimeSheet> = emptyList(),
    val loading: Boolean = false,
    val navigateToExpanseId: Long? = null,
    val selectAll: Boolean = true,
    val fabVisible: Boolean = false,
    val showFileList: Boolean = false,
    val lessThan24hDays: Int = 0,
    val fullDays: Int = 0,
    val missingDailyAllowance: Boolean = false,
    val noAllowanceDays: Int = 0,
    val totalPaidAmount: Float = 0f,
    val accommodation: Accommodation? = null,
)

