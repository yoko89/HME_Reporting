package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.expanse_sheet.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.Expanse
import com.neklaway.hme_reporting.utils.toDate

@Composable
fun ExpanseSheetItemCard(
    expanse: Expanse,
    currencyExchange: State<String>,
    cardClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyName = currencyExchange.value


    Card(
        modifier = modifier
            .padding(5.dp)
            .clickable {
                cardClicked()
            }
    ) {
        Text(
            text = expanse.description,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = expanse.date.toDate(),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(2f)
            )
            Text(
                text = expanse.invoiceNumber,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(2f)
            )

            Checkbox(
                checked = expanse.personallyPaid,
                onCheckedChange = {},
                modifier = Modifier.weight(1f)
            )
        }

        Row {
            Text(
                text = expanse.amount.toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )


            Text(
                text = currencyName,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = expanse.amountAED.toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

        }
    }
}
