package com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.time_sheet_use_cases

import com.neklaway.hme_reporting.feature_time_sheet.domain.model.TimeSheet
import com.neklaway.hme_reporting.feature_time_sheet.domain.model.toTimeSheetEntity
import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.TimeSheetRepository
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.is_ibau.GetIsIbauUseCase
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

class UpdateTimeSheetUseCase @Inject constructor(
    val repo: TimeSheetRepository,
    val getIsIbauUseCase: GetIsIbauUseCase
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
        noWorkDay: Boolean,
        id: Long,
        created: Boolean
    ): Flow<Resource<Boolean>> = flow {

        emit(Resource.Loading())
        if (HMEId == null) {
            emit(Resource.Error("HME Code must be selected"))
        } else if ((getIsIbauUseCase()) && (IBAUId == null)) {
            emit(Resource.Error("IBAU Code must be selected"))
        } else if (date == null) {
            emit(Resource.Error("Date must be selected"))
        } else if ((!noWorkDay) && (travelStart == null)) {
            emit(Resource.Error("Travel Start must be selected"))
        } else if ((!noWorkDay && !travelDay) && (workStart == null)) {
            emit(Resource.Error("Work Start must be selected"))
        } else if ((!noWorkDay && !travelDay) && (workEnd == null)) {
            emit(Resource.Error("Work End must be selected"))
        } else if ((!noWorkDay) && (travelEnd == null)) {
            emit(Resource.Error("Travel End must be selected"))
        } else if ((!noWorkDay) && (traveledDistance == null)) {
            emit(Resource.Error("Travel distance can't be empty"))
        } else if ((!noWorkDay || !travelDay) && (breakDuration == null)) {
            emit(Resource.Error("Break Duration can't be empty"))
        } else if ((!noWorkDay && !travelDay) && ((travelStart?.compareTo(workStart ?: travelStart)
                ?: 0) > 0)
        ) {
            emit(Resource.Error("Work Start should be after Travel Start"))
        } else if ((!noWorkDay && !travelDay) && ((workStart?.compareTo(workEnd ?: workStart)
                ?: 0) > 0)
        ) {
            emit(Resource.Error("Work End should be after Work Start"))
        } else if ((!noWorkDay && !travelDay) && ((workEnd?.compareTo(travelEnd ?: workEnd)
                ?: 0) > 0)
        ) {
            emit(Resource.Error("Travel End should be after Work End"))
        } else if ((travelDay) && ((travelStart?.compareTo(travelEnd ?: travelStart)
                ?: 0) > 0)
        ) {
            emit(Resource.Error("Travel End should be after Travel Start"))
        } else {
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
                created = created,
                travelDay = travelDay,
                noWorkDay = noWorkDay,
                selected = !created,
                id = id,
            )

            if (timeSheet.workTime < 0f) {
                emit(Resource.Error("Work Time can't be less than zero"))
            } else {
                try {
                    val result = repo.update(timeSheet.toTimeSheetEntity())
                    if (result > 0) {
                        emit(Resource.Success(true))
                    } else {
                        emit(Resource.Error("Error: Can't Update Time Sheet"))
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(Resource.Error(e.message ?: "Error: Can't Update Time Sheet"))
                }
            }
        }
    }
}


