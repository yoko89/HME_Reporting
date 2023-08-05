package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.currency_exchange_rate

import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.CurrencyExchange
import com.neklaway.hme_reporting.utils.ResourceWithString

data class CurrencyExchangeState(
    val currencyExchangeList: List<CurrencyExchange> = emptyList(),
    val currencyName: String = "",
    val exchangeRate: ResourceWithString<Float> = ResourceWithString.Success(0f,""),
    val loading:Boolean=false,
    val selectedCurrency : CurrencyExchange? = null
)
