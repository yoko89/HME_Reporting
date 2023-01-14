package com.neklaway.hme_reporting.common.domain.use_cases.hme_code_use_cases

import com.neklaway.hme_reporting.common.data.entity.toHMECode
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.repository.HMECodeRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetHMECodeByIdUseCase @Inject constructor(
    val repo: HMECodeRepository
) {

    operator fun invoke(id: Long): Flow<Resource<HMECode>> = flow {
        emit(Resource.Loading())
        val result = repo.getById(id).toHMECode()
        emit(Resource.Success(result))
    }

}