package com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.hme_code_use_cases

import com.neklaway.hme_reporting.feature_time_sheet.domain.model.HMECode
import com.neklaway.hme_reporting.feature_time_sheet.domain.model.toHMECodeEntity
import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.HMECodeRepository
import com.neklaway.hme_reporting.feature_settings.domain.repository.SettingsRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

class UpdateHMECodeUseCase @Inject constructor(
    val repo: HMECodeRepository,
    val settingsRepository: SettingsRepository
) {

    operator fun invoke(
        id: Long,
        customerId: Long,
        code: String,
        machineType: String?,
        machineNumber: String?,
        workDescription: String?,
        fileNumber: Int,
        signerName: String? = null,
        signatureDate: Calendar? = null
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        val notIbau = !settingsRepository.isIbauUser().first()
        if (notIbau && code.trim().isBlank()) {
            emit(Resource.Error("HME Code can't be blank"))
        } else if (notIbau && machineType?.trim()?.isBlank() == true) {
            emit(Resource.Error("Machine Type can't be blank"))
        } else if (notIbau && machineNumber?.trim()?.isBlank() == true) {
            emit(Resource.Error("Machine number can't be blank"))
        } else if (notIbau && workDescription?.trim()?.isBlank() == true) {
            emit(Resource.Error("Work Description can't be blank"))
        } else {
            val hmeCode =
                HMECode(
                    customerId,
                    code.trim(),
                    machineType?.trim(),
                    machineNumber?.trim(),
                    workDescription?.trim(),
                    fileNumber,
                    signerName?.trim(),
                    signatureDate,
                    id
                )
            try {

                val result = repo.update(hmeCode.toHMECodeEntity())
                if (result > 0) {
                    emit(Resource.Success(true))
                } else {
                    emit(Resource.Error("Error: Can't update HME Code"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error(e.message ?: "Error: Can't update HME Code"))
            }
        }
    }
}