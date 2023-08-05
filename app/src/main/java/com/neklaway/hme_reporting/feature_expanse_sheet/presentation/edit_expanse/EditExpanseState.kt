package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.edit_expanse

import android.net.Uri
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.CurrencyExchange
import com.neklaway.hme_reporting.utils.ResourceWithString
import java.util.Calendar

data class EditExpanseState(
    val loading: Boolean = false,
    val date: Calendar? = null,
    val invoiceNumber: String = "",
    val description: String = "",
    val personallyPaid: Boolean = false,
    val amount: ResourceWithString<Float> = ResourceWithString.Success(0f, ""),
    val currencyList: List<CurrencyExchange> = emptyList(),
    val amountAED: ResourceWithString<Float> = ResourceWithString.Success(0f, ""),
    val invoicesUris: List<Uri> = emptyList(),
    val showDatePicker: Boolean = false,
    val selectedCurrency: CurrencyExchange? = null,
    val expanseId: Long = -1,
)
