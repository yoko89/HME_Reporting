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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat
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

    private val _uiEvent = Channel<NewExpanseUiEvents>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val mutableUriList: MutableList<Uri> = mutableListOf()

    init {
        getCustomers()
        getCurrencyList()
    }

    private fun getCustomers() {
        getAllCustomersFlowUseCase().onEach { result ->
            when (result) {
                is Resource.Error -> {
                    _uiEvent.send(
                        NewExpanseUiEvents.UserMessage(
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

    private fun customerSelected(customer: Customer) {
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
                            _uiEvent.send(
                                NewExpanseUiEvents.UserMessage(
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

    private fun hmeSelected(hmeCode: HMECode) {
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

    private fun insertExpanse() {
        viewModelScope.launch(Dispatchers.IO) {

            val amountInFloat = state.value.amount.let { resource ->
                when (resource) {
                    is ResourceWithString.Error -> {
                        _uiEvent.send(NewExpanseUiEvents.UserMessage(resource.message ?: "Error"))
                        resource.data
                    }

                    is ResourceWithString.Loading -> null
                    is ResourceWithString.Success -> {
                        resource.data
                    }
                }

            }
            val amountInAEDInFloat = state.value.amountAED.let { resource ->
                when (resource) {
                    is ResourceWithString.Error -> {
                        _uiEvent.send(NewExpanseUiEvents.UserMessage(resource.message ?: "Error"))
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
                        _uiEvent.send(
                            NewExpanseUiEvents.UserMessage(
                                result.message ?: "Can't Insert Expanse"
                            )
                        )
                        _state.update { it.copy(loading = false) }
                    }

                    is Resource.Success -> {
                        _uiEvent.send(NewExpanseUiEvents.UserMessage("Expanse Saved"))
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
                amount = ResourceWithString.Success(0f, ""),
                amountAED = ResourceWithString.Success(0f, ""),
                personallyPaid = false,
                selectedCurrency = null,
                invoicesUris = emptyList()
            )
        }
    }

    private fun dateClicked() {
        _state.update { it.copy(showDatePicker = true) }
        Log.d(TAG, "dateClicked: ")
    }

    private fun datePicked(year: Int, month: Int, day: Int) {
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

    private fun datePickedCanceled() {
        _state.update { it.copy(showDatePicker = false) }
    }

    private fun amountChanged(amount: String) {
        amount.toFloatWithString().let { resourceWithString ->
            _state.update { it.copy(amount = resourceWithString) }

            when (resourceWithString) {
                is ResourceWithString.Success -> {
                    viewModelScope.launch {
                        calculateAmountInAED()
                    }
                }

                else -> Unit
            }
        }
    }

    private fun amountAEDChanged(amount: String) {
        amount.toFloatWithString().let { resourceWithString ->
            _state.update { it.copy(amountAED = resourceWithString) }
        }
    }

    private fun getCurrencyList() {
        viewModelScope.launch {
            getAllCurrencyExchangeFlowUseCase().collect { resource ->
                when (resource) {
                    is Resource.Error -> {
                        _uiEvent.send(
                            NewExpanseUiEvents.UserMessage(
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

    private fun currencySelected(currencyExchange: CurrencyExchange) {
        viewModelScope.launch {
            _state.update { it.copy(selectedCurrency = currencyExchange) }
            calculateAmountInAED()
        }

    }

    private fun calculateAmountInAED() {
        val amountInAED = state.value.amount.let {resource ->
            when (resource) {
                is ResourceWithString.Success -> {
                    val amount = resource.data?.times(state.value.selectedCurrency?.rate ?: 0f)
                    Log.d(
                        TAG,"calculateAmountInAED: rate is ${state.value.selectedCurrency?.rate}"
                    )
                    val df = DecimalFormat("#.##")
                    df.roundingMode = RoundingMode.HALF_UP
                    val floatRounded = df.format(amount).toFloat()

                    ResourceWithString.Success(floatRounded, floatRounded.toString())
                }

                else -> ResourceWithString.Success(0f, "")

            }
        }
        _state.update { it.copy(amountAED = amountInAED) }
    }

    private fun invoiceNumberChanged(invoiceNumber: String) {
        _state.update { it.copy(invoiceNumber = invoiceNumber) }
    }

    private fun descriptionChanged(description: String) {
        _state.update { it.copy(description = description) }
    }

    private fun cashCheckChanged(checked: Boolean) {
        _state.update { it.copy(personallyPaid = checked) }
    }

    private fun takePicture(context: Context) {
        val selectedHme = state.value.selectedHMECode ?: return
        val (providerUri, uri) = createUriForInvoice(context, selectedHme.code)
        mutableUriList.add(uri)

        viewModelScope.launch {
            _uiEvent.send(NewExpanseUiEvents.TakePicture(providerUri))
        }
    }

    private fun photoTaken() {
        val list = mutableUriList.toList()
        _state.update { it.copy(invoicesUris = list) }
    }

    private fun deleteImage(uri: Uri) {
        mutableUriList.remove(uri)
        uri.toFile().delete()
        val list = mutableUriList.toList()
        _state.update { it.copy(invoicesUris = list) }

    }

    private fun photoPicked(context: Context, externalUri: Uri?) {
        val selectedHme = state.value.selectedHMECode ?: return
        if (externalUri == null) {
            viewModelScope.launch { _uiEvent.send(NewExpanseUiEvents.UserMessage("Can't get photo")) }
            return
        }

        val internalUri = createUriForInvoice(context, selectedHme.code).second
        copyFiles(context, externalUri, internalUri)
        mutableUriList.add(internalUri)
        _state.update { it.copy(invoicesUris = mutableUriList.toList()) }

        Log.d(TAG, "photoPicked: ${externalUri.encodedPath}")
    }

    private fun pickPicture() {
        viewModelScope.launch {
            _uiEvent.send(NewExpanseUiEvents.PickPicture)
        }
    }

    fun userEvent(event: NewExpanseUserEvent) {
        when (event) {
            is NewExpanseUserEvent.AmountAEDChanged -> amountAEDChanged(event.amount)
            is NewExpanseUserEvent.AmountChanged -> amountChanged(event.amount)
            is NewExpanseUserEvent.CashCheckChanged -> cashCheckChanged(event.checked)
            is NewExpanseUserEvent.CurrencySelected -> currencySelected(event.currencyExchange)
            is NewExpanseUserEvent.CustomerSelected -> customerSelected(event.customer)
            NewExpanseUserEvent.DateClicked -> dateClicked()
            is NewExpanseUserEvent.DatePicked -> datePicked(event.year, event.month, event.day)
            NewExpanseUserEvent.DatePickedCanceled -> datePickedCanceled()
            is NewExpanseUserEvent.DeleteImage -> deleteImage(event.uri)
            is NewExpanseUserEvent.DescriptionChanged -> descriptionChanged(event.description)
            is NewExpanseUserEvent.HmeSelected -> hmeSelected(event.hmeCode)
            NewExpanseUserEvent.InsertExpanse -> insertExpanse()
            is NewExpanseUserEvent.InvoiceNumberChanged -> invoiceNumberChanged(event.number)
            is NewExpanseUserEvent.PhotoPicked -> photoPicked(event.context, event.uri)
            NewExpanseUserEvent.PhotoTaken -> photoTaken()
            NewExpanseUserEvent.PickPicture -> pickPicture()
            is NewExpanseUserEvent.TakePicture -> takePicture(event.context)
        }
    }
}