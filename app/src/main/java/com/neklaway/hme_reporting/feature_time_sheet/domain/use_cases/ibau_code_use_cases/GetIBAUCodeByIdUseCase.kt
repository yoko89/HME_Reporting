package com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.ibau_code_use_cases

import com.neklaway.hme_reporting.feature_time_sheet.data.entity.toIBAUCode
import com.neklaway.hme_reporting.feature_time_sheet.domain.model.IBAUCode
import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.IBAUCodeRepository
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