package com.neklaway.hme_reporting.common.domain.use_cases.visa_use_cases

import com.neklaway.hme_reporting.common.data.entity.toVisa
import com.neklaway.hme_reporting.common.domain.model.Visa
import com.neklaway.hme_reporting.common.domain.repository.VisaRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllVisasFlowUseCase @Inject constructor(
    val repo: VisaRepository
) {

    operator fun invoke(): Flow<Resource<List<Visa>>> = flow {
        emit(Resource.Loading())
        try {
            emitAll(repo.getAllFlow().map { visaEntities ->
                Resource.Success(visaEntities.map { visaEntity ->
                        visaEntity.toVisa()
                    })
            })
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error: Can't get Visa"))
        }

    }
}

