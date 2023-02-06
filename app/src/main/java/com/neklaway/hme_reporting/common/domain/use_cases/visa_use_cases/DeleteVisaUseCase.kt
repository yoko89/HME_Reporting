package com.neklaway.hme_reporting.common.domain.use_cases.visa_use_cases

import com.neklaway.hme_reporting.common.domain.model.Visa
import com.neklaway.hme_reporting.common.domain.model.toVisaEntity
import com.neklaway.hme_reporting.common.domain.repository.VisaRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteVisaUseCase @Inject constructor(
    val repo: VisaRepository
) {

    operator fun invoke(visa: Visa): Flow<Resource<Boolean>> = flow {
        val result = repo.delete(visa.toVisaEntity())
        if (result > 0) {
            emit(Resource.Success(true))
        } else {
            emit(Resource.Error("Error: Can't delete visa"))
        }
    }

}