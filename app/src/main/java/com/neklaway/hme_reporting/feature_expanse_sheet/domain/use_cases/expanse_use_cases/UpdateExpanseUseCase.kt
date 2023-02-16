package com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.expanse_use_cases

import android.database.sqlite.SQLiteConstraintException
import android.net.Uri
import android.util.Log
import com.neklaway.hme_reporting.common.domain.repository.ExpanseRepository
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.Expanse
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.toExpansesEntity
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

private const val TAG = "UpdateExpanseUseCase"

class UpdateExpanseUseCase @Inject constructor(
    val repo: ExpanseRepository
) {

    operator fun invoke(
        HMEId: Long?,
        date: Calendar?,
        invoiceNumber: String,
        description: String,
        personallyPaid: Boolean,
        amount: Float?,
        currencyID: Long?,
        amountAED: Float?,
        invoiceUris: List<String> = emptyList(),
        id: Long?
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        if (HMEId == null) {
            emit(Resource.Error("HME Code can't be null"))
            return@flow
        }
        if (date == null) {
            emit(Resource.Error("Date can't be null"))
            return@flow
        }
        if (invoiceNumber.trim().isBlank()) {
            emit(Resource.Error("Invoice number can't be blank"))
            return@flow
        }
        if (description.trim().isBlank()) {
            emit(Resource.Error("Description can't be blank"))
            return@flow
        }
        if (amount == null) {
            emit(Resource.Error("Amount can't be null"))
            return@flow
        }
        if (amount <= 0) {
            emit(Resource.Error("Amount can't be negative"))
            return@flow
        }
        if (currencyID == null) {
            emit(Resource.Error("Currency can't be null"))
            return@flow
        }
        if (amountAED == null) {
            emit(Resource.Error("Amount in AED can't be null"))
            return@flow
        }
        if (amountAED <= 0) {
            emit(Resource.Error("Amount in AED can't be negative"))
            return@flow
        }
        if (id ==null){
            emit(Resource.Error("ID can't be null"))
            return@flow
        }

        try {
            val expanse = Expanse(
                HMEId,
                date,
                invoiceNumber,
                description,
                personallyPaid,
                amount,
                currencyID,
                amountAED,
                invoiceUris,
                id
            )
            val result = repo.update(expanse.toExpansesEntity())
            if (result > 0) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("Error: Can't Update Expanse"))
                Log.d(TAG, "invoke: error $result")
            }
        } catch (e: SQLiteConstraintException) {
            e.printStackTrace()
            emit(Resource.Error(e.message ?: "Error: Can't Update Expanse"))
            Log.d(TAG, "invoke: error ${e.message}")
        }
    }
}