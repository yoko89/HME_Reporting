package com.neklaway.hme_reporting.common.presentation.common.component

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import java.util.Calendar

//@Composable
//fun CustomDatePicker(
//    year: Int?,
//    month: Int?,
//    day: Int?,
//    dateSet: (year: Int, month: Int, day: Int) -> Unit,
//    canceled: () -> Unit
//) {
//    val today = Calendar.getInstance()
//    val context = LocalContext.current
//
//    val datePicker = DatePickerDialog(
//        context,
//        { _: DatePicker, _year: Int, _month: Int, _day: Int ->
//            dateSet(_year, _month, _day)
//        },
//        year ?: today.get(Calendar.YEAR),
//        month ?: today.get(Calendar.MONTH),
//        day ?: today.get(Calendar.DAY_OF_MONTH)
//    )
//    datePicker.setCanceledOnTouchOutside(false)
//    datePicker.setOnCancelListener {
//        canceled()
//    }
//    datePicker.show()
//}

@Composable
fun CustomDatePicker(
    year: Int?,
    month: Int?,
    day: Int?,
    dateSet: (year: Int, month: Int, day: Int) -> Unit,
    canceled: () -> Unit
) {
    val today = Calendar.getInstance()

    year?.let { today.set(Calendar.YEAR, year) }
    month?.let { today.set(Calendar.MONTH, month) }
    day?.let { today.set(Calendar.DAY_OF_MONTH, day) }

    val state = rememberDatePickerState(today.timeInMillis)
    DatePickerDialog(onDismissRequest = {},
        confirmButton = {
            TextButton(onClick = {
                val cal = Calendar.getInstance()
                cal.timeInMillis = state.selectedDateMillis ?: cal.timeInMillis
                dateSet(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                )
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = canceled) {
                Text(text = "CANCEL")
            }
        }) {
        DatePicker(state)
    }
}