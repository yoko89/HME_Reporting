package com.neklaway.hme_reporting.feature_time_sheet.presentation.hme_code

import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode

data class HMECodeState(
    val customers: List<Customer> = emptyList(),
    val hmeCodes: List<HMECode> = emptyList(),
    val loading: Boolean = false,
    val selectedCustomer: Customer? = null,
    val selectedHMECode: HMECode? = null,
    val isIbau:Boolean = false,
    val hmeCode: String = "",
    val machineType: String = "",
    val machineNumber: String = "",
    val workDescription: String = ""
)
