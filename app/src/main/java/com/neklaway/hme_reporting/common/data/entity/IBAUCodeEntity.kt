package com.neklaway.hme_reporting.common.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.neklaway.hme_reporting.common.domain.model.IBAUCode

@Entity(
    indices = [Index(value = ["code"], unique = true), Index(value = ["HMEId"])],
    foreignKeys = [ForeignKey(
        entity = HMECodeEntity::class,
        parentColumns = ["id"],
        childColumns = ["HMEId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    tableName = "ibauCodeTable"
)
data class IBAUCodeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    val HMEId: Long,
    val code: String,
    val machineType: String,
    val machineNumber: String,
    val workDescription: String,
)

fun IBAUCodeEntity.toIBAUCode(): IBAUCode {
    return IBAUCode(HMEId, code,machineType,machineNumber,workDescription,id)
}