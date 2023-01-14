package com.neklaway.hme_reporting.common.domain.use_cases.ibau_code_use_cases

import com.neklaway.hme_reporting.common.domain.model.IBAUCode
import com.neklaway.hme_reporting.common.domain.model.toIBAUCodeEntity
import com.neklaway.hme_reporting.common.domain.repository.IBAUCodeRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteIBAUCodeUseCase @Inject constructor(
    val repo: IBAUCodeRepository
) {

    operator fun invoke(ibauCode: IBAUCode): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        val result = repo.delete(ibauCode.toIBAUCodeEntity())
        if(result > 0){
            emit(Resource.Success(true))
        }else{
            emit(Resource.Error("Error: Can't delete IBAU Code"))
        }
    }

}