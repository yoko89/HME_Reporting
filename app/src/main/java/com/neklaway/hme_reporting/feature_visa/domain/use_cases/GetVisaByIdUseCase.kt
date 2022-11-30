package com.neklaway.hme_reporting.feature_visa.domain.use_cases

import com.neklaway.hme_reporting.feature_time_sheet.data.entity.toCustomer
import com.neklaway.hme_reporting.feature_time_sheet.domain.model.Customer
import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.CustomerRepository
import com.neklaway.hme_reporting.feature_visa.data.entity.toVisa
import com.neklaway.hme_reporting.feature_visa.domain.model.Visa
import com.neklaway.hme_reporting.feature_visa.domain.repository.VisaRepository
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