package com.neklaway.hme_reporting.common.presentation.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import java.util.Calendar

//@Composable
//fun CustomTimePicker(
//    hour: Int?,
//    minute: Int?,
//    timeSet: (hour: Int, minute: Int) -> Unit,
//    canceled: () -> Unit
//) {
//    val today = Calendar.getInstance()
//    val context = LocalContext.current
//
//    val is24H = DateFormat.is24HourFormat(context)
//
//    val timePicker = TimePickerDialog(
//        context,
//        { _, _hour: Int, _minute: Int ->
//            timeSet(_hour, _minute)
//        },
//        hour ?: today.get(Calendar.HOUR_OF_DAY),
//        minute ?: today.get(Calendar.MINUTE),
//        is24H
//    )
//    timePicker.setCanceledOnTouchOutside(false)
//    timePicker.setOnCancelListener {
//        canceled()
//    }
//    timePicker.show()
//}
//

@Composable
fun CustomTimePicker(
    hour: Int?,
    minute: Int?,
    timeSet: (hour: Int, minute: Int) -> Unit,
    canceled: () -> Unit
) {
    val today = Calendar.getInstance()

    val state = rememberTimePickerState(
        initialHour = hour ?: today.get(Calendar.HOUR_OF_DAY),
        initialMinute = minute ?: today.get(Calendar.MINUTE)
    )

    AlertDialog(
        onDismissRequest = {},
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.background,
                RoundedCornerShape(5.dp)
            )
            .padding(8.dp),
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column {
            TimePicker(state = state)

            Row(modifier = Modifier.align(Alignment.End)) {
                TextButton(onClick = canceled) {
                    Text(text = "CANCEL")
                }
                Spacer(modifier = Modifier.width(5.dp))
                TextButton(onClick = { timeSet(state.hour, state.minute) }) {
                    Text(text = "OK")

                }
            }
        }
    }
}