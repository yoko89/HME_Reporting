package com.neklaway.hme_reporting.feature_time_sheet.presentation.customer

import com.neklaway.hme_reporting.feature_time_sheet.domain.model.Customer

data class CustomerState(
    val customers: List<Customer> = emptyList(),
    val loading: Boolean = false,
    val selectedCustomer: Customer? = null,
    val customerName: String = "",
    val customerCity: String = "",
    val customerCountry: String = ""
)
