package com.neklaway.hme_reporting.common.domain.use_cases.customer_use_cases

import com.neklaway.hme_reporting.common.data.entity.toCustomer
import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.repository.CustomerRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllCustomersFlowUseCase @Inject constructor(
    val repo: CustomerRepository
) {

    operator fun invoke(): Flow<Resource<List<Customer>>> = flow {
        emit(Resource.Loading())
        try {
            emitAll(repo.getAllFlow().map { customerEntities ->
                Resource.Success(
                    customerEntities.map { customerEntity ->
                        customerEntity.toCustomer()
                    })
            })
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error: Can't get customers"))
        }

    }
}

