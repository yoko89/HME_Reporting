package com.neklaway.hme_reporting.feature_time_sheet.presentation.new_time_sheet.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.neklaway.hme_reporting.common.domain.model.CarMileage
import com.neklaway.hme_reporting.utils.toDate
import com.neklaway.hme_reporting.utils.toTime
import java.util.*

@Composable
fun CarMileageItemCard(
    carMileage: CarMileage,
    cardClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier
            .padding(5.dp)
            .fillMaxWidth()
            .clickable {
                cardClicked()
            },
    ) {
        Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = carMileage.startDate.toDate(),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = carMileage.startTime.toTime(),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = carMileage.startMileage.toString(),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = carMileage.endDate.toDate(),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = carMileage.endTime.toTime(),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = carMileage.endMileage.toString(),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            OutlinedIconButton(
                onClick = onDeleteClicked,
                modifier = Modifier
                    .weight(0.15f)
                    .padding(all = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Car Mileage",
                    tint = Color.Red,
                    modifier = Modifier.alpha(1f)
                )
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    CarMileageItemCard(
        carMileage = CarMileage(
            Calendar.getInstance(),
            Calendar.getInstance(),
            1000,
            Calendar.getInstance(),
            Calendar.getInstance(),
            1000
        ), cardClicked = {  }, onDeleteClicked = {  })
}