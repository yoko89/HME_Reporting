package com.neklaway.hme_reporting.common.domain.use_cases.customer_use_cases

import android.database.sqlite.SQLiteConstraintException
import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.toCustomerEntity
import com.neklaway.hme_reporting.common.domain.repository.CustomerRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class InsertCustomerListUseCase @Inject constructor(
    val repo: CustomerRepository
) {

    operator fun invoke(customers: List<Customer>): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            val results = repo.insert(customers.map { it.toCustomerEntity() })
            results.forEach { result ->
            if (result > 0) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("Error: Can't insert customers"))
            }
            }
        } catch (e: SQLiteConstraintException) {
            e.printStackTrace()
            emit(Resource.Error(e.message ?: "Error: Can't Insert Customer"))
        }
    }
}
