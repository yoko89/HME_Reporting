package com.neklaway.hme_reporting.feature_car_mileage.domain.model

import com.neklaway.hme_reporting.common.data.entity.CarMileageEntity
import com.neklaway.hme_reporting.utils.DateAsLongSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CarMileage(
    @Serializable(with = DateAsLongSerializer::class)
    val startDate: Calendar,
    @Serializable(with = DateAsLongSerializer::class)
    val startTime: Calendar,
    val startMileage: Long,
    @Serializable(with = DateAsLongSerializer::class)
    val endDate: Calendar,
    @Serializable(with = DateAsLongSerializer::class)
    val endTime: Calendar,
    val endMileage: Long,
    val id: Long? = null,
)

fun CarMileage.toCarMileageEntity(): CarMileageEntity {
    return CarMileageEntity(
        id,
        startDate.timeInMillis,
        startTime.timeInMillis,
        startMileage,
        endDate.timeInMillis,
        endTime.timeInMillis,
        endMileage
    )
}

