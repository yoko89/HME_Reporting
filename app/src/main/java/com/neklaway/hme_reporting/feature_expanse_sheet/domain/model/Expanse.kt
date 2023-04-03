package com.neklaway.hme_reporting.feature_expanse_sheet.domain.model

import android.net.Uri
import com.neklaway.hme_reporting.common.data.entity.ExpanseEntity
import com.neklaway.hme_reporting.common.domain.model.TimeSheet
import com.neklaway.hme_reporting.utils.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.util.*

@Serializable
data class Expanse(
    val HMEId: Long,
    @Serializable(with = CalendarAsLongSerializer::class)
    val date: Calendar,
    val invoiceNumber: String,
    val description: String,
    val personallyPaid: Boolean,
    val amount: Float,
    val currencyID: Long,
    val amountAED: Float,
   val invoicesUri: List<String> = emptyList(),
    val id: Long? = null
){
    companion object {
        val listSerializer: KSerializer<List<Expanse>> = ListSerializer(serializer())
    }
}

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
        invoicesUri =  invoicesUri,
        id = id
    )
}
