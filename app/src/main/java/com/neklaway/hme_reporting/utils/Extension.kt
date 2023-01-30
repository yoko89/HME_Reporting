package com.neklaway.hme_reporting.utils

import java.text.DateFormat
import java.util.*

fun Long.toCalender(): Calendar {
    val cal = Calendar.getInstance()
    cal.timeInMillis = this
    return cal
}

fun Calendar?.toDate(): String {
    val formatter = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault())

    this?.let {
        return try {
            formatter.format(it.timeInMillis)
        } catch (e: Exception) {
            e.printStackTrace()
            "ERROR"
        }
    }
    return "N/A"

}


fun Calendar?.toTime(): String {
    val formatter = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())

    this?.let {
        return try {
            formatter.format(it.timeInMillis)
        } catch (e: Exception) {
            e.printStackTrace()
            "ERROR"
        }
    }
    return "N/A"
}
