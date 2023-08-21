package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.new_expense

import android.content.Context
import android.net.Uri
import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.CurrencyExchange

sealed class NewExpenseUserEvent {
    object PhotoTaken : NewExpenseUserEvent()
    class PhotoPicked(val context: Context,val uri:Uri?):NewExpenseUserEvent()
    object DateClicked:NewExpenseUserEvent()
    class DatePicked(val year:Int,val month:Int,val day:Int):NewExpenseUserEvent()
    object DatePickedCanceled:NewExpenseUserEvent()
    class TakePicture(val context: Context):NewExpenseUserEvent()
    object PickPicture:NewExpenseUserEvent()
    object InsertExpense:NewExpenseUserEvent()
    class CustomerSelected(val customer: Customer):NewExpenseUserEvent()
    class HmeSelected(val hmeCode: HMECode):NewExpenseUserEvent()
    class InvoiceNumberChanged(val number:String):NewExpenseUserEvent()
    class DescriptionChanged(val description:String):NewExpenseUserEvent()
    class CashCheckChanged(val checked:Boolean):NewExpenseUserEvent()
    class AmountChanged(val amount:String):NewExpenseUserEvent()
    class CurrencySelected(val currencyExchange: CurrencyExchange):NewExpenseUserEvent()
    class AmountAEDChanged(val amount: String):NewExpenseUserEvent()
    class DeleteImage(val uri: Uri):NewExpenseUserEvent()
}
