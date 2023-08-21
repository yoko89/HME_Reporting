package com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.expanse_use_cases

import com.neklaway.hme_reporting.common.data.entity.toExpanse
import com.neklaway.hme_reporting.common.domain.repository.ExpanseRepository
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.Expense
import com.neklaway.hme_reporting.utils.Resource
import javax.inject.Inject

class GetExpanseByIdUseCase @Inject constructor(
    val repo: ExpanseRepository
) {

    operator fun invoke(id: Long): Resource<Expense> {
        return try {
            Resource.Success(repo.getById(id).toExpanse())
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage?:"Can't get expanse")
        }
    }
}

