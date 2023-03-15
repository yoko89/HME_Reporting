package com.neklaway.hme_reporting.feature_expanse_sheet.domain.model

import com.neklaway.hme_reporting.common.data.entity.CurrencyExchangeEntity
@kotlinx.serialization.Serializable
data class CurrencyExchange(
    val currencyName:String,
    val rate:Float,
    val id:Long? = null
){
    override fun toString(): String {
        return "$currencyName\nrate: $rate"
    }
}

fun CurrencyExchange.toCurrencyExchangeEntity() : CurrencyExchangeEntity {
    return CurrencyExchangeEntity(
        currencyName = currencyName,
        rate = rate,
        id = id
    )
}