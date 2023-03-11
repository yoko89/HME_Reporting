package com.neklaway.hme_reporting.common.domain.model

import com.neklaway.hme_reporting.common.data.entity.HMECodeEntity
import com.neklaway.hme_reporting.utils.DateAsLongSerializer
import kotlinx.serialization.Serializable
import java.util.Calendar

@Serializable
data class HMECode(
    val customerId: Long,
    val code: String,
    val machineType: String?,
    val machineNumber: String?,
    val workDescription: String?,
    val fileNumber : Int = 0,
    val signerName : String? = null,
    @Serializable(with = DateAsLongSerializer::class)
    val signatureDate: Calendar? = null,
    val id: Long? = null
) {
    override fun toString(): String {
        var hmeString = ""
        hmeString += code
        if (machineType?.isNotBlank() == true) {
            hmeString += "\nMachine Type: $machineType"
        }
        if (machineNumber?.isNotBlank() == true) {
            hmeString += " Machine Number: $machineNumber"
        }
        if (workDescription?.isNotBlank() == true) {
            hmeString += "\nWork Description: $workDescription"
        }

        return hmeString
    }
}


fun HMECode.toHMECodeEntity(): HMECodeEntity {
    return HMECodeEntity(id, customerId, code, machineType, machineNumber, workDescription,fileNumber,signerName,signatureDate?.timeInMillis)
}