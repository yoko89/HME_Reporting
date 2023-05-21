package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.edit_expanse

import android.content.Context
import android.net.Uri
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.CurrencyExchange

sealed class EditExpanseUserEvent {
    object PhotoTaken : EditExpanseUserEvent()
    class PhotoPicked(val context: Context, val uri: Uri?) : EditExpanseUserEvent()
    object DateClicked : EditExpanseUserEvent()
    class DatePicked(val year: Int, val month: Int, val day: Int) : EditExpanseUserEvent()
    object DateShown : EditExpanseUserEvent()
    object DeleteExpanse : EditExpanseUserEvent()
    object UpdateExpanse : EditExpanseUserEvent()
    class TakePicture(val context: Context) : EditExpanseUserEvent()
    object PickPicture : EditExpanseUserEvent()
    class InvoiceNumberChanged(val number: String) : EditExpanseUserEvent()
    class DescriptionChanged(val description: String) : EditExpanseUserEvent()
    class CashCheckChanged(val checked: Boolean) : EditExpanseUserEvent()
    class AmountChanged(val amount: String) : EditExpanseUserEvent()
    class CurrencySelected(val currencyExchange: CurrencyExchange) : EditExpanseUserEvent()
    class AmountAEDChanged(val amount: String) : EditExpanseUserEvent()
    class DeleteImage(val uri: Uri) : EditExpanseUserEvent()
}
