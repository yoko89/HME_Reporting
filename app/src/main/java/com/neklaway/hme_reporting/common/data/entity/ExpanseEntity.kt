package com.neklaway.hme_reporting.common.data.entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.Expanse
import com.neklaway.hme_reporting.utils.toCalender


@Entity(
    indices = [Index(value = ["HMEId"])],
    foreignKeys = [ForeignKey(
        entity = HMECodeEntity::class,
        parentColumns = ["id"],
        childColumns = ["HMEId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    ),
        ForeignKey(
            entity = CurrencyExchangeEntity::class,
            parentColumns = ["id"],
            childColumns = ["currencyID"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )],
    tableName = "expansesTable"
)
data class ExpanseEntity(
    val HMEId: Long,
    val date: Long,
    val invoiceNumber: String,
    val description: String,
    val personallyPaid: Boolean,
    val amount: Float,
    val currencyID: Long,
    val amountAED: Float,
    val invoiceUris: List<Uri> = emptyList(),
    @PrimaryKey(autoGenerate = true)
    val id: Long?
)

fun ExpanseEntity.toExpanse(): Expanse {
    return Expanse(
        HMEId = HMEId,
        date = date.toCalender(),
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
