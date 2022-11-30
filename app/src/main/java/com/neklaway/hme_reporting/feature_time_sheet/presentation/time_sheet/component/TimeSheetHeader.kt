package com.neklaway.hme_reporting.feature_time_sheet.presentation.time_sheet.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun TimeSheetHeader(
    selectAll:Boolean,
    onSelectAllChecked: (checked: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Checkbox(
            checked = selectAll,
            onCheckedChange = { checked ->
                onSelectAllChecked(checked)
            })

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Travel\nStart",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Work\nStart",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Work\nEnd",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Travel\nEnd",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Travel\nDist.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

            }

            Divider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row {
                Text(
                    text = "Travel\nTime",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Work\nTime",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Over\nTime",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Break\nTime",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}