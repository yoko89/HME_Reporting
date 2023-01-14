package com.neklaway.hme_reporting.common.domain.use_cases.customer_use_cases

import com.neklaway.hme_reporting.common.data.entity.toCustomer
import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.repository.CustomerRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCustomerByIdUseCase @Inject constructor(
    val repo: CustomerRepository
) {

    operator fun invoke(id: Long): Flow<Resource<Customer>> = flow {
        emit(Resource.Loading())
        val result = repo.getById(id).toCustomer()
        emit(Resource.Success(result))
    }

}