package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.expanse_sheet

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neklaway.hme_reporting.common.data.entity.Accommodation
import com.neklaway.hme_reporting.common.data.entity.AllowanceType
import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.use_cases.customer_use_cases.GetAllCustomersFlowUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.hme_code_use_cases.GetHMECodeByCustomerIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.hme_code_use_cases.UpdateHMECodeUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.customer_id.GetCustomerIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.customer_id.SetCustomerIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.hme_id.GetHMEIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.hme_id.SetHMEIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.time_sheet_use_cases.GetTimeSheetByHMECodeIdUseCase
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.Expanse
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.currency_exchange_use_cases.GetCurrencyExchangeByIdUseCase
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.expanse_pdf_worker_use_case.ExpansePDFWorkerUseCase
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.expanse_use_cases.GetExpanseByHMEIdUseCase
import com.neklaway.hme_reporting.utils.CalculateAllowance
import com.neklaway.hme_reporting.utils.CalculateExpanse
import com.neklaway.hme_reporting.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

private const val TAG = "ExpanseSheetViewModel"

@HiltViewModel
class ExpanseSheetViewModel @Inject constructor(
    private val getAllCustomersFlowUseCase: GetAllCustomersFlowUseCase,
    private val getHMECodeByCustomerIdUseCase: GetHMECodeByCustomerIdUseCase,
    private val getTimeSheetByHMECodeIdUseCase: GetTimeSheetByHMECodeIdUseCase,
    private val getCustomerIdUseCase: GetCustomerIdUseCase,
    private val setCustomerIdUseCase: SetCustomerIdUseCase,
    private val getHmeIdUseCase: GetHMEIdUseCase,
    private val setHMEIdUseCase: SetHMEIdUseCase,
    private val getCurrencyExchangeByIdUseCase: GetCurrencyExchangeByIdUseCase,
    private val getExpanseByHMEIdUseCase: GetExpanseByHMEIdUseCase,
    private val calculateAllowance: CalculateAllowance,
    private val calculateExpanse: CalculateExpanse,
    private val updateHMECodeUseCase: UpdateHMECodeUseCase,
    private val expansePDFWorkerUseCase: ExpansePDFWorkerUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ExpanseSheetState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<ExpanseSheetUiEvents>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val totalExpanse = MutableStateFlow(0f)
    private val totalAllowance = MutableStateFlow(0f)

    init {
        getCustomers()
        totalExpanse.combine(totalAllowance) { totalExpanse, totalAllowance ->
            Pair(
                totalExpanse,
                totalAllowance
            )
        }.onEach { totalPair ->
            _state.update { it.copy(totalPaidAmount = totalPair.first + totalPair.second) }
        }.launchIn(viewModelScope)
    }

    private fun getCustomers() {
        getAllCustomersFlowUseCase().onEach { result ->
            when (result) {
                is Resource.Error -> {
                    sendEvent(
                        ExpanseSheetUiEvents.UserMessage(
                            result.message ?: "Can't get customers"
                        )
                    )
                    _state.update { it.copy(loading = false) }
                }

                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> {
                    val savedCustomerId = getCustomerIdUseCase()
                    val savedSelectedCustomer = result.data?.find { it.id == savedCustomerId }
                    _state.update {
                        it.copy(
                            customers = result.data.orEmpty(),
                            loading = false,
                            selectedCustomer = savedSelectedCustomer,
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
        _state.update {
            ExpanseSheetState(
                customers = it.customers,
                selectedCustomer = customer
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            setCustomerIdUseCase(customer.id!!)


            getHMECodeByCustomerIdUseCase(customer.id).collect { result ->
                when (result) {
                    is Resource.Error -> {
                        sendEvent(
                            ExpanseSheetUiEvents.UserMessage(
                                result.message ?: "Can't get HME codes"
                            )
                        )
                        _state.update { it.copy(loading = false) }
                    }

                    is Resource.Loading -> _state.update { it.copy(loading = true) }
                    is Resource.Success -> {
                        val savedSelectedHmeId = getHmeIdUseCase()
                        val savedSelectedHme = result.data?.find { it.id == savedSelectedHmeId }
                        _state.update {
                            ExpanseSheetState(
                                customers = it.customers,
                                selectedCustomer = it.selectedCustomer,
                                hmeCodes = result.data.orEmpty(),
                                selectedHMECode = savedSelectedHme,
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

    private fun hmeSelected(hmeCode: HMECode) {


        _state.update {
            it.copy(
                selectedHMECode = hmeCode,
                accommodation = hmeCode.accommodation,
            )
        }

        viewModelScope.launch {
            setHMEIdUseCase(hmeCode.id!!)
        }

        getTimeSheetByHMECodeIdUseCase(hmeCode.id!!).onEach { result ->
            when (result) {
                is Resource.Error -> {
                    sendEvent(
                        ExpanseSheetUiEvents.UserMessage(
                            result.message ?: "Can't get Time Sheets"
                        )
                    )
                    _state.update { it.copy(loading = false) }
                }

                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> {
                    Log.d(
                        TAG,
                        "get timesheet by HME: success "
                    )
                    result.data?.let { timeSheetList ->

                        Log.d(
                            TAG,
                            "get timesheet by HME: collect $timeSheetList "
                        )
                        val lessThan24H = timeSheetList.count { timeSheet ->
                            timeSheet.dailyAllowance == AllowanceType._8hours && timeSheet.expanseSelected
                        }
                        val fullDay = timeSheetList.count { timeSheet ->
                            timeSheet.dailyAllowance == AllowanceType._24hours && timeSheet.expanseSelected
                        }
                        val noAllowance = timeSheetList.count { timeSheet ->
                            timeSheet.dailyAllowance == AllowanceType.No && timeSheet.expanseSelected
                        }


                        _state.update {
                            it.copy(
                                timeSheetList = timeSheetList,
                                lessThan24hDays = lessThan24H,
                                fullDays = fullDay,
                                noAllowanceDays = noAllowance,
                                loading = false,
                                missingDailyAllowance = timeSheetList.any { timeSheet ->
                                    timeSheet.dailyAllowance == null && timeSheet.expanseSelected
                                })
                        }
                        totalAllowance.emit(
                            calculateAllowance.invoke(fullDay, lessThan24H)
                        )

                        if (timeSheetList.any {
                                it.dailyAllowance == null && it.expanseSelected
                            }) {
                            sendEvent(ExpanseSheetUiEvents.UserMessage("Daily Allowance is missing, please Check"))
                        }
                    }
                }
            }
        }.launchIn(viewModelScope)

        getExpanseByHMEIdUseCase(hmeCode.id).onEach { result ->
            when (result) {
                is Resource.Error -> {
                    sendEvent(
                        ExpanseSheetUiEvents.UserMessage(
                            result.message ?: "Can't get Expanses"
                        )
                    )
                    _state.update { it.copy(loading = false) }
                }

                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> {
                    val expanseList = result.data?.sortedBy { it.date } ?: emptyList()
                    Log.d(
                        TAG,
                        "get Expanse by HME: collect $expanseList "
                    )
                    _state.update {
                        it.copy(
                            expanseList = expanseList,
                            loading = false,
                        )
                    }

                    totalExpanse.emit(calculateExpanse(expanseList))
                }
            }
        }.launchIn(viewModelScope)
    }


    private fun expanseClicked(expanse: Expanse) {
        expanse.id?.let {
            viewModelScope.launch {
                sendEvent(ExpanseSheetUiEvents.NavigateToExpanseSheetUi(it))
            }
        }
    }

    private fun showMoreFABClicked() {
        _state.update { it.copy(fabVisible = !it.fabVisible) }
    }

    private fun openExpanseSheets() {
        _state.update { it.copy(showFileList = true) }
    }

    private fun createExpanseSheet() {
        viewModelScope.launch {
            expansePDFWorkerUseCase(
                state.value.timeSheetList.filter { it.expanseSelected },
                state.value.expanseList
            ).collect { resource ->
                when (resource) {
                    is Resource.Error -> {
                        _state.update { it.copy(loading = false) }
                        delay(1000)
                        sendEvent(ExpanseSheetUiEvents.UserMessage("PDF Creation Error"))
                    }

                    is Resource.Loading -> _state.update { it.copy(loading = true) }
                    is Resource.Success -> {
                        _state.update { it.copy(loading = false) }
                        sendEvent(ExpanseSheetUiEvents.UserMessage("PDF Created"))
                    }
                }
                Log.d(
                    TAG,
                    "createExpanseSheet: $resource"
                )
            }
        }
    }


    private fun sendEvent(event: ExpanseSheetUiEvents) {
        viewModelScope.launch {
            _uiEvent.send(event)
            Log.d(TAG, "sendEvent: $event")
        }
    }

    private fun fileSelected(file: File) {
        _state.update { it.copy(showFileList = false) }
        viewModelScope.launch {
            sendEvent(ExpanseSheetUiEvents.ShowFile(file))
        }
    }

    private fun fileSelectionCanceled() {
        _state.update { it.copy(showFileList = false) }
    }

    fun getCurrencyExchangeName(currencyID: Long) = flow {
            getCurrencyExchangeByIdUseCase(currencyID).let { resource ->
                emit(
                    when (resource) {
                        is Resource.Error -> "Error"
                        is Resource.Loading -> ""
                        is Resource.Success -> resource.data?.currencyName ?: "Error"
                    }
                )
            }
    }

    private fun accommodationChanged(accommodation: Accommodation) {
        viewModelScope.launch {
            state.value.selectedHMECode?.let { hmeCode ->
                updateHMECodeUseCase(
                    id = hmeCode.id!!,
                    customerId = hmeCode.customerId,
                    code = hmeCode.code,
                    machineType = hmeCode.machineType,
                    machineNumber = hmeCode.machineNumber,
                    workDescription = hmeCode.workDescription,
                    fileNumber = hmeCode.fileNumber,
                    expanseNumber = hmeCode.expanseNumber,
                    signerName = hmeCode.signerName,
                    signatureDate = hmeCode.signatureDate,
                    accommodation = accommodation
                ).collect { result ->
                    when (result) {
                        is Resource.Error -> sendEvent(
                            ExpanseSheetUiEvents.UserMessage(
                                result.message ?: "Error"
                            )
                        )

                        is Resource.Loading -> Unit
                        is Resource.Success -> {
                            _state.update { it.copy(accommodation = accommodation) }
                        }
                    }

                }
            }
        }
    }

    fun userEvent(event: ExpanseSheetUserEvent) {
        when (event) {
            is ExpanseSheetUserEvent.AccommodationChanged -> accommodationChanged(event.accommodation)
            ExpanseSheetUserEvent.CreateExpanseSheet -> createExpanseSheet()
            is ExpanseSheetUserEvent.CustomerSelected -> customerSelected(event.customer)
            is ExpanseSheetUserEvent.ExpanseClicked -> expanseClicked(event.expanse)
            is ExpanseSheetUserEvent.FileSelected -> fileSelected(event.file)
            ExpanseSheetUserEvent.FileSelectionCanceled -> fileSelectionCanceled()
            is ExpanseSheetUserEvent.HmeSelected -> hmeSelected(event.hmeCode)
            ExpanseSheetUserEvent.OpenExpanseSheets -> openExpanseSheets()
            ExpanseSheetUserEvent.ShowMoreFABClicked -> showMoreFABClicked()
        }
    }

}