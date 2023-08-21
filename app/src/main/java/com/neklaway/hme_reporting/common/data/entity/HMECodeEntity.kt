package com.neklaway.hme_reporting.common.data.entity

import androidx.room.*
import androidx.room.ForeignKey.Companion.CASCADE
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.utils.toCalender

@Entity(
    indices = [Index(value = ["code"], unique = true), Index(
        value = ["customerId"],
        unique = false
    )],
    foreignKeys = [ForeignKey(
        entity = CustomerEntity::class,
        parentColumns = ["id"],
        childColumns = ["customerId"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )],
    tableName = "hmeCodeTable"
)
data class HMECodeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val customerId: Long,
    val code: String,
    val machineType: String?,
    val machineNumber: String?,
    val workDescription: String?,
    val fileNumber: Int = 0,
    @ColumnInfo(defaultValue = "0")
    val expanseNumber: Int = 0,
    val signerName: String?,
    val signatureDate: Long?,
    val accommodation: Accommodation? = null,
)


fun HMECodeEntity.toHMECode(): HMECode {
    return HMECode(
        customerId = customerId,
        code = code,
        machineType = machineType,
        machineNumber = machineNumber,
        workDescription = workDescription,
        fileNumber = fileNumber,
        expenseNumber = expanseNumber,
        signerName = signerName,
        signatureDate = signatureDate?.toCalender(),
        accommodation = accommodation,
        id = id
    )
}

enum class Accommodation {
    CompanyCC, Cash
}