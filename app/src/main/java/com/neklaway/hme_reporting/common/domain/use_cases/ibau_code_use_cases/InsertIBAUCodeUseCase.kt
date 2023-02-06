package com.neklaway.hme_reporting.common.domain.use_cases.ibau_code_use_cases

import com.neklaway.hme_reporting.common.domain.model.IBAUCode
import com.neklaway.hme_reporting.common.domain.model.toIBAUCodeEntity
import com.neklaway.hme_reporting.common.domain.repository.IBAUCodeRepository
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
            return@flow
        }
        if (machineType.trim().isBlank()) {
            emit(Resource.Error("Machine Type can't be blank"))
            return@flow
        }
        if (machineNumber.trim().isBlank()) {
            emit(Resource.Error("Machine number can't be blank"))
            return@flow
        }
        if (workDescription.trim().isBlank()) {
            emit(Resource.Error("Work Description can't be blank"))
            return@flow
        }
        if (hmeId == null) {
            emit(Resource.Error("HME Must be selected First"))
            return@flow
        }

        val ibauCode =
            IBAUCode(
                hmeId,
                code.trim(),
                machineType.trim(),
                machineNumber.trim(),
                workDescription.trim(),
                null
            )
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