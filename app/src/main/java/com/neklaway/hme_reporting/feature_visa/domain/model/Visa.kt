package com.neklaway.hme_reporting.feature_visa.domain.model

import com.neklaway.hme_reporting.common.data.entity.VisaEntity
import com.neklaway.hme_reporting.utils.CalendarAsLongSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Visa(
    val country: String,
    @Serializable(with = CalendarAsLongSerializer::class)
    val date: Calendar,
    val selected: Boolean = false,
    val id: Long? = null,
)

fun Visa.toVisaEntity(): VisaEntity {
    return VisaEntity(id, country, date.timeInMillis, selected)
}
