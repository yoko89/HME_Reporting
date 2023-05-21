package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.currency_exchange_rate

import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.CurrencyExchange

sealed class CurrencyExchangeUserEvent{
    object UpdateCurrency:CurrencyExchangeUserEvent()
    object SaveCurrency:CurrencyExchangeUserEvent()
    class CurrencyNameChange(val name:String):CurrencyExchangeUserEvent()
    class CurrencyRateChanged(val rate:String):CurrencyExchangeUserEvent()
    class CurrencySelected(val currencyExchange: CurrencyExchange):CurrencyExchangeUserEvent()
    class DeleteRate(val currencyExchange: CurrencyExchange):CurrencyExchangeUserEvent()
}
