package com.neklaway.hme_reporting.common.domain.use_cases.hme_code_use_cases

import android.util.Log
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.model.toHMECodeEntity
import com.neklaway.hme_reporting.common.domain.repository.HMECodeRepository
import com.neklaway.hme_reporting.feature_settings.domain.repository.SettingsRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val TAG = "InsertHMECodeUseCase"

class InsertHMECodeUseCase @Inject constructor(
    val repo: HMECodeRepository,
    val settingsRepository: SettingsRepository
) {

    operator fun invoke(
        customerId: Long?,
        code: String,
        machineType: String?,
        machineNumber: String?,
        workDescription: String?
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        Log.d(TAG, "invoke: loading")
        val notIbau = !settingsRepository.isIbauUser().first()
        if (code.trim().isBlank()) {
            emit(Resource.Error("HME Code can't be blank"))
        } else if (customerId == null) {
            emit(Resource.Error("Please select customer"))
        } else if (notIbau && machineType?.trim()
                ?.isBlank() != false
        ) {
            emit(Resource.Error("Machine Type can't be blank"))
        } else if (notIbau && machineNumber?.trim()
                ?.isBlank() != false
        ) {
            emit(Resource.Error("Machine number can't be blank"))
        } else if (notIbau && workDescription?.trim()
                ?.isBlank() != false
        ) {
            emit(Resource.Error("Work Description can't be blank"))
        } else {
            Log.d(TAG, "invoke: no errors")
            val hmeCode =
                HMECode(customerId, code.trim(), machineType?.trim(), machineNumber?.trim(), workDescription?.trim())
            try {
                Log.d(TAG, "invoke: trying to insert $hmeCode")
                val result = repo.insert(hmeCode.toHMECodeEntity())
                if (result >= 0) {
                    emit(Resource.Success(true))
                    Log.d(TAG, "invoke: insert success")
                } else {
                    emit(Resource.Error("Error: Can't insert HME Code"))
                    Log.d(TAG, "invoke: insert fail")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error(e.message ?: "Error: Can't insert HME Code"))
                Log.d(TAG, "invoke: insert fail ${e.message}")

            }
        }
    }
}