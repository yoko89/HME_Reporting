package com.neklaway.hme_reporting.common.presentation.common.component

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.util.*

@Composable
fun CustomDatePicker(
    year: Int?,
    month: Int?,
    day: Int?,
    dateSet: (year: Int, month: Int, day: Int) -> Unit,
    canceled: () -> Unit
) {
    val today = Calendar.getInstance()
    val context = LocalContext.current

    val datePicker = DatePickerDialog(
        context,
        { _: DatePicker, _year: Int, _month: Int, _day: Int ->
            dateSet(_year, _month, _day)
        },
        year ?: today.get(Calendar.YEAR),
        month ?: today.get(Calendar.MONTH),
        day ?: today.get(Calendar.DAY_OF_MONTH)
    )
    datePicker.setCanceledOnTouchOutside(false)
    datePicker.setOnCancelListener {
        canceled()
    }

    datePicker.show()
}