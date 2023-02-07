package com.neklaway.hme_reporting.common.domain.use_cases.visa_use_cases

import com.neklaway.hme_reporting.common.domain.model.Visa
import com.neklaway.hme_reporting.common.domain.repository.VisaRepository
import com.neklaway.hme_reporting.common.data.entity.toVisa
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetVisaByIdUseCase @Inject constructor(
    val repo: VisaRepository
) {

    operator fun invoke(id: Long): Flow<Resource<Visa>> = flow {
        emit(Resource.Loading())
        val result = repo.getById(id).toVisa()
        emit(Resource.Success(result))
    }

}