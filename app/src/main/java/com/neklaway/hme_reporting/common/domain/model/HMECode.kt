package com.neklaway.hme_reporting.common.domain.model

import com.neklaway.hme_reporting.common.data.entity.Accommodation
import com.neklaway.hme_reporting.common.data.entity.HMECodeEntity
import com.neklaway.hme_reporting.utils.CalendarAsLongSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class HMECode(
    val customerId: Long,
    val code: String,
    val machineType: String?,
    val machineNumber: String?,
    val workDescription: String?,
    val fileNumber: Int = 0,
    val expenseNumber: Int = 0,
    val signerName: String? = null,
    @Serializable(with = CalendarAsLongSerializer::class)
    val signatureDate: Calendar? = null,
    val accommodation: Accommodation? = null,
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
    return HMECodeEntity(
        id = id,
        customerId = customerId,
        code = code,
        machineType = machineType,
        machineNumber = machineNumber,
        workDescription = workDescription,
        fileNumber = fileNumber,
        expanseNumber = expenseNumber,
        signerName = signerName,
        signatureDate = signatureDate?.timeInMillis,
        accommodation = accommodation
    )
}