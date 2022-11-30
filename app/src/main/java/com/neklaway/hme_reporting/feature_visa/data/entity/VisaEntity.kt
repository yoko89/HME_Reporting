package com.neklaway.hme_reporting.feature_visa.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.neklaway.hme_reporting.feature_time_sheet.domain.model.Customer
import com.neklaway.hme_reporting.feature_visa.domain.model.Visa
import com.neklaway.hme_reporting.utils.toCalender

@Entity(
    indices = [Index(value = ["country"], unique = true)],
    tableName = "visaTable"
)
data class VisaEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val country: String,
    val date: Long,
    val selected:Boolean =false

)

fun VisaEntity.toVisa(): Visa {
    return Visa(country,date.toCalender(),selected,id)
}