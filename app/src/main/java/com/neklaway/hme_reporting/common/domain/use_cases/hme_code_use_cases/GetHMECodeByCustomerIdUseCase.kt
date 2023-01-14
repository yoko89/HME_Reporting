package com.neklaway.hme_reporting.common.domain.use_cases.hme_code_use_cases

import com.neklaway.hme_reporting.common.data.entity.toHMECode
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.repository.HMECodeRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val TAG = "GetHMECodeByCustomerIdUseCase"
class GetHMECodeByCustomerIdUseCase @Inject constructor(
    val repo: HMECodeRepository
) {

    suspend operator fun invoke(id: Long): Flow<Resource<List<HMECode>>> = flow {
        emit(Resource.Loading())
        val result = repo.getByCustomerId(id).map { it.toHMECode() }
               emit(Resource.Success(result))
    }

}