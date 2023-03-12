package com.neklaway.hme_reporting.utils

import android.annotation.SuppressLint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun Long.toCalender(): Calendar {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Dubai"))
    cal.timeInMillis = this
    return cal
}

fun Calendar?.toDate(): String {
    val formatter = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault())
formatter.timeZone = TimeZone.getTimeZone("Asia/Dubai")
    this?.let {
        return try {
            formatter.format(it.timeInMillis)
        } catch (e: Exception) {
            e.printStackTrace()
            "ERROR"
        }
    }
    return "---"

}
@SuppressLint("SimpleDateFormat")
fun Calendar?.toStdDate(): String {
    val formatter = SimpleDateFormat("dd MMM yyyy")
    formatter.timeZone = TimeZone.getTimeZone("Asia/Dubai")

    this?.let {
        return try {
            formatter.format(it.timeInMillis)
        } catch (e: Exception) {
            e.printStackTrace()
            "ERROR"
        }
    }
    return "---"

}


fun Calendar?.toTime(): String {
    val formatter = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("Asia/Dubai")

    this?.let {
        return try {
            formatter.format(it.timeInMillis)
        } catch (e: Exception) {
            e.printStackTrace()
            "ERROR"
        }
    }
    return "---"
}

@SuppressLint("SimpleDateFormat")
fun Calendar?.toTime24(): String {
    val formatter = SimpleDateFormat("HH:mm")
    formatter.timeZone = TimeZone.getTimeZone("Asia/Dubai")

    this?.let {
        return try {
            formatter.format(it.timeInMillis)
        } catch (e: Exception) {
            e.printStackTrace()
            "ERROR"
        }
    }
    return "---"
}


fun String.toFloatWithString(): ResourceWithString<Float> {
    val string = this
    val float: Float?

    try {
        float = this.toFloat()

    } catch (e: NumberFormatException) {
        e.printStackTrace()
        return if (string.isNotBlank()) {
            ResourceWithString.Error(message = "Error " + e.message, string = "")
        } else {
            ResourceWithString.Success(0f, "")
        }
    }

    return ResourceWithString.Success(float, string)
}


fun String.toIntWithString(): ResourceWithString<Int> {
    val string = this
    val int: Int?

    try {
        int = this.toInt()

    } catch (e: NumberFormatException) {
        e.printStackTrace()
        return if (string.isNotBlank()) {
            ResourceWithString.Error(message = "Error " + e.message, string = "")
        } else {
            ResourceWithString.Success(0, "")
        }
    }

    return ResourceWithString.Success(int, string)
}
