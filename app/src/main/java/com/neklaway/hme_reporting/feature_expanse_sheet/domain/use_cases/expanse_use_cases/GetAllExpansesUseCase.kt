package com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.expanse_use_cases

import com.neklaway.hme_reporting.common.data.entity.toExpanse
import com.neklaway.hme_reporting.common.domain.repository.ExpanseRepository
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.Expense
import com.neklaway.hme_reporting.utils.Resource
import javax.inject.Inject

class GetAllExpansesUseCase @Inject constructor(
    val repo: ExpanseRepository
) {

    suspend operator fun invoke(): Resource<List<Expense>> {
        return try {
            Resource.Success(
                repo.getAll().map { expanse ->
                    expanse.toExpanse()
                })
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error: Can't get Expanse")
        }

    }
}

