package com.neklaway.hme_reporting.feature_time_sheet.presentation.ibau_code

import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.model.IBAUCode

data class IBAUCodeState(
    val customers: List<Customer> = emptyList(),
    val hmeCodes: List<HMECode> = emptyList(),
    val ibauCodes: List<IBAUCode> = emptyList(),
    val loading: Boolean = false,
    val selectedCustomer: Customer? = null,
    val selectedHMECode: HMECode? = null,
    val selectedIBAUCode : IBAUCode? = null,
    val ibauCode: String = "",
    val machineType: String = "",
    val machineNumber: String = "",
    val workDescription: String = ""
)
