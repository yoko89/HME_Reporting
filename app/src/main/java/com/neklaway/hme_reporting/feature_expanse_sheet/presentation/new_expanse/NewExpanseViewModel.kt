package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.new_expanse

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.use_cases.customer_use_cases.GetAllCustomersFlowUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.hme_code_use_cases.GetHMECodeByCustomerIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.customer_id.GetCustomerIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.customer_id.SetCustomerIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.hme_id.GetHMEIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.hme_id.SetHMEIdUseCase
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.CurrencyExchange
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.currency_exchange_use_cases.GetAllCurrencyExchangeFlowUseCase
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.expanse_use_cases.InsertExpanseUseCase
import com.neklaway.hme_reporting.utils.Resource
import com.neklaway.hme_reporting.utils.ResourceWithString
import com.neklaway.hme_reporting.utils.copyFiles
import com.neklaway.hme_reporting.utils.createUriForInvoice
import com.neklaway.hme_reporting.utils.toFloatWithString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

private const val TAG = "NewExpanseViewModel"

@HiltViewModel
class NewExpanseViewModel @Inject constructor(
    private val getAllCustomersFlowUseCase: GetAllCustomersFlowUseCase,
    private val getHMECodeByCustomerIdUseCase: GetHMECodeByCustomerIdUseCase,
    private val getCustomerIdUseCase: GetCustomerIdUseCase,
    private val setCustomerIdUseCase: SetCustomerIdUseCase,
    private val getHmeIdUseCase: GetHMEIdUseCase,
    private val setHMEIdUseCase: SetHMEIdUseCase,
    private val getAllCurrencyExchangeFlowUseCase: GetAllCurrencyExchangeFlowUseCase,
    private val insertExpanseUseCase: InsertExpanseUseCase,
) : ViewModel() {


    private val _state = MutableStateFlow(NewExpanseState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<NewExpanseEvents>()
    val event: SharedFlow<NewExpanseEvents> = _event

    private val mutableUriList = state.value.invoicesUris.toMutableList()

    init {
        getCustomers()
        getCurrencyList()
    }

    private fun getCustomers() {
        getAllCustomersFlowUseCase().onEach { result ->
            when (result) {
                is Resource.Error -> {
                    _event.emit(
                        NewExpanseEvents.UserMessage(
                            result.message ?: "Can't get customers"
                        )
                    )
                    _state.update { it.copy(loading = false) }
                }

                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> {
                    val savedCustomerId = getCustomerIdUseCase()
                    val savedSelectedCustomer = result.data?.find { customer ->
                        customer.id == savedCustomerId
                    }
                    _state.update {
                        it.copy(
                            customers = result.data.orEmpty(),
                            loading = false,
                            selectedCustomer = savedSelectedCustomer
                        )
                    }
                    savedSelectedCustomer?.let {
                        customerSelected(it)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun customerSelected(customer: Customer) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    selectedCustomer = customer
                )
            }

            viewModelScope.launch(Dispatchers.IO) {
                setCustomerIdUseCase(customer.id!!)

                getHMECodeByCustomerIdUseCase(customer.id).collect { resource ->
                    when (resource) {
                        is Resource.Error -> {
                            _event.emit(
                                NewExpanseEvents.UserMessage(
                                    resource.message ?: "Can't get HME codes"
                                )
                            )
                            _state.update { it.copy(loading = false) }
                        }

                        is Resource.Loading -> _state.update { it.copy(loading = true) }
                        is Resource.Success -> {
                            val savedSelectedHmeId = getHmeIdUseCase()
                            val savedSelectedHme = resource.data?.find { hmeCode ->
                                hmeCode.id == savedSelectedHmeId
                            }
                            _state.update {
                                it.copy(
                                    hmeCodes = resource.data.orEmpty(),
                                    loading = false,
                                    selectedHMECode = savedSelectedHme
                                )
                            }
                            savedSelectedHme?.let {
                                hmeSelected(it)
                            }
                        }
                    }
                }
            }
        }
    }

    fun hmeSelected(hmeCode: HMECode) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    selectedHMECode = hmeCode
                )
            }

            viewModelScope.launch(Dispatchers.IO) hmeSelectedScope@{
                setHMEIdUseCase(hmeCode.id!!)
            }
        }
    }

    fun insertExpanse() {
        viewModelScope.launch(Dispatchers.IO) {

            val amountInFloat = state.value.amount.toFloatWithString().let { resource ->
                when (resource) {
                    is ResourceWithString.Error -> {
                        _event.emit(NewExpanseEvents.UserMessage(resource.message ?: "Error"))
                        resource.data
                    }

                    is ResourceWithString.Loading -> null
                    is ResourceWithString.Success -> {
                        resource.data
                    }
                }

            }
            val amountInAEDInFloat = state.value.amountAED.toFloatWithString().let { resource ->
                when (resource) {
                    is ResourceWithString.Error -> {
                        _event.emit(NewExpanseEvents.UserMessage(resource.message ?: "Error"))
                        resource.data
                    }

                    is ResourceWithString.Loading -> null
                    is ResourceWithString.Success -> {
                        resource.data
                    }
                }

            }

            insertExpanseUseCase.invoke(
                HMEId = state.value.selectedHMECode?.id,
                date = state.value.date,
                invoiceNumber = state.value.invoiceNumber,
                description = state.value.description,
                personallyPaid = state.value.personallyPaid,
                amount = amountInFloat,
                currencyID = state.value.selectedCurrency?.id,
                amountAED = amountInAEDInFloat,
                invoiceUris = state.value.invoicesUris.map { it.toString() }
            ).collect { result ->
                Log.d(TAG, "insertExpanse: result is $result")
                when (result) {
                    is Resource.Loading -> _state.update { it.copy(loading = true) }
                    is Resource.Error -> {
                        _event.emit(
                            NewExpanseEvents.UserMessage(
                                result.message ?: "Can't Insert Expanse"
                            )
                        )
                        _state.update { it.copy(loading = false) }
                    }

                    is Resource.Success -> {
                        _event.emit(NewExpanseEvents.UserMessage("Expanse Saved"))
                        _state.update { it.copy(loading = false) }
                        clearState()
                    }
                }
            }
        }
    }

    private fun clearState() {
        mutableUriList.clear()
        _state.update {
            it.copy(
                date = null,
                description = "",
                invoiceNumber = "",
                amount = "",
                amountAED = "",
                personallyPaid = false,
                selectedCurrency = null,
                invoicesUris = emptyList()
            )
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

    fun datePickedCanceled() {
        _state.update { it.copy(showDatePicker = false) }
    }

    fun amountChanged(amount: String) {
        amount.toFloatWithString().let { resourceWithString ->
            when (resourceWithString) {
                is ResourceWithString.Error -> {
                    viewModelScope.launch {
                        _event.emit(
                            NewExpanseEvents.UserMessage(
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
                        _event.emit(
                            NewExpanseEvents.UserMessage(
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
                        _event.emit(
                            NewExpanseEvents.UserMessage(
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
                    _event.emit(NewExpanseEvents.UserMessage(resource.message ?: "Error"))
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

    fun cashCheckChanged(checked: Boolean) {
        _state.update { it.copy(personallyPaid = checked) }
    }

    fun takePicture(context: Context) {
        val selectedHme = state.value.selectedHMECode ?: return
        val (providerUri, uri) = createUriForInvoice(context, selectedHme.code)
        mutableUriList.add(uri)

        viewModelScope.launch {
            _event.emit(NewExpanseEvents.TakePicture(providerUri))
        }
    }

    fun photoTaken(successful: Boolean) {
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
        val selectedHme = state.value.selectedHMECode ?: return
        if (externalUri == null) {
            viewModelScope.launch { _event.emit(NewExpanseEvents.UserMessage("Can't get photo")) }
            return
        }

        val internalUri = createUriForInvoice(context, selectedHme.code).second
        copyFiles(context,externalUri, internalUri)
        mutableUriList.add(internalUri)
        _state.update { it.copy(invoicesUris = mutableUriList) }

        Log.d(TAG, "photoPicked: ${externalUri.encodedPath}")
    }

    fun pickPicture() {
        viewModelScope.launch {
            _event.emit(NewExpanseEvents.PickPicture)
        }
    }
}