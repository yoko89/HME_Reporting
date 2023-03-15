package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.new_expanse

import android.net.Uri
import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.CurrencyExchange
import java.util.*

data class NewExpanseState(
    val customers: List<Customer> = emptyList(),
    val selectedCustomer: Customer? = null,
    val hmeCodes: List<HMECode> = emptyList(),
    val selectedHMECode: HMECode? = null,
    val date: Calendar? = null,
    val loading: Boolean = false,
    val invoiceNumber: String = "",
    val description: String = "",
    val personallyPaid: Boolean = false,
    val amount: String = "",
    val currencyList: List<CurrencyExchange> = emptyList(),
    val amountAED: String = "",
    val invoicesUris: List<Uri> = emptyList(),
    val showDatePicker: Boolean = false,
    val selectedCurrency: CurrencyExchange? = null
)
