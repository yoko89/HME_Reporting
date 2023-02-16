package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.daily_allowance

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neklaway.hme_reporting.common.data.entity.AllowanceType
import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.model.TimeSheet
import com.neklaway.hme_reporting.common.domain.use_cases.customer_use_cases.GetAllCustomersFlowUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.hme_code_use_cases.GetHMECodeByCustomerIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.customer_id.GetCustomerIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.customer_id.SetCustomerIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.hme_id.GetHMEIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.hme_id.SetHMEIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.time_sheet_use_cases.GetTimeSheetByHMECodeIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.time_sheet_use_cases.UpdateTimeSheetUseCase
import com.neklaway.hme_reporting.utils.Resource
import com.neklaway.hme_reporting.utils.toDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "DailyAllowanceViewModel"

@HiltViewModel
class DailyAllowanceViewModel @Inject constructor(
    private val getAllCustomersFlowUseCase: GetAllCustomersFlowUseCase,
    private val getHMECodeByCustomerIdUseCase: GetHMECodeByCustomerIdUseCase,
    private val getTimeSheetByHMECodeIdUseCase: GetTimeSheetByHMECodeIdUseCase,
    private val getCustomerIdUseCase: GetCustomerIdUseCase,
    private val setCustomerIdUseCase: SetCustomerIdUseCase,
    private val getHmeIdUseCase: GetHMEIdUseCase,
    private val setHMEIdUseCase: SetHMEIdUseCase,
    private val updateTimeSheetUseCase: UpdateTimeSheetUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(DailyAllowanceState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<String>()
    val event: SharedFlow<String> = _event


    init {
        getCustomers()
    }

    private fun getCustomers() {
        getAllCustomersFlowUseCase().onEach { result ->
            when (result) {
                is Resource.Error -> {
                    sendEvent(
                        result.message ?: "Can't get customers"
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
            it.copy(
                selectedCustomer = customer
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            setCustomerIdUseCase(customer.id!!)

            getHMECodeByCustomerIdUseCase(customer.id).collect { result ->
                when (result) {
                    is Resource.Error -> {
                        sendEvent(
                            result.message ?: "Can't get HME codes"
                        )
                        _state.update { it.copy(loading = false) }
                    }
                    is Resource.Loading -> _state.update { it.copy(loading = true) }
                    is Resource.Success -> {
                        val savedSelectedHmeId = getHmeIdUseCase()
                        val savedSelectedHme = result.data?.find { it.id == savedSelectedHmeId }
                        _state.update {
                            it.copy(
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
        Log.d(
            TAG,
            "HME selected"
        )

        viewModelScope.launch(Dispatchers.IO) {
            setHMEIdUseCase(hmeCode.id!!)

            getTimeSheetByHMECodeIdUseCase(hmeCode.id).onEach { result ->
                Log.d(
                    TAG,
                    "get timesheet by HME: $result "
                )
                when (result) {
                    is Resource.Error -> {
                        sendEvent(
                            result.message ?: "Can't get Time Sheets"
                        )
                        _state.update { it.copy(loading = false) }
                    }
                    is Resource.Loading -> _state.update { it.copy(loading = true) }
                    is Resource.Success -> {
                        Log.d(
                            TAG,
                            "get timesheet by HME: success ${result.data}"
                        )
                        result.data?.let { timeSheetList ->
                            _state.update {
                                it.copy(
                                    timeSheetList = timeSheetList,
                                    loading = false,
                                )
                            }
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun timeSheetClicked(timeSheet: TimeSheet, allowanceType: AllowanceType) {
        timeSheet.id?.let {
            Log.d(TAG, "timeSheetClicked: ${timeSheet.date.toDate()} ${allowanceType.name}")
            viewModelScope.launch {
                updateTimeSheetUseCase(
                    HMEId = timeSheet.HMEId,
                    IBAUId = timeSheet.IBAUId,
                    date = timeSheet.date,
                    travelStart = timeSheet.travelStart,
                    workStart = timeSheet.workStart,
                    workEnd = timeSheet.workEnd,
                    travelEnd = timeSheet.travelEnd,
                    breakDuration = timeSheet.breakDuration,
                    traveledDistance = timeSheet.traveledDistance,
                    overTimeDay = timeSheet.overTimeDay,
                    travelDay = timeSheet.travelDay,
                    noWorkDay = timeSheet.noWorkDay,
                    id = timeSheet.id,
                    created = timeSheet.created,
                    dailyAllowance = allowanceType
                ).collect { resource ->
                    when (resource) {
                        is Resource.Error -> {
                            sendEvent(resource.message ?: "Error")
                            _state.update { it.copy(loading = false) }
                        }
                        is Resource.Loading -> _state.update { it.copy(loading = true) }
                        is Resource.Success -> _state.update { it.copy(loading = false) }
                    }
                }
            }
        }
    }

    private suspend fun sendEvent(event: String) {
        _event.emit(event)
    }

}