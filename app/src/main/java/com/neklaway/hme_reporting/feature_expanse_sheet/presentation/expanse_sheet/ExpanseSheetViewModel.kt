package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.expanse_sheet

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neklaway.hme_reporting.common.data.entity.AllowanceType
import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.use_cases.customer_use_cases.GetAllCustomersFlowUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.hme_code_use_cases.GetHMECodeByCustomerIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.customer_id.GetCustomerIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.customer_id.SetCustomerIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.hme_id.GetHMEIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.hme_id.SetHMEIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.time_sheet_use_cases.GetTimeSheetByHMECodeIdUseCase
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.Expanse
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.currency_exchange_use_cases.GetCurrencyExchangeByIdUseCase
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.expanse_use_cases.GetExpanseByHMEIdUseCase
import com.neklaway.hme_reporting.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
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
) : ViewModel() {

    private val _state = MutableStateFlow(ExpanseSheetState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<ExpanseSheetEvents>()
    val event: SharedFlow<ExpanseSheetEvents> = _event


    init {
        getCustomers()
    }

    private fun getCustomers() {
        getAllCustomersFlowUseCase().onEach { result ->
            when (result) {
                is Resource.Error -> {
                    sendEvent(
                        ExpanseSheetEvents.UserMessage(
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

    fun customerSelected(customer: Customer) {
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
                            ExpanseSheetEvents.UserMessage(
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

    fun hmeSelected(hmeCode: HMECode) {


        _state.update {
            it.copy(
                selectedHMECode = hmeCode,
            )
        }

        viewModelScope.launch {
            setHMEIdUseCase(hmeCode.id!!)
        }

        getTimeSheetByHMECodeIdUseCase(hmeCode.id!!).onEach { result ->
            when (result) {
                is Resource.Error -> {
                    sendEvent(
                        ExpanseSheetEvents.UserMessage(
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
                            timeSheet.dailyAllowance == AllowanceType._8hours
                        }
                        val fullDay = timeSheetList.count { timeSheet ->
                            timeSheet.dailyAllowance == AllowanceType._24hours
                        }
                        val noAllowance = timeSheetList.count { timeSheet ->
                            timeSheet.dailyAllowance == AllowanceType.no
                        }

                        _state.update {
                            it.copy(
                                timeSheetList = timeSheetList,
                                lessThan24hDays = lessThan24H,
                                fullDays = fullDay,
                                noAllowanceDays = noAllowance,
                                loading = false,
                            )
                        }
                    }
                }
            }
        }.launchIn(viewModelScope)


        getExpanseByHMEIdUseCase(hmeCode.id).onEach { result ->
            when (result) {
                is Resource.Error -> {
                    sendEvent(
                        ExpanseSheetEvents.UserMessage(
                            result.message ?: "Can't get Expanses"
                        )
                    )
                    _state.update { it.copy(loading = false) }
                }
                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> {
                    val expanseList = result.data ?: emptyList()
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
                }
            }
        }.launchIn(viewModelScope)
    }

    fun expanseClicked(expanse: Expanse) {
        expanse.id?.let {
            viewModelScope.launch {
                sendEvent(ExpanseSheetEvents.NavigateToExpanseSheet(it))
            }
        }
    }

    fun showMoreFABClicked() {
        _state.update { it.copy(fabVisible = !it.fabVisible) }
    }

    fun openExpanseSheets() {
        _state.update { it.copy(showFileList = true) }
    }


//TODO
//    fun createExpanseSheet() {
//        viewModelScope.launch {
//            expansePdfWorkerUseCase(state.value.timeSheetList,state.value.expanseList).collect { resource ->
//                when (resource) {
//                    is Resource.Error -> {
//                        _state.update { it.copy(loading = false) }
//                        delay(1000)
//                        sendEvent(TimeSheetEvents.UserMessage("PDF Creation Error"))
//                    }
//                    is Resource.Loading -> _state.update { it.copy(loading = true) }
//                    is Resource.Success -> {
//                        sendEvent(TimeSheetEvents.UserMessage("PDF Created"))
//                        _state.update { it.copy(loading = false) }
//                    }
//                }
//                Log.d(
//                    com.neklaway.hme_reporting.feature_time_sheet.presentation.time_sheet.TAG,
//                    "createTimeSheet: $resource"
//                )
//            }
//        }
//    }


    private suspend fun sendEvent(event: ExpanseSheetEvents) {
        _event.emit(event)
    }

    fun fileSelected(file: File) {
        _state.update { it.copy(showFileList = false) }
        viewModelScope.launch {
            sendEvent(ExpanseSheetEvents.ShowFile(file))
        }
    }

    fun fileSelectionCanceled() {
        _state.update { it.copy(showFileList = false) }
    }

    fun getCurrencyExchangeName(expanse: Expanse): Flow<String> = flow {
        getCurrencyExchangeByIdUseCase(expanse.currencyID).collect { resource ->
            emit(
                when (resource) {
                    is Resource.Error -> "Error"
                    is Resource.Loading -> ""
                    is Resource.Success -> resource.data?.currencyName ?: "Error"
                }
            )
        }
    }

}