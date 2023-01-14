package com.neklaway.hme_reporting.common.domain.use_cases.ibau_code_use_cases

import com.neklaway.hme_reporting.common.data.entity.toIBAUCode
import com.neklaway.hme_reporting.common.domain.model.IBAUCode
import com.neklaway.hme_reporting.common.domain.repository.IBAUCodeRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetIBAUCodeByIdUseCase @Inject constructor(
    val repo: IBAUCodeRepository
) {

    operator fun invoke(id: Long): Flow<Resource<IBAUCode>> = flow {
        emit(Resource.Loading())
        val result = repo.getById(id).toIBAUCode()
        emit(Resource.Success(result))
    }

}