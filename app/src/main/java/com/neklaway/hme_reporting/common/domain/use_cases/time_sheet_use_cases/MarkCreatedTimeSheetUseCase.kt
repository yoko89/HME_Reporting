package com.neklaway.hme_reporting.common.domain.use_cases.time_sheet_use_cases

import android.util.Log
import com.neklaway.hme_reporting.common.domain.model.TimeSheet
import com.neklaway.hme_reporting.common.domain.model.toTimeSheetEntity
import com.neklaway.hme_reporting.common.domain.repository.TimeSheetRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val TAG: String = "MarkCreatedTimeSheet"

class MarkCreatedTimeSheetUseCase @Inject constructor(
    val repo: TimeSheetRepository
) {

    operator fun invoke(timeSheets: List<TimeSheet>): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        val markedTimeSheet = timeSheets.map { it.copy(created = true) }
        val markedTimeSheetEntity = markedTimeSheet.map { it.toTimeSheetEntity() }
        val result = repo.update(markedTimeSheetEntity)
        Log.d(TAG, "invoke: $markedTimeSheetEntity")
        if (result > 0) {
            emit(Resource.Success(true))
        } else {
            emit(Resource.Error("Error: Can't Create Time Sheets"))
        }
    }

}