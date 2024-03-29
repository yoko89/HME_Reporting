package com.neklaway.hme_reporting.utils

import android.net.Uri
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*


object CalendarAsLongSerializer : KSerializer<Calendar> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Calendar", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Calendar) {
        val mills = value.timeInMillis
        encoder.encodeLong(mills)
    }

    override fun deserialize(decoder: Decoder): Calendar {
        val mills = decoder.decodeLong()
        return mills.toCalender()
    }
}