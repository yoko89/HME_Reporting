package com.neklaway.hme_reporting.feature_visa.domain.use_cases

import com.neklaway.hme_reporting.feature_time_sheet.domain.model.Customer
import com.neklaway.hme_reporting.feature_time_sheet.domain.model.toCustomerEntity
import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.CustomerRepository
import com.neklaway.hme_reporting.feature_visa.domain.model.Visa
import com.neklaway.hme_reporting.feature_visa.domain.model.toVisaEntity
import com.neklaway.hme_reporting.feature_visa.domain.repository.VisaRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteVisaUseCase @Inject constructor(
    val repo: VisaRepository
) {

    operator fun invoke(visa: Visa): Flow<Resource<Boolean>> = flow {
        val result = repo.delete(visa.toVisaEntity())
        if(result > 0){
            emit(Resource.Success(true))
        }else{
            emit(Resource.Error("Error: Can't delete visa"))
        }
    }

}