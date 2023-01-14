package com.neklaway.hme_reporting.common.domain.use_cases.ibau_code_use_cases

import com.neklaway.hme_reporting.common.data.entity.toIBAUCode
import com.neklaway.hme_reporting.common.domain.model.IBAUCode
import com.neklaway.hme_reporting.common.domain.repository.IBAUCodeRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAllIBAUCodesUseCase @Inject constructor(
    val repo: IBAUCodeRepository
) {

    operator fun invoke(): Flow<Resource<List<IBAUCode>>> = flow {
        emit(Resource.Loading())
        val result = repo.getAll().map { it.toIBAUCode() }
        emit(Resource.Success(result))
    }

}