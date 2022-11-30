package com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.customer_use_cases

import com.neklaway.hme_reporting.feature_time_sheet.data.entity.toCustomer
import com.neklaway.hme_reporting.feature_time_sheet.domain.model.Customer
import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.CustomerRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class GetAllCustomersUseCase @Inject constructor(
    val repo: CustomerRepository
) {

    suspend operator fun invoke(): Resource<List<Customer>> {

            return try {
            Resource.Success(repo.getAll().map { customerEntity ->
                    customerEntity.toCustomer()
                })
            }catch (e:IOException){
                Resource.Error(e.message?:"Can't get Customer List")
            }
        }

}


