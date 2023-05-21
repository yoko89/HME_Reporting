package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.new_expanse

import android.content.Context
import android.net.Uri
import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.CurrencyExchange

sealed class NewExpanseUserEvent {
    object PhotoTaken : NewExpanseUserEvent()
    class PhotoPicked(val context: Context,val uri:Uri?):NewExpanseUserEvent()
    object DateClicked:NewExpanseUserEvent()
    class DatePicked(val year:Int,val month:Int,val day:Int):NewExpanseUserEvent()
    object DatePickedCanceled:NewExpanseUserEvent()
    class TakePicture(val context: Context):NewExpanseUserEvent()
    object PickPicture:NewExpanseUserEvent()
    object InsertExpanse:NewExpanseUserEvent()
    class CustomerSelected(val customer: Customer):NewExpanseUserEvent()
    class HmeSelected(val hmeCode: HMECode):NewExpanseUserEvent()
    class InvoiceNumberChanged(val number:String):NewExpanseUserEvent()
    class DescriptionChanged(val description:String):NewExpanseUserEvent()
    class CashCheckChanged(val checked:Boolean):NewExpanseUserEvent()
    class AmountChanged(val amount:String):NewExpanseUserEvent()
    class CurrencySelected(val currencyExchange: CurrencyExchange):NewExpanseUserEvent()
    class AmountAEDChanged(val amount: String):NewExpanseUserEvent()
    class DeleteImage(val uri: Uri):NewExpanseUserEvent()
}
