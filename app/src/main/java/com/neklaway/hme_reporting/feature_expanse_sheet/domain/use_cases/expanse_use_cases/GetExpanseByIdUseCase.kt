package com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.expanse_use_cases

import com.neklaway.hme_reporting.common.data.entity.toExpanse
import com.neklaway.hme_reporting.common.domain.repository.ExpanseRepository
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.Expanse
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class GetExpanseByIdUseCase @Inject constructor(
    val repo: ExpanseRepository
) {

    suspend operator fun invoke(id: Long): Flow<Resource<Expanse>> = flow {
        emit(Resource.Loading())
        emitAll(repo.getById(id).map {
            Resource.Success(it.toExpanse())
        })
    }

}