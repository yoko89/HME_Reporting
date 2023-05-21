package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.edit_expanse

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neklaway.hme_reporting.common.domain.use_cases.hme_code_use_cases.GetHMECodeByIdUseCase
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.CurrencyExchange
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.Expanse
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.currency_exchange_use_cases.GetAllCurrencyExchangeFlowUseCase
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.currency_exchange_use_cases.GetCurrencyExchangeByIdUseCase
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.expanse_use_cases.DeleteExpanseUseCase
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.expanse_use_cases.GetExpanseByIdUseCase
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.expanse_use_cases.UpdateExpanseUseCase
import com.neklaway.hme_reporting.utils.Resource
import com.neklaway.hme_reporting.utils.ResourceWithString
import com.neklaway.hme_reporting.utils.copyFiles
import com.neklaway.hme_reporting.utils.createUriForInvoice
import com.neklaway.hme_reporting.utils.toFloatWithString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

private const val TAG = "EditExpanseViewModel"

@HiltViewModel
class EditExpanseViewModel @Inject constructor(
    private val updateExpanseUseCase: UpdateExpanseUseCase,
    private val getExpanseByIdUseCase: GetExpanseByIdUseCase,
    private val deleteExpanseUseCase: DeleteExpanseUseCase,
    private val getCurrencyExchangeByIdUseCase: GetCurrencyExchangeByIdUseCase,
    private val getAllCurrencyExchangeFlowUseCase: GetAllCurrencyExchangeFlowUseCase,
    private val getHMECodeByIdUseCase: GetHMECodeByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        const val EXPANSE_ID = "expanse_id"
    }

    private val _state = MutableStateFlow(EditExpanseState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<EditExpanseUiEvents>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val mutableUriList: MutableList<Uri> = mutableListOf()

    private val expanseId: Long
    private lateinit var returnedExpanse: Expanse

    init {
        getCurrencyList()

        expanseId = savedStateHandle[EXPANSE_ID] ?: -1
        _state.update { it.copy(expanseId = expanseId) }
        Log.d(TAG, ": ${state.value.expanseId}")

        viewModelScope.launch(Dispatchers.IO) {
            getExpanseByIdUseCase(expanseId).let { result ->
                Log.d(TAG, "Expanse: $result")
                when (result) {
                    is Resource.Error -> {
                        _uiEvent.send(
                            EditExpanseUiEvents.UserMessage(
                                result.message ?: "Error can't retrieve Data "
                            )
                        )
                        _state.update { it.copy(loading = false) }
                    }

                    is Resource.Loading -> _state.update { it.copy(loading = true) }
                    is Resource.Success -> {
                        Log.d(TAG, "Expanse loaded: Success")
                        val expanse = result.data ?: return@let
                        Log.d(TAG, "expanse is: $expanse")
                        val currency =
                            getCurrencyExchangeByIdUseCase(expanse.currencyID).data
                        _state.update {
                            it.copy(
                                date = expanse.date,
                                invoiceNumber = expanse.invoiceNumber,
                                description = expanse.description,
                                personallyPaid = expanse.personallyPaid,
                                amount = expanse.amount.toString(),
                                amountAED = expanse.amountAED.toString(),
                                invoicesUris = expanse.invoicesUri.map { uriString ->
                                    uriString.toUri()
                                },
                                selectedCurrency = currency,
                                expanseId = expanseId,
                                loading = false
                            )
                        }
                        mutableUriList.addAll(state.value.invoicesUris)
                        returnedExpanse = expanse
                    }
                }
            }
            Log.d(TAG, "returned expanse: $returnedExpanse")
            returnedExpanse
        }
    }


    fun updateExpanse() {
        viewModelScope.launch {
            if (!::returnedExpanse.isInitialized) {
                return@launch
            }

            updateExpanseUseCase.invoke(
                HMEId = returnedExpanse.HMEId,
                date = state.value.date,
                invoiceNumber = state.value.invoiceNumber,
                description = state.value.description,
                personallyPaid = state.value.personallyPaid,
                amount = state.value.amount.toFloat(),
                currencyID = state.value.selectedCurrency?.id,
                amountAED = state.value.amountAED.toFloat(),
                invoiceUris = state.value.invoicesUris.map { it.toString() },
                id = state.value.expanseId,
            ).collect { result ->
                when (result) {
                    is Resource.Error -> {
                        _uiEvent.send(
                            EditExpanseUiEvents.UserMessage(
                                result.message ?: "Error can't update"
                            )
                        )
                        _state.update { it.copy(loading = false) }
                    }

                    is Resource.Loading -> _state.update { it.copy(loading = true) }
                    is Resource.Success -> {
                        _state.update { it.copy(loading = false) }
                        _uiEvent.send(EditExpanseUiEvents.PopBackStack)
                    }
                }

            }
        }
    }

    fun dateClicked() {
        _state.update { it.copy(showDatePicker = true) }
        Log.d(TAG, "dateClicked: ")
    }

    fun datePicked(year: Int, month: Int, day: Int) {
        val date = Calendar.getInstance()
        date.timeZone = TimeZone.getTimeZone("Asia/Dubai")
        date.set(
            year,
            month,
            day,
            0,
            0,
            0
        )
        date.set(Calendar.MILLISECOND, 0)
        _state.update { it.copy(date = date, showDatePicker = false) }
    }

    fun dateShown() {
        _state.update { it.copy(showDatePicker = false) }
    }

    fun deleteExpanse() {
        viewModelScope.launch {


            if (!::returnedExpanse.isInitialized) {
                _uiEvent.send(EditExpanseUiEvents.UserMessage("Can't retrieve Expanse"))
                return@launch
            }

            deleteExpanseUseCase(returnedExpanse).collect { result ->
                when (result) {
                    is Resource.Error -> {
                        _uiEvent.send(
                            EditExpanseUiEvents.UserMessage(
                                result.message ?: "Error can't delete Expanse"
                            )
                        )
                        _state.update { it.copy(loading = false) }
                    }

                    is Resource.Loading -> _state.update { it.copy(loading = true) }
                    is Resource.Success -> {
                        _uiEvent.send(EditExpanseUiEvents.UserMessage("Expanse Deleted"))
                        _uiEvent.send(EditExpanseUiEvents.PopBackStack)
                        _state.update { it.copy(loading = false) }

                    }
                }
            }
        }
    }


    fun currencySelected(currencyExchange: CurrencyExchange) {
        viewModelScope.launch {
            _state.update { it.copy(selectedCurrency = currencyExchange) }
            calculateAmountInAED()
        }

    }

    private suspend fun calculateAmountInAED() {
        val amountInAED = state.value.amount.toFloatWithString().let { resource ->
            when (resource) {
                is ResourceWithString.Error -> {
                    _uiEvent.send(EditExpanseUiEvents.UserMessage(resource.message ?: "Error"))
                    ""
                }

                is ResourceWithString.Loading -> ""
                is ResourceWithString.Success -> {
                    resource.data?.let { amount ->
                        (amount.times(state.value.selectedCurrency?.rate ?: 0f).toString())
                    } ?: ""
                }
            }
        }
        _state.update { it.copy(amountAED = amountInAED) }
    }

    fun invoiceNumberChanged(invoiceNumber: String) {
        _state.update { it.copy(invoiceNumber = invoiceNumber) }
    }

    fun descriptionChanged(description: String) {
        _state.update { it.copy(description = description) }
    }

    fun cashCheckChanged(cash: Boolean) {
        _state.update { it.copy(personallyPaid = cash) }
    }

    fun amountChanged(amount: String) {
        amount.toFloatWithString().let { resourceWithString ->
            when (resourceWithString) {
                is ResourceWithString.Error -> {
                    viewModelScope.launch {

                        _uiEvent.send(
                            EditExpanseUiEvents.UserMessage(
                                resourceWithString.message ?: "Error in Amount"
                            )
                        )
                    }
                    _state.update { it.copy(amount = resourceWithString.string ?: "") }
                }

                is ResourceWithString.Loading -> Unit
                is ResourceWithString.Success -> {
                    _state.update { it.copy(amount = resourceWithString.string ?: "") }
                    viewModelScope.launch {
                        calculateAmountInAED()
                    }
                }
            }
        }
    }

    fun amountAEDChanged(amount: String) {
        amount.toFloatWithString().let { resourceWithString ->
            when (resourceWithString) {
                is ResourceWithString.Error -> {
                    viewModelScope.launch {
                        _uiEvent.send(
                            EditExpanseUiEvents.UserMessage(
                                resourceWithString.message ?: "Error in Amount"
                            )
                        )
                    }
                    _state.update { it.copy(amountAED = resourceWithString.string ?: "") }
                }

                is ResourceWithString.Loading -> Unit
                is ResourceWithString.Success -> {
                    _state.update { it.copy(amountAED = resourceWithString.string ?: "") }
                }
            }
        }
    }

    private fun getCurrencyList() {
        viewModelScope.launch {
            getAllCurrencyExchangeFlowUseCase().collect { resource ->
                when (resource) {
                    is Resource.Error -> {
                        _uiEvent.send(
                            EditExpanseUiEvents.UserMessage(
                                resource.message ?: "Error can't get Currency List"
                            )
                        )
                        _state.update { it.copy(loading = false) }
                    }

                    is Resource.Loading -> {
                        _state.update { it.copy(loading = true) }
                    }

                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                currencyList = resource.data ?: emptyList(),
                                loading = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun takePicture(context: Context) {
        viewModelScope.launch {
            if (!::returnedExpanse.isInitialized) {
                _uiEvent.send(EditExpanseUiEvents.UserMessage("Can't retrieve Expanse"))
                return@launch
            }
            val selectedHmeId = returnedExpanse.HMEId
            getHMECodeByIdUseCase(selectedHmeId).collect { hmeCodeResource ->
                when (hmeCodeResource) {
                    is Resource.Error -> {
                        _uiEvent.send(EditExpanseUiEvents.UserMessage("Can't Retrieve HME Code"))
                    }

                    is Resource.Loading -> Unit
                    is Resource.Success -> {
                        val (providerUri, uri) = createUriForInvoice(
                            context,
                            hmeCodeResource.data!!.code
                        )
                        mutableUriList.add(uri)
                        _uiEvent.send(EditExpanseUiEvents.TakePicture(providerUri))
                    }
                }
            }
        }
    }

    fun photoTaken() {
        val list = mutableUriList.toList()
        _state.update { it.copy(invoicesUris = list) }
    }

    fun deleteImage(uri: Uri) {
        mutableUriList.remove(uri)
        uri.toFile().delete()
        val list = mutableUriList.toList()
        _state.update { it.copy(invoicesUris = list) }

    }

    fun photoPicked(context: Context, externalUri: Uri?) {
        if (externalUri == null) {
            viewModelScope.launch { _uiEvent.send(EditExpanseUiEvents.UserMessage("Can't get photo")) }
            return
        }
        viewModelScope.launch {
            val selectedHmeId = returnedExpanse.HMEId
            getHMECodeByIdUseCase(selectedHmeId).collect { hmeCodeResource ->
                when (hmeCodeResource) {
                    is Resource.Error -> {
                        _uiEvent.send(EditExpanseUiEvents.UserMessage("Can't Retrieve HME Code"))
                    }

                    is Resource.Loading -> Unit
                    is Resource.Success -> {
                        val hmeCode = hmeCodeResource.data ?: return@collect
                        val internalUri = createUriForInvoice(context, hmeCode.code).second
                        copyFiles(context, externalUri, internalUri)
                        mutableUriList.add(internalUri)
                        _state.update { it.copy(invoicesUris = mutableUriList.toList()) }

                        Log.d(TAG, "photoPicked: ${externalUri.encodedPath}")
                    }
                }
            }
        }
    }

    fun pickPicture() {
        viewModelScope.launch {
            _uiEvent.send(EditExpanseUiEvents.PickPicture)
        }
    }

    fun userEvent(event: EditExpanseUserEvent){
        when(event){
            is EditExpanseUserEvent.AmountAEDChanged -> amountAEDChanged(event.amount)
            is EditExpanseUserEvent.AmountChanged -> amountChanged(event.amount)
            is EditExpanseUserEvent.CashCheckChanged -> cashCheckChanged(event.checked)
            is EditExpanseUserEvent.CurrencySelected -> currencySelected(event.currencyExchange)
            EditExpanseUserEvent.DateClicked -> dateClicked()
            is EditExpanseUserEvent.DatePicked -> datePicked(event.year,event.month,event.day)
            EditExpanseUserEvent.DateShown -> dateShown()
            EditExpanseUserEvent.DeleteExpanse -> deleteExpanse()
            is EditExpanseUserEvent.DeleteImage -> deleteImage(event.uri)
            is EditExpanseUserEvent.DescriptionChanged -> descriptionChanged(event.description)
            is EditExpanseUserEvent.InvoiceNumberChanged -> invoiceNumberChanged(event.number)
            is EditExpanseUserEvent.PhotoPicked -> photoPicked(event.context,event.uri)
            EditExpanseUserEvent.PhotoTaken -> photoTaken()
            EditExpanseUserEvent.PickPicture -> pickPicture()
            is EditExpanseUserEvent.TakePicture -> takePicture(event.context)
            EditExpanseUserEvent.UpdateExpanse -> updateExpanse()
        }
    }
}