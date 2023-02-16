package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.currency_exchange_rate

import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.CurrencyExchange

data class CurrencyExchangeState(
    val currencyExchangeList: List<CurrencyExchange> = emptyList(),
    val currencyName: String = "",
    val exchangeRate: String = "",
    val loading:Boolean=false,
    val selectedCurrency : CurrencyExchange? = null
)
