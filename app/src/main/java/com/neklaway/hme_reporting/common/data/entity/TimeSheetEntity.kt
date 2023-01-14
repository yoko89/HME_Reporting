package com.neklaway.hme_reporting.common.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.neklaway.hme_reporting.common.domain.model.TimeSheet
import com.neklaway.hme_reporting.utils.toCalender

@Entity(
    indices = [Index(value = ["HMEId"])],
    foreignKeys = [ForeignKey(
        entity = HMECodeEntity::class,
        parentColumns = ["id"],
        childColumns = ["HMEId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    tableName = "timeSheetTable"
)
data class TimeSheetEntity(
    val HMEId: Long,
    val IBAUId: Long?,
    val date: Long,
    val travelStart: Long?,
    val workStart: Long?,
    val workEnd: Long?,
    val travelEnd: Long?,
    val breakDuration: Float,
    val traveledDistance: Int,
    val overTimeDay: Boolean = false,
    val created: Boolean = false,
    val travelDay: Boolean = false,
    val noWorkDay : Boolean = false,
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null
)


fun TimeSheetEntity.toTimeSheet(): TimeSheet {
    return TimeSheet(
        HMEId = HMEId,
        IBAUId = IBAUId,
        date = date.toCalender(),
        travelStart = travelStart?.toCalender(),
        workStart = workStart?.toCalender(),
        workEnd = workEnd?.toCalender(),
        travelEnd = travelEnd?.toCalender(),
        breakDuration = breakDuration,
        traveledDistance = traveledDistance,
        overTimeDay = overTimeDay,
        created = created,
        travelDay = travelDay,
        noWorkDay = noWorkDay,
        selected = !created,
        id = id
    )
}
