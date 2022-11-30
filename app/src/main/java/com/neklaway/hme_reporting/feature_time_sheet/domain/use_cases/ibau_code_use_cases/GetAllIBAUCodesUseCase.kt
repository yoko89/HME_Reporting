package com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.ibau_code_use_cases

import com.neklaway.hme_reporting.feature_time_sheet.data.entity.toIBAUCode
import com.neklaway.hme_reporting.feature_time_sheet.domain.model.IBAUCode
import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.IBAUCodeRepository
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