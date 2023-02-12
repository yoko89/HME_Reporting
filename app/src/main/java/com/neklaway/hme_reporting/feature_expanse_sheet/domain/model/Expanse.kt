package com.neklaway.hme_reporting.feature_expanse_sheet.domain.model

import android.net.Uri
import com.neklaway.hme_reporting.common.data.entity.ExpanseEntity
import java.util.*


data class Expanse(
    val HMEId: Long,
    val date: Calendar,
    val invoiceNumber: String,
    val description: String,
    val personallyPaid: Boolean,
    val amount: Float,
    val currencyID: Long,
    val amountAED: Float,
    val invoiceUris: List<Uri> = emptyList(),
    val id: Long?
)

fun Expanse.toExpansesEntity(): ExpanseEntity {
    return ExpanseEntity(
        HMEId = HMEId,
        date = date.timeInMillis,
        invoiceNumber = invoiceNumber,
        description = description,
        personallyPaid = personallyPaid,
        amount = amount,
        currencyID = currencyID,
        amountAED = amountAED,
        invoiceUris = invoiceUris,
        id = id
    )
}
