package com.neklaway.hme_reporting.feature_time_sheet.presentation.time_sheet

import com.neklaway.hme_reporting.feature_time_sheet.domain.model.Customer
import com.neklaway.hme_reporting.feature_time_sheet.domain.model.HMECode
import com.neklaway.hme_reporting.feature_time_sheet.domain.model.IBAUCode
import com.neklaway.hme_reporting.feature_time_sheet.domain.model.TimeSheet

data class TimeSheetState(
    val isLoading: Boolean = false,
    val customers: List<Customer> = emptyList(),
    val selectedCustomer: Customer? = null,
    val hmeCodes: List<HMECode> = emptyList(),
    val selectedHMECode: HMECode? = null,
    val ibauCodes: List<IBAUCode> = emptyList(),
    val selectedIBAUCode: IBAUCode? = null,
    val isIbau: Boolean = false,
    val timeSheets: List<TimeSheet> = emptyList(),
    val loading: Boolean = false,
    val navigateToTimeSheetId: Long? = null,
    val selectAll: Boolean = true,
    val fabVisible: Boolean = false,
    val showSignaturePad: Boolean = false,
    val signatureAvailable: Boolean = false,
    val showFileList: Boolean = false,
)

