package com.neklaway.hme_reporting.common.domain.use_cases.customer_use_cases

import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.toCustomerEntity
import com.neklaway.hme_reporting.common.domain.repository.CustomerRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteCustomerUseCase @Inject constructor(
    val repo: CustomerRepository
) {

    operator fun invoke(customer: Customer): Flow<Resource<Boolean>> = flow {
        val result = repo.delete(customer.toCustomerEntity())
        if(result > 0){
            emit(Resource.Success(true))
        }else{
            emit(Resource.Error("Error: Can't delete customer"))
        }
    }

}