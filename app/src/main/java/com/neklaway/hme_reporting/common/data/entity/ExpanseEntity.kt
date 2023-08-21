package com.neklaway.hme_reporting.common.data.entity

import androidx.room.*
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.Expense
import com.neklaway.hme_reporting.utils.toCalender
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json


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
    @ColumnInfo(index = true)
    val currencyID: Long,
    val amountAED: Float,
    val invoicesUri: List<String> = emptyList(),
    @PrimaryKey(autoGenerate = true)
    val id: Long?
)

fun ExpanseEntity.toExpanse(): Expense {
    return Expense(
        HMEId = HMEId,
        date = date.toCalender(),
        invoiceNumber = invoiceNumber,
        description = description,
        personallyPaid = personallyPaid,
        amount = amount,
        currencyID = currencyID,
        amountAED = amountAED,
        invoicesUri = invoicesUri,
        id = id
    )
}

class StringListConverter {
    @TypeConverter
    fun stringToListOfString(value: String): List<String> {
        return Json.decodeFromString(ListSerializer(String.serializer()), value)
    }

    @TypeConverter
    fun listOfStringToString(value: List<String>): String {
        return Json.encodeToString(ListSerializer(String.serializer()), value)
    }
}