package com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.expanse_use_cases

import com.neklaway.hme_reporting.common.data.entity.toExpanse
import com.neklaway.hme_reporting.common.domain.repository.ExpanseRepository
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.Expense
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllExpansesFlowUseCase @Inject constructor(
    val repo: ExpanseRepository
) {

    operator fun invoke(): Flow<Resource<List<Expense>>> = flow {
        emit(Resource.Loading())
        try {
            emitAll(repo.getAllFlow().map { expanses ->
                Resource.Success(expanses.map { expanse ->
                    expanse.toExpanse()
                })
            })
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error: Can't get Expanse"))
        }

    }
}

