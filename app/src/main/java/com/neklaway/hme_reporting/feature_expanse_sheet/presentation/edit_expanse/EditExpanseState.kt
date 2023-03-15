package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.edit_expanse

import android.net.Uri
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.CurrencyExchange
import java.util.*

data class EditExpanseState(
    val loading: Boolean = false,
    val date: Calendar? = null,
    val invoiceNumber: String = "",
    val description: String = "",
    val personallyPaid: Boolean = false,
    val amount: String = "",
    val currencyList: List<CurrencyExchange> = emptyList(),
    val amountAED: String = "",
    val invoicesUris: List<Uri> = emptyList(),
    val showDatePicker: Boolean = false,
    val selectedCurrency: CurrencyExchange? = null,
    val expanseId: Long = -1,
)
