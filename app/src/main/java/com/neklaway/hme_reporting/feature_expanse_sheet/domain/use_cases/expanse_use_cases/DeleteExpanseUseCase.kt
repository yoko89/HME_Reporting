package com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.expanse_use_cases

import androidx.core.net.toFile
import androidx.core.net.toUri
import com.neklaway.hme_reporting.common.domain.repository.ExpanseRepository
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.Expense
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.toExpansesEntity
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteExpanseUseCase @Inject constructor(
    val repo: ExpanseRepository
) {

    operator fun invoke(expense: Expense): Flow<Resource<Boolean>> = flow {
        val result = repo.delete(expense.toExpansesEntity())

        if (result > 0) {
            expense.invoicesUri.map { it.toUri() }.forEach {
                it.toFile().delete()
            }
            emit(Resource.Success(true))
        } else {
            emit(Resource.Error("Error: Can't delete Expanse"))
        }
    }

}