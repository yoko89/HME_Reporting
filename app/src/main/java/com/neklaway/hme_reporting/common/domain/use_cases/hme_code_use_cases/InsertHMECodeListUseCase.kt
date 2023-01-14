package com.neklaway.hme_reporting.common.domain.use_cases.hme_code_use_cases

import android.util.Log
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.model.toHMECodeEntity
import com.neklaway.hme_reporting.common.domain.repository.HMECodeRepository
import com.neklaway.hme_reporting.feature_settings.domain.repository.SettingsRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val TAG = "InsertHMECodeUseCase"

class InsertHMECodeListUseCase @Inject constructor(
    val repo: HMECodeRepository,
    val settingsRepository: SettingsRepository
) {

    operator fun invoke(
        hmeCodes: List<HMECode>
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        Log.d(TAG, "invoke: loading")
        try {
            Log.d(TAG, "invoke: trying to insert $hmeCodes")
            val results = repo.insert(hmeCodes.map { it.toHMECodeEntity() })
            results.forEach { result ->
                if (result >= 0) {
                    emit(Resource.Success(true))
                    Log.d(TAG, "invoke: insert success")
                } else {
                    emit(Resource.Error("Error: Can't insert HME Code"))
                    Log.d(TAG, "invoke: insert fail")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e.message ?: "Error: Can't update HME Code"))
            Log.d(TAG, "invoke: insert fail ${e.message}")

        }
    }
}
