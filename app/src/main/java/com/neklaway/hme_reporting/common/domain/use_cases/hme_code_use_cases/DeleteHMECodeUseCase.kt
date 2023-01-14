package com.neklaway.hme_reporting.common.domain.use_cases.hme_code_use_cases

import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.model.toHMECodeEntity
import com.neklaway.hme_reporting.common.domain.repository.HMECodeRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteHMECodeUseCase @Inject constructor(
    val repo: HMECodeRepository
) {

    operator fun invoke(hmeCode: HMECode): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        val result = repo.delete(hmeCode.toHMECodeEntity())
        if(result > 0){
            emit(Resource.Success(true))
        }else{
            emit(Resource.Error("Error: Can't delete HME Code"))
        }
    }

}