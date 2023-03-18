package com.neklaway.hme_reporting.common.domain.model

import android.util.Log
import com.neklaway.hme_reporting.common.data.entity.AllowanceType
import com.neklaway.hme_reporting.common.data.entity.TimeSheetEntity
import com.neklaway.hme_reporting.utils.CalendarAsLongSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import java.util.*

private const val TAG = "TimeSheet"

@Serializable
data class TimeSheet(
    val HMEId: Long,
    val IBAUId: Long?,
    @Serializable(with = CalendarAsLongSerializer::class)
    val date: Calendar,
    @Serializable(with = CalendarAsLongSerializer::class)
    val travelStart: Calendar?,
    @Serializable(with = CalendarAsLongSerializer::class)
    val workStart: Calendar?,
    @Serializable(with = CalendarAsLongSerializer::class)
    val workEnd: Calendar?,
    @Serializable(with = CalendarAsLongSerializer::class)
    val travelEnd: Calendar?,
    val breakDuration: Float,
    val traveledDistance: Int,
    val overTimeDay: Boolean = false,
    val created: Boolean = false,
    val expanseCreated: Boolean= false,
    val travelDay: Boolean = false,
    val noWorkDay: Boolean = false,
    val selected: Boolean = !created,
    val expanseSelected: Boolean = !expanseCreated,
    val dailyAllowance : AllowanceType? = null,
    val id: Long? = null,
    var overLap: Boolean = false,
) {
    companion object {
        val listSerializer: KSerializer<List<TimeSheet>> = ListSerializer(serializer())
    }

    private val workTimeTotal = if (!noWorkDay) {
        if ((workEnd != null) && (workStart != null)) {
            Log.d(
                TAG,
                "calculated workTime: ${((workEnd.timeInMillis - workStart.timeInMillis) / 3600000f) - (breakDuration)}"
            )
            ((workEnd.timeInMillis - workStart.timeInMillis) / 3600000f) - (breakDuration)
        } else {
            0f
        }
    } else {
        0f
    }

    val workTime = if (!overTimeDay)
        if (workTimeTotal < 8f) {
            workTimeTotal
        } else {
            8f
        } else 0f

    val overTime = if (overTimeDay) {
        workTimeTotal
    } else if (workTimeTotal > 8f) {
        workTimeTotal - 8f
    } else {
        0f
    }

    val travelTime = if ((travelStart != null) && (travelEnd != null)) {
        ((travelEnd.timeInMillis - travelStart.timeInMillis) / 3600000f) - (workTimeTotal) - (breakDuration)
    } else {
        0f
    }


    val workTimeString = String.format("%.2f", workTime)


    val overTimeString = String.format("%.2f", overTime)


    val travelTimeString = String.format("%.2f", travelTime)

    val breakTimeString = breakDuration.toString()

}

fun TimeSheet.toTimeSheetEntity(): TimeSheetEntity {
    return TimeSheetEntity(
        HMEId = HMEId,
        IBAUId = IBAUId,
        date = date.timeInMillis,
        travelStart = travelStart?.timeInMillis,
        workStart = workStart?.timeInMillis,
        workEnd = workEnd?.timeInMillis,
        travelEnd = travelEnd?.timeInMillis,
        breakDuration = breakDuration,
        traveledDistance = traveledDistance,
        overTimeDay = overTimeDay,
        created = created,
        expanseCreated = expanseCreated,
        travelDay = travelDay,
        noWorkDay = noWorkDay,
        expanseSelected = expanseSelected,
        dailyAllowance = dailyAllowance,
        id = id
    )
}



