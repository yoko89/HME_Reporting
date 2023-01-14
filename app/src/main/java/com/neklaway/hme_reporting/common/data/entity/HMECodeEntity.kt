package com.neklaway.hme_reporting.common.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.utils.toCalender

@Entity(
    indices = [Index(value = ["code"], unique = true), Index(value = ["customerId"])],
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
    val fileNumber:Int = 0,
    val signerName:String?,
    val signatureDate: Long?
)


fun HMECodeEntity.toHMECode(): HMECode {
    return HMECode(customerId, code, machineType, machineNumber, workDescription,fileNumber,signerName,signatureDate?.toCalender(),id)
}