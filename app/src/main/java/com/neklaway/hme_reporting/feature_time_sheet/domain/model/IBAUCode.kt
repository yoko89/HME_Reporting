package com.neklaway.hme_reporting.feature_time_sheet.domain.model

import com.neklaway.hme_reporting.feature_time_sheet.data.entity.IBAUCodeEntity
import kotlinx.serialization.Serializable

@Serializable
data class IBAUCode(
    val HMEId: Long,
    val code: String,
    val machineType: String,
    val machineNumber: String,
    val workDescription: String,
    val id: Long?,
    ) {
    override fun toString(): String {
        return code
    }
}

fun IBAUCode.toIBAUCodeEntity(): IBAUCodeEntity {
    return IBAUCodeEntity(id, HMEId, code, machineType, machineNumber, workDescription)
}