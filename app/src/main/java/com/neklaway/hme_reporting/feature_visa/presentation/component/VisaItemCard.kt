package com.neklaway.hme_reporting.feature_visa.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.neklaway.hme_reporting.common.domain.model.Visa
import com.neklaway.hme_reporting.utils.toDate
import java.time.temporal.ChronoUnit
import java.util.*

@Composable
fun VisaItemCard(
    visa: Visa,
    cardClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onCheckedChanged: (checked: Boolean) -> Unit,
    visaReminderWarning: Int,
    modifier: Modifier = Modifier
) {

    val currentDay = Calendar.getInstance()
    currentDay.set(Calendar.HOUR_OF_DAY, 0)
    currentDay.set(Calendar.MINUTE, 0)
    currentDay.set(Calendar.SECOND, 0)
    currentDay.set(Calendar.MILLISECOND, 0)

    val days = ChronoUnit.DAYS.between(currentDay.toInstant(), visa.date.toInstant())

    Card(
        modifier = modifier
            .padding(5.dp)
            .clickable {
                cardClicked()
            },
        colors = CardDefaults.cardColors(
            containerColor =
            if (days < 0) Color(red = 255, green = 0, blue = 0, alpha = 150)
            else if (days < visaReminderWarning) Color.Yellow
            else Color.Green
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 5.dp)
        ) {

            Checkbox(
                checked = visa.selected,
                onCheckedChange = { checked ->
                    onCheckedChanged(checked)
                })

            Text(
                text = visa.country,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = visa.date.toDate(),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            OutlinedIconButton(
                onClick = {
                    onDeleteClicked()
                },
                modifier = Modifier
                    .weight(0.15f)
                    .padding(all = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Visa",
                    tint = Color.Red,
                    modifier = Modifier.alpha(1f)
                )
            }
        }

    }
}