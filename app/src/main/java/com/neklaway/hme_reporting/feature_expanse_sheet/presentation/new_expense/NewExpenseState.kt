package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.new_expense

import android.net.Uri
import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.CurrencyExchange
import com.neklaway.hme_reporting.utils.ResourceWithString
import java.util.*

data class NewExpenseState(
    val customers: List<Customer> = emptyList(),
    val selectedCustomer: Customer? = null,
    val hmeCodes: List<HMECode> = emptyList(),
    val selectedHMECode: HMECode? = null,
    val date: Calendar? = null,
    val loading: Boolean = false,
    val invoiceNumber: String = "",
    val description: String = "",
    val personallyPaid: Boolean = false,
    val amount: ResourceWithString<Float> = ResourceWithString.Success(0f, ""),
    val currencyList: List<CurrencyExchange> = emptyList(),
    val amountAED: ResourceWithString<Float> = ResourceWithString.Success(0f, ""),
    val invoicesUris: List<Uri> = emptyList(),
    val showDatePicker: Boolean = false,
    val selectedCurrency: CurrencyExchange? = null
)
