package com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.customer_use_cases

import android.database.sqlite.SQLiteConstraintException
import com.neklaway.hme_reporting.feature_time_sheet.domain.model.Customer
import com.neklaway.hme_reporting.feature_time_sheet.domain.model.toCustomerEntity
import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.CustomerRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class InsertCustomerUseCase @Inject constructor(
    val repo: CustomerRepository
) {

    operator fun invoke(name:String , city:String,country:String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        if(name.trim().isBlank()){
            emit(Resource.Error("Customer name can't be blank"))
        }
        else if(city.trim().isBlank()){
            emit(Resource.Error("Customer city can't be blank"))
        }
        else if(country.trim().isBlank()){
            emit(Resource.Error("Customer country can't be blank"))
        }else{
            try {
            val customer = Customer(country.trim(),city.trim(),name.trim())
            val result = repo.insert(customer.toCustomerEntity())
            if(result > 0){
                emit(Resource.Success(true))
            }else{
                emit(Resource.Error("Error: Can't insert customer"))
            }
        }catch (e: SQLiteConstraintException){
                e.printStackTrace()
                emit(Resource.Error(e.message?:"Error: Can't Insert Customer"))
            }
        }
    }
}