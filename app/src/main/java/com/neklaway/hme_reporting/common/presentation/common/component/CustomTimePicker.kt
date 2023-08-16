package com.neklaway.hme_reporting.common.presentation.common.component

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import java.util.Calendar

private const val TAG = "TimePocker"

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
        dismissButton = {
            TextButton(onClick = canceled) {
                Text(text = "CANCEL")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                timeSet(state.hour, state.minute)
                Log.d(TAG, "CustomTimePicker: ${state.hour} : ${state.minute}")
            }) {
                Text(text = "OK")
            }
        },
        text = {
            Column {
                Text(
                    text = "Select time",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(5.dp))
                TimePicker(state = state)
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    )
}