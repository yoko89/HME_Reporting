package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.daily_allowance.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DailyAllowanceHeader(modifier: Modifier = Modifier) {

    Row(modifier = modifier.fillMaxWidth().padding(5.dp)) {
        Text(text = "Date", Modifier.weight(3f))

        Text(text = "24H", Modifier.weight(1f))
        Text(text = "8H", Modifier.weight(1f))
        Text(text = "no", Modifier.weight(1f))
    }

}