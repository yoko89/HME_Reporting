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

private const val TAG = "UpdateTimeSheetListUseCase"

class UpdateTimeSheetListUseCase @Inject constructor(
    val repo: TimeSheetRepository,
    val getIsIbauUseCase: GetIsIbauUseCase
) {


    operator fun invoke(
        timeSheetList: List<TimeSheet>
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val result = repo.update(timeSheetList.map { it.toTimeSheetEntity() })
            if (result > 0) {
                emit(Resource.Success(true))
                Log.d(TAG, "invoke: update success")
            } else {
                emit(Resource.Error("Error: Can't Update Time Sheet"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e.message ?: "Error: Can't Update Time Sheet"))
        }
    }

}