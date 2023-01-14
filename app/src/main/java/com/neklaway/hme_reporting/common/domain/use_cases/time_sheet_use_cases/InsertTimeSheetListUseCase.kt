package com.neklaway.hme_reporting.common.domain.use_cases.time_sheet_use_cases

import android.util.Log
import com.neklaway.hme_reporting.common.domain.model.TimeSheet
import com.neklaway.hme_reporting.common.domain.model.toTimeSheetEntity
import com.neklaway.hme_reporting.common.domain.repository.TimeSheetRepository
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.is_ibau.GetIsIbauUseCase
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val TAG = "InsertTimeSheetUseCase"

class InsertTimeSheetListUseCase @Inject constructor(
    val getIsIbauUseCase: GetIsIbauUseCase,
    val repo: TimeSheetRepository
) {

    operator fun invoke(
        timeSheets: List<TimeSheet>
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        Log.d(TAG, "InsertTimeSheet: $timeSheets")

        try {
            val results = repo.insert(timeSheets.map { it.toTimeSheetEntity() })
            results.forEach { result ->
                if (result > 0) {
                    emit(Resource.Success(true))
                } else {
                    emit(Resource.Error("Error: Can't insert Time Sheets"))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e.message ?: "Error: Can't insert Time Sheets"))
        }
    }
}

