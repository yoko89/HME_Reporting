package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.daily_allowance.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neklaway.hme_reporting.common.data.entity.AllowanceType
import com.neklaway.hme_reporting.common.domain.model.TimeSheet
import com.neklaway.hme_reporting.utils.toDate

@Composable
fun DailyAllowanceItemCard(
    timeSheet: TimeSheet,
    dailyAllowanceChanged: (AllowanceType) -> Unit,
    modifier: Modifier = Modifier
) {

    Card(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth().padding(5.dp),
        verticalAlignment = Alignment.CenterVertically) {
            Text(text = timeSheet.date.toDate(), Modifier.weight(3f))

            RadioButton(
                selected = (timeSheet.dailyAllowance == AllowanceType._24hours),
                onClick = { dailyAllowanceChanged(AllowanceType._24hours) },
                Modifier.weight(1f)
            )
            RadioButton(
                selected = (timeSheet.dailyAllowance == AllowanceType._8hours),
                onClick = { dailyAllowanceChanged(AllowanceType._8hours) },
                Modifier.weight(1f)
            )

            RadioButton(
                selected = (timeSheet.dailyAllowance == AllowanceType.no),
                onClick = { dailyAllowanceChanged(AllowanceType.no) },
                Modifier.weight(1f)
            )

        }
    }
}