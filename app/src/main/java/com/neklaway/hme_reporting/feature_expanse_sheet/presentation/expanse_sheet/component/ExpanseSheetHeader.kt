package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.expanse_sheet.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ExpanseSheetHeader(
    modifier: Modifier = Modifier
) {


    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {



        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Date",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(2f)
            )
            Text(
                text = "Invoice",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(2f)
            )

            Text(
                text = "Cash",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

        }

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row {
            Text(
                text = "Amount",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "Currency",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "Amount AED",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )


        }
    }
}