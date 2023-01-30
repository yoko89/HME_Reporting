package com.neklaway.hme_reporting.common.domain.visa_use_cases

import android.database.sqlite.SQLiteConstraintException
import com.neklaway.hme_reporting.common.domain.model.Visa
import com.neklaway.hme_reporting.common.domain.model.toVisaEntity
import com.neklaway.hme_reporting.common.domain.repository.VisaRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

class InsertVisaUseCase @Inject constructor(
    val repo: VisaRepository
) {

    operator fun invoke(country: String, date: Calendar?): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        if (country.trim().isBlank()) {
            emit(Resource.Error("Visa Country can't be blank"))
        } else if (date == null) {
            emit(Resource.Error("Visa Date can't be blank"))

        } else {
            try {
                val visa = Visa(country.trim(), date)
                val result = repo.insert(visa.toVisaEntity())
                if (result > 0) {
                    emit(Resource.Success(true))
                } else {
                    emit(Resource.Error("Error: Can't insert Visa"))
                }
            } catch (e: SQLiteConstraintException) {
                e.printStackTrace()
                emit(Resource.Error(e.message ?: "Error: Can't Insert Visa"))
            }
        }
    }
}