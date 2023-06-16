package com.neklaway.hme_reporting.common.presentation.common.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.model.IBAUCode
import com.neklaway.hme_reporting.feature_car_mileage.domain.model.CarMileage
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.CurrencyExchange
import com.neklaway.hme_reporting.utils.toDate
import java.util.Calendar

@Composable
fun <T> DeleteDialog(
    item: T,
    onConfirm: (T) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = { onConfirm(item) }) {
                Text(text = "Delete")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
        title = {
            Text(text = "Unrecoverable Delete")
        },
        text = {
            when (item) {
                is Calendar -> {
                    Text(text = "Delete ${item.toDate()}?")
                }

                is HMECode -> {
                    Text(text = "Delete ${item.code} and all it's related TimeSheets?")
                }

                is IBAUCode -> {
                    Text(text = "Delete ${item.code} and all it's related TimeSheets?")
                }

                is Customer -> {
                    Text(text = "Delete ${item.name} and all it's related Missions?")
                }

                is CarMileage -> {
                    Text(text = "Delete Mileage entry for day ${item.startDate.toDate()}?")
                }

                is CurrencyExchange -> {
                    Text(text = "Delete ${item.currencyName}?")

                }

                else -> {
                    Text(text = "Delete ${item.toString()}? ")
                }
            }
        })
}

@Preview
@Composable
fun DeletePreview() {
    DeleteDialog(item = HMECode(
        customerId = 123,
        code = "M23-00700",
        machineNumber = "123",
        machineType = "RotoPacker",
        workDescription = "Description"
    ), onConfirm = {},
        onDismiss = {})
}