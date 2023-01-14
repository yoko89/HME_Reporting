package com.neklaway.hme_reporting.common.domain.visa_use_cases

import com.neklaway.hme_reporting.common.data.entity.toVisa
import com.neklaway.hme_reporting.common.domain.model.Visa
import com.neklaway.hme_reporting.common.domain.repository.VisaRepository
import com.neklaway.hme_reporting.utils.Resource
import java.io.IOException
import javax.inject.Inject

class GetAllVisasUseCase @Inject constructor(
    val repo: VisaRepository
) {

    suspend operator fun invoke(): Resource<List<Visa>> {

        return try {
            Resource.Success(repo.getAll().map { visaEntity ->
                visaEntity.toVisa()
            })
        } catch (e: IOException) {
            Resource.Error(e.message ?: "Can't get Visa List")
        }
    }

}


