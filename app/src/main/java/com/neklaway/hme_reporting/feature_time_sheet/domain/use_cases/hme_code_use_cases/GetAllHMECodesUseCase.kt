package com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.hme_code_use_cases

import com.neklaway.hme_reporting.feature_time_sheet.data.entity.toHMECode
import com.neklaway.hme_reporting.feature_time_sheet.domain.model.HMECode
import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.HMECodeRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAllHMECodesUseCase @Inject constructor(
    val repo: HMECodeRepository
) {

    operator fun invoke(): Flow<Resource<List<HMECode>>> = flow {
        emit(Resource.Loading())
        val result = repo.getAll().map { it.toHMECode() }
        emit(Resource.Success(result))
    }

}