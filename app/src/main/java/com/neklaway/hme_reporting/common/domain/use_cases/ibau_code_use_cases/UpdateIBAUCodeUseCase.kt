package com.neklaway.hme_reporting.common.domain.use_cases.ibau_code_use_cases

import com.neklaway.hme_reporting.common.domain.model.IBAUCode
import com.neklaway.hme_reporting.common.domain.model.toIBAUCodeEntity
import com.neklaway.hme_reporting.common.domain.repository.IBAUCodeRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateIBAUCodeUseCase @Inject constructor(
    val repo: IBAUCodeRepository
) {

    operator fun invoke(
        id: Long,
        hmeId: Long,
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
        } else {
            val ibauCode = IBAUCode(hmeId, code.trim(), machineType.trim(), machineNumber.trim(), workDescription.trim(), id)
            try {
                val result = repo.update(ibauCode.toIBAUCodeEntity())
                if (result > 0) {
                    emit(Resource.Success(true))
                } else {
                    emit(Resource.Error("Error: Can't update IBAU Code"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error(e.message ?: "Error: Can't update IBAU Code"))
            }
        }
    }
}