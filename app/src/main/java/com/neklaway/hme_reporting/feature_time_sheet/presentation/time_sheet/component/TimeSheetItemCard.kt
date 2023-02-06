package com.neklaway.hme_reporting.feature_time_sheet.presentation.time_sheet.component

import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.neklaway.hme_reporting.common.domain.model.TimeSheet
import com.neklaway.hme_reporting.utils.toDate
import com.neklaway.hme_reporting.utils.toTime

@Composable
fun TimeSheetItemCard(
    timeSheet: TimeSheet,
    cardClicked: () -> Unit,
    onCheckedChanged: (checked: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val createdCardColor = animateColorAsState(
        targetValue = if (timeSheet.created) MaterialTheme.colorScheme.inverseSurface
        else MaterialTheme.colorScheme.surfaceVariant
    )

    val infiniteTransition = rememberInfiniteTransition()
    val cardColor = if (timeSheet.overLap) infiniteTransition.animateColor(
        initialValue = createdCardColor.value,
        targetValue = Color.Yellow,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 500, delayMillis = 1000, easing = LinearEasing) , repeatMode = RepeatMode.Reverse)
    )
    else createdCardColor


    Card(
        modifier = modifier
            .padding(5.dp)
            .clickable {
                cardClicked()
            },
        colors = CardDefaults.cardColors(containerColor = cardColor.value),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 5.dp)
        ) {

            Checkbox(
                checked = timeSheet.selected,
                onCheckedChange = { checked ->
                    onCheckedChanged(checked)
                })

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = timeSheet.date.toDate(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    Text(
                        text = timeSheet.travelStart.toTime(),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = timeSheet.workStart.toTime(),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = timeSheet.workEnd.toTime(),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = timeSheet.travelEnd.toTime(),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                    )

                    Text(
                        text = timeSheet.traveledDistance.toString(),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                    )
                }

                Row {
                    Text(
                        text = timeSheet.travelTimeString,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = timeSheet.workTimeString,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = timeSheet.overTimeString,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = timeSheet.breakTimeString,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                    )
                }
            }
        }
    }
}