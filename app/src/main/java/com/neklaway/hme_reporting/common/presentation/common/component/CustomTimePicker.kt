package com.neklaway.hme_reporting.common.presentation.common.component

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.neklaway.hme_reporting.utils.Resource
import com.neklaway.hmereporting.R
import java.util.*

@Composable
fun CustomTimePicker(
    hour: Int?,
    minute: Int?,
    timeSet: (hour: Int, minute: Int) -> Unit,
    canceled: () -> Unit
) {
    val today = Calendar.getInstance()
    val context = LocalContext.current

    val is24H = DateFormat.is24HourFormat(context)

    val timePicker = TimePickerDialog(
        context,
        { _, _hour: Int, _minute: Int ->
            timeSet(_hour, _minute)
        },
        hour ?: today.get(Calendar.HOUR_OF_DAY),
        minute ?: today.get(Calendar.MINUTE),
        is24H
    )
    timePicker.setCanceledOnTouchOutside(false)
    timePicker.setOnCancelListener {
        canceled()
    }
    timePicker.show()
}