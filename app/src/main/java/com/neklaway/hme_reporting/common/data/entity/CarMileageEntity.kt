package com.neklaway.hme_reporting.common.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.neklaway.hme_reporting.common.domain.model.CarMileage
import com.neklaway.hme_reporting.utils.toCalender

@Entity(
    tableName = "carMileageTable"
)
data class CarMileageEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val startDate: Long,
    val startTime: Long,
    val startMileage: Long,
    val endDate: Long,
    val endTime: Long,
    val endMileage: Long,
)

fun CarMileageEntity.toCarMileage(): CarMileage {
    return CarMileage(
        startDate.toCalender(),
        startTime.toCalender(),
        startMileage,
        endDate.toCalender(),
        endTime.toCalender(),
        endMileage,
        id
    )
}