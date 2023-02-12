package com.neklaway.hme_reporting.common.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.CurrencyExchange

@Entity(
    tableName = "currencyExchangeTable"
)
data class CurrencyExchangeEntity(
    val currencyName:String,
    val rate:Float,
    @PrimaryKey(autoGenerate = true)
    val id:Long?
)


fun CurrencyExchangeEntity.toCurrencyExchange() : CurrencyExchange {
    return CurrencyExchange(
        currencyName = currencyName,
    rate = rate,
        id = id
    )
}