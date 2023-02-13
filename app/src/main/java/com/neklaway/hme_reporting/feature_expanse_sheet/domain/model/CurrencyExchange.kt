package com.neklaway.hme_reporting.feature_expanse_sheet.domain.model

import com.neklaway.hme_reporting.common.data.entity.CurrencyExchangeEntity

data class CurrencyExchange(
    val currencyName:String,
    val rate:Float,
    val id:Long? = null
)

fun CurrencyExchange.toCurrencyExchangeEntity() : CurrencyExchangeEntity {
    return CurrencyExchangeEntity(
        currencyName = currencyName,
        rate = rate,
        id = id
    )
}