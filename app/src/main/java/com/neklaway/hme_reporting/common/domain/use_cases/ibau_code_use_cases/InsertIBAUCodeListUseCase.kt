package com.neklaway.hme_reporting.common.domain.use_cases.ibau_code_use_cases

import com.neklaway.hme_reporting.common.domain.model.IBAUCode
import com.neklaway.hme_reporting.common.domain.model.toIBAUCodeEntity
import com.neklaway.hme_reporting.common.domain.repository.IBAUCodeRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class InsertIBAUCodeListUseCase @Inject constructor(
    val repo: IBAUCodeRepository
) {

    operator fun invoke(
        ibauCodes: List<IBAUCode>
    ): Flow<Resource<Boolean>> = flow {

        emit(Resource.Loading())
        try {
            val results = repo.insert(ibauCodes.map { it.toIBAUCodeEntity() })
            results.forEach { result ->
            if (result > 0) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("Error: Can't Insert IBAU Codes"))
            }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e.message ?: "Error: Can't Insert IBAU Codes"))
        }
    }
}
