package com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.ibau_code_use_cases

import com.neklaway.hme_reporting.feature_time_sheet.domain.model.IBAUCode
import com.neklaway.hme_reporting.feature_time_sheet.domain.model.toIBAUCodeEntity
import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.IBAUCodeRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class InsertIBAUCodeUseCase @Inject constructor(
    val repo: IBAUCodeRepository
) {

    operator fun invoke(
        hmeId: Long?,
        code: String,
        machineType: String,
        machineNumber: String,
        workDescription: String
    ): Flow<Resource<Boolean>> = flow {

        emit(Resource.Loading())

        if (code.trim().isBlank()) {
            emit(Resource.Error("IBAU Code can't be blank"))
        } else if (machineType.trim().isBlank()) {
            emit(Resource.Error("Machine Type can't be blank"))
        } else if (machineNumber.trim().isBlank()) {
            emit(Resource.Error("Machine number can't be blank"))
        } else if (workDescription.trim().isBlank()) {
            emit(Resource.Error("Work Description can't be blank"))
        } else if (hmeId == null) {
            emit(Resource.Error("HME Must be selected First"))
        }

        if (code.isNotBlank() and machineNumber.isNotBlank() and machineType.isNotBlank() and workDescription.isNotBlank() and (hmeId != null)) {
            val ibauCode =
                IBAUCode(hmeId!!, code.trim(), machineType.trim(), machineNumber.trim(), workDescription.trim(), null)
            try {
                val result = repo.insert(ibauCode.toIBAUCodeEntity())
                if (result > 0) {
                    emit(Resource.Success(true))
                } else {
                    emit(Resource.Error("Error: Can't Insert IBAU Code"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error(e.message ?: "Error: Can't Insert IBAU Code"))
            }
        }
    }

}