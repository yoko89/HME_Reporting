package com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.expanse_use_cases

import androidx.core.net.toFile
import androidx.core.net.toUri
import com.neklaway.hme_reporting.common.domain.repository.ExpanseRepository
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.Expanse
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.toExpansesEntity
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteExpanseUseCase @Inject constructor(
    val repo: ExpanseRepository
) {

    operator fun invoke(expanse: Expanse): Flow<Resource<Boolean>> = flow {
        val result = repo.delete(expanse.toExpansesEntity())

        if (result > 0) {
            expanse.invoicesUri.map { it.toUri() }.forEach {
                it.toFile().delete()
            }
            emit(Resource.Success(true))
        } else {
            emit(Resource.Error("Error: Can't delete Expanse"))
        }
    }

}