package com.neklaway.hme_reporting.common.domain.use_cases.time_sheet_use_cases

import android.util.Log
import com.neklaway.hme_reporting.common.domain.model.TimeSheet
import com.neklaway.hme_reporting.common.domain.model.toTimeSheetEntity
import com.neklaway.hme_reporting.common.domain.repository.TimeSheetRepository
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.is_ibau.GetIsIbauUseCase
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

private const val TAG = "InsertTimeSheetUseCase"

class InsertTimeSheetUseCase @Inject constructor(
    val getIsIbauUseCase: GetIsIbauUseCase,
    val repo: TimeSheetRepository
) {

    operator fun invoke(
        HMEId: Long?,
        IBAUId: Long?,
        date: Calendar?,
        travelStart: Calendar?,
        workStart: Calendar?,
        workEnd: Calendar?,
        travelEnd: Calendar?,
        breakDuration: Float?,
        traveledDistance: Int?,
        overTimeDay: Boolean,
        travelDay: Boolean,
        noWorkDay: Boolean
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        if (HMEId == null) {
            emit(Resource.Error("HME Code must be selected"))
            return@flow
        }
        if ((getIsIbauUseCase()) && (IBAUId == null)) {
            emit(Resource.Error("IBAU Code must be selected"))
            return@flow
        }
        if (date == null) {
            emit(Resource.Error("Date must be selected"))
            return@flow
        }
        if ((!noWorkDay) && (travelStart == null)) {
            emit(Resource.Error("Travel Start must be selected"))
            return@flow
        }
        if ((!noWorkDay && !travelDay) && (workStart == null)) {
            emit(Resource.Error("Work Start must be selected"))
            return@flow
        }
        if ((!noWorkDay && !travelDay) && (workEnd == null)) {
            emit(Resource.Error("Work End must be selected"))
            return@flow
        }
        if ((!noWorkDay) && (travelEnd == null)) {
            emit(Resource.Error("Travel End must be selected"))
            return@flow
        }
        if ((!noWorkDay) && (traveledDistance == null)) {
            emit(Resource.Error("Travel distance can't be empty"))
            return@flow
        }
        if ((!noWorkDay && !travelDay) && (breakDuration == null)) {
            emit(Resource.Error("Break Duration can't be empty"))
            return@flow
        }
        if ((!noWorkDay && !travelDay) && ((travelStart?.compareTo(workStart ?: travelStart)
                ?: 0) > 0)
        ) {
            emit(Resource.Error("Work Start should be after Travel Start"))
            return@flow
        }
        if ((!noWorkDay && !travelDay) && ((workStart?.compareTo(workEnd ?: workStart) ?: 0) > 0)
        ) {
            emit(Resource.Error("Work End should be after Work Start"))
            return@flow
        }
        if ((!noWorkDay && !travelDay) && ((workEnd?.compareTo(travelEnd ?: workEnd) ?: 0) > 0)
        ) {
            emit(Resource.Error("Travel End should be after Work End"))
            return@flow
        }
        if ((travelDay) && ((travelStart?.compareTo(travelEnd ?: travelStart) ?: 0) > 0)
        ) {
            emit(Resource.Error("Travel End should be after Travel Start"))
            return@flow
        }
        val timeSheet = TimeSheet(
            HMEId = HMEId,
            IBAUId = IBAUId,
            date = date,
            travelStart = if (noWorkDay) null else travelStart,
            workStart = if (noWorkDay || travelDay) null else workStart,
            workEnd = if (noWorkDay || travelDay) null else workEnd,
            travelEnd = if (noWorkDay) null else travelEnd,
            breakDuration = if (noWorkDay || travelDay) 0f else breakDuration ?: 0f,
            traveledDistance = if (noWorkDay) 0 else traveledDistance ?: 0,
            overTimeDay = overTimeDay,
            travelDay = travelDay,
            noWorkDay = noWorkDay
        )

        Log.d(TAG, "InsertTimeSheet: $timeSheet")

        if (timeSheet.workTime < 0f) {
            emit(Resource.Error("Work Time can't be less than zero"))
            return@flow
        }
        try {
            val result = repo.insert(timeSheet.toTimeSheetEntity())
            if (result > 0) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("Error: Can't insert Time Sheet"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e.message ?: "Error: Can't insert Time Sheet"))
        }
    }
}

