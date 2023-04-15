package com.neklaway.hme_reporting.feature_time_sheet.presentation.new_time_sheet

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.model.IBAUCode
import com.neklaway.hme_reporting.common.domain.use_cases.customer_use_cases.GetAllCustomersFlowUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.hme_code_use_cases.GetHMECodeByCustomerIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.ibau_code_use_cases.GetIBAUCodeByHMECodeIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.break_duration.GetSavedBreakDurationUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.break_duration.SetSavedBreakDurationUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.customer_id.GetCustomerIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.customer_id.SetCustomerIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.date.GetDateUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.date.SetDateUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.hme_id.GetHMEIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.hme_id.SetHMEIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.ibau_id.GetIBAUIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.ibau_id.SetIBAUIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.is_travel_day.GetIsSavedTravelDayUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.is_travel_day.SetIsSavedTravelDayUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.is_weekend.GetIsWeekendUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.is_weekend.SetIsWeekendUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.over_time.GetIsOverTimeUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.over_time.SetIsOverTimeUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.travel_distance.GetTravelDistanceUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.travel_distance.SetTravelDistanceUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.travel_end.GetTravelEndUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.travel_end.SetTravelEndUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.travel_start.GetTravelStartUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.travel_start.SetTravelStartUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.work_end.GetWorkEndUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.work_end.SetWorkEndUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.work_start.GetWorkStartUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.work_start.SetWorkStartUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.time_sheet_use_cases.InsertTimeSheetUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.break_time.GetBreakDurationUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.is_auto_clear.GetIsAutoClearUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.is_ibau.GetIsIbauUseCase
import com.neklaway.hme_reporting.utils.Resource
import com.neklaway.hme_reporting.utils.ResourceWithString
import com.neklaway.hme_reporting.utils.toFloatWithString
import com.neklaway.hme_reporting.utils.toIntWithString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

private const val TAG = "NewTimeSheetViewModel"

@HiltViewModel
class NewTimeSheetViewModel @Inject constructor(
    private val getAllCustomersFlowUseCase: GetAllCustomersFlowUseCase,
    private val getHMECodeByCustomerIdUseCase: GetHMECodeByCustomerIdUseCase,
    private val getIBAUCodeByHMECodeIdUseCase: GetIBAUCodeByHMECodeIdUseCase,
    private val getIsIBAUUseCase: GetIsIbauUseCase,
    private val insertTimeSheetUseCase: InsertTimeSheetUseCase,
    private val getCustomerIdUseCase: GetCustomerIdUseCase,
    private val setCustomerIdUseCase: SetCustomerIdUseCase,
    private val getHmeIdUseCase: GetHMEIdUseCase,
    private val setHMEIdUseCase: SetHMEIdUseCase,
    private val getIBAUIdUseCase: GetIBAUIdUseCase,
    private val setIBAUIdUseCase: SetIBAUIdUseCase,
    private val getIsAutoClearUseCase: GetIsAutoClearUseCase,
    private val getBreakDurationUseCase: GetBreakDurationUseCase,
    private val getSavedBreakDurationUseCase: GetSavedBreakDurationUseCase,
    private val setSavedBreakDurationUseCase: SetSavedBreakDurationUseCase,
    private val getDateUseCase: GetDateUseCase,
    private val setDateUseCase: SetDateUseCase,
    private val getIsSavedTravelDayUseCase: GetIsSavedTravelDayUseCase,
    private val setIsSavedTravelDayUseCase: SetIsSavedTravelDayUseCase,
    private val getIsWeekendUseCase: GetIsWeekendUseCase,
    private val setIsWeekendUseCase: SetIsWeekendUseCase,
    private val getIsOverTimeUseCase: GetIsOverTimeUseCase,
    private val setIsOverTimeUseCase: SetIsOverTimeUseCase,
    private val getTravelDistanceUseCase: GetTravelDistanceUseCase,
    private val setTravelDistanceUseCase: SetTravelDistanceUseCase,
    private val getTravelEndUseCase: GetTravelEndUseCase,
    private val setTravelEndUseCase: SetTravelEndUseCase,
    private val getTravelStartUseCase: GetTravelStartUseCase,
    private val setTravelStartUseCase: SetTravelStartUseCase,
    private val getWorkEndUseCase: GetWorkEndUseCase,
    private val setWorkEndUseCase: SetWorkEndUseCase,
    private val getWorkStartUseCase: GetWorkStartUseCase,
    private val setWorkStartUseCase: SetWorkStartUseCase
) : ViewModel() {


    private val _state = MutableStateFlow(NewTimeSheetState())
    val state = _state.asStateFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage

    private var isIbau: Boolean = false

    init {
        getCustomers()
        getSavedData()
    }

    private fun getSavedData() {
        viewModelScope.launch {
            isIbau = getIsIBAUUseCase.invoke()
            val isTravelDay = getIsSavedTravelDayUseCase()
            val isWeekEnd = getIsWeekendUseCase()
            val savedDate = getDateUseCase()
            val workStart = getWorkStartUseCase()
            val workEnd = getWorkEndUseCase()
            val travelStart = getTravelStartUseCase()
            val travelEnd = getTravelEndUseCase()
            val breakDuration =
                getSavedBreakDurationUseCase()?.toString() ?: getBreakDurationUseCase()
            val travelDistance = getTravelDistanceUseCase()?.toString() ?: ""
            val isOverTime = getIsOverTimeUseCase()

            _state.update {
                it.copy(
                    isIbau = isIbau,
                    travelDay = isTravelDay,
                    noWorkday = isWeekEnd,
                    date = savedDate,
                    workEnd = workEnd,
                    workStart = workStart,
                    traveledDistance = travelDistance,
                    travelEnd = travelEnd,
                    travelStart = travelStart,
                    breakDuration = breakDuration,
                    overTimeDay = isOverTime
                )
            }
        }
    }

    private fun getCustomers() {
        getAllCustomersFlowUseCase().onEach { result ->
            when (result) {
                is Resource.Error -> {
                    _userMessage.emit(result.message ?: "Can't get customers")
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

                getHMECodeByCustomerIdUseCase(customer.id).collect { result ->
                    when (result) {
                        is Resource.Error -> {
                            _userMessage.emit(result.message ?: "Can't get HME codes")
                            _state.update { it.copy(loading = false) }
                        }
                        is Resource.Loading -> _state.update { it.copy(loading = true) }
                        is Resource.Success -> {
                            val savedSelectedHmeId = getHmeIdUseCase()
                            val savedSelectedHme = result.data?.find { hmeCode ->
                                hmeCode.id == savedSelectedHmeId
                            }
                            _state.update {
                                it.copy(
                                    hmeCodes = result.data.orEmpty(),
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

                if (!getIsIBAUUseCase()) return@hmeSelectedScope

                getIBAUCodeByHMECodeIdUseCase(hmeCode.id).collect { result ->
                    when (result) {
                        is Resource.Error -> {
                            _userMessage.emit(result.message ?: "Can't get IBAU codes")
                            _state.update { it.copy(loading = false) }
                        }
                        is Resource.Loading -> _state.update { it.copy(loading = true) }
                        is Resource.Success -> {
                            val savedSelectedIbauId = getIBAUIdUseCase()
                            val savedSelectedIbau =
                                result.data?.find { it.id == savedSelectedIbauId }
                            _state.update {
                                it.copy(
                                    ibauCodes = result.data.orEmpty(),
                                    loading = false,
                                    selectedIBAUCode = savedSelectedIbau
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    fun ibauSelected(ibauCode: IBAUCode) {
        _state.update {
            it.copy(
                selectedIBAUCode = ibauCode
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            setIBAUIdUseCase(ibauCode.id!!)
        }
    }

    fun insertTimeSheet() {
        viewModelScope.launch(Dispatchers.IO) {

            insertTimeSheetUseCase.invoke(
                HMEId = state.value.selectedHMECode?.id,
                IBAUId = state.value.selectedIBAUCode?.id,
                date = state.value.date,
                travelStart = state.value.travelStart,
                workStart = state.value.workStart,
                workEnd = state.value.workEnd,
                travelEnd = state.value.travelEnd,
                breakDuration = state.value.breakDuration.toFloatOrNull(),
                traveledDistance = state.value.traveledDistance.toIntOrNull(),
                overTimeDay = state.value.overTimeDay,
                travelDay = state.value.travelDay,
                noWorkDay = state.value.noWorkday
            ).collect { result ->
                Log.d(TAG, "insertTimeSheet: result is $result")
                when (result) {
                    is Resource.Loading -> _state.update { it.copy(loading = true) }
                    is Resource.Error -> {
                        _userMessage.emit(result.message ?: "Can't get IBAU codes")
                        _state.update { it.copy(loading = false) }
                    }
                    is Resource.Success -> {
                        _userMessage.emit("TimeSheet Saved")
                        _state.update { it.copy(loading = false) }
                        if (!getIsAutoClearUseCase.invoke()) {
                            _state.update {
                                it.copy(
                                    date = null,
                                )
                            }
                            viewModelScope.launch(Dispatchers.IO) {
                                setDateUseCase(null)
                            }
                            return@collect
                        }

                        clearState()
                        clearSavedData()
                    }
                }
            }
        }
    }

    private fun clearSavedData() {
        viewModelScope.launch(Dispatchers.IO) {
            setTravelDistanceUseCase(null)
            setIsOverTimeUseCase(false)
            setDateUseCase(null)
            setIsSavedTravelDayUseCase(false)
            setIsWeekendUseCase(false)
            setTravelEndUseCase(null)
            setTravelStartUseCase(null)
            setWorkEndUseCase(null)
            setWorkStartUseCase(null)
            setSavedBreakDurationUseCase(null)
            setIsOverTimeUseCase(false)
        }
    }

    private suspend fun clearState() {
        _state.update {
            it.copy(
                date = null,
                travelStart = null,
                travelEnd = null,
                workStart = null,
                workEnd = null,
                traveledDistance = "",
                breakDuration = getBreakDurationUseCase.invoke()
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

        val travelStart = state.value.travelStart
        travelStart?.set(year, month, day)
        val workStart = state.value.workStart
        workStart?.set(year, month, day)
        val workEnd = state.value.workEnd
        workEnd?.set(year, month, day)
        val travelEnd = state.value.travelEnd
        travelEnd?.set(year, month, day)


        _state.update {
            it.copy(
                date = date,
                travelStart = travelStart,
                workStart = workStart,
                workEnd = workEnd,
                travelEnd = travelEnd,
                showDatePicker = false
            )
        }
        viewModelScope.launch {
            setDateUseCase(date)
            setTravelStartUseCase(travelStart)
            setWorkStartUseCase(workStart)
            setWorkEndUseCase(workEnd)
            setTravelEndUseCase(travelEnd)
        }
    }

    fun datePickedCanceled() {
        _state.update { it.copy(showDatePicker = false) }
    }

    fun travelStartClicked() {
        Log.d(TAG, "NewTimeSheetScreen: Work start clicked")

        _state.update { it.copy(showTimePickerTravelStart = true) }
    }

    fun travelStartPicked(hour: Int, minute: Int) {
        val date = state.value.date!!.clone() as Calendar
        date.set(Calendar.HOUR_OF_DAY, hour)
        date.set(Calendar.MINUTE, minute)
        _state.update { it.copy(travelStart = date) }
        timePickerShown()
        viewModelScope.launch {
            setTravelStartUseCase(date)
        }
    }

    fun workStartClicked() {
        _state.update { it.copy(showTimePickerWorkStart = true) }
    }

    fun workStartPicked(hour: Int, minute: Int) {
        val date = state.value.date!!.clone() as Calendar
        date.set(Calendar.HOUR_OF_DAY, hour)
        date.set(Calendar.MINUTE, minute)
        _state.update { it.copy(workStart = date) }
        timePickerShown()
        viewModelScope.launch {
            setWorkStartUseCase(date)
        }
    }

    fun workEndClicked() {
        _state.update { it.copy(showTimePickerWorkEnd = true) }
    }

    fun workEndPicked(hour: Int, minute: Int) {
        val date = state.value.date!!.clone() as Calendar
        date.set(Calendar.HOUR_OF_DAY, hour)
        date.set(Calendar.MINUTE, minute)
        _state.update { it.copy(workEnd = date) }
        timePickerShown()
        viewModelScope.launch {
            setWorkEndUseCase(date)
        }
    }

    fun travelEndClicked() {
        _state.update { it.copy(showTimePickerTravelEnd = true) }
    }

    fun travelEndPicked(hour: Int, minute: Int) {
        val date = state.value.date!!.clone() as Calendar
        date.set(Calendar.HOUR_OF_DAY, hour)
        date.set(Calendar.MINUTE, minute)
        _state.update { it.copy(travelEnd = date) }
        timePickerShown()
        viewModelScope.launch {
            setTravelEndUseCase(date)
        }
    }

    fun timePickerShown() {
        _state.update {
            it.copy(
                showTimePickerTravelEnd = false,
                showTimePickerTravelStart = false,
                showTimePickerWorkEnd = false,
                showTimePickerWorkStart = false
            )
        }
    }

    fun breakDurationChanged(breakDuration: String) {
            breakDuration.toFloatWithString().let { resourceWithString ->
                when (resourceWithString) {
                    is ResourceWithString.Error -> {
                        viewModelScope.launch {
                            _userMessage.emit(resourceWithString.message ?: "Error in Break Time")
                        }
                        _state.update { it.copy(breakDuration = resourceWithString.string ?: "") }
                    }
                    is ResourceWithString.Loading -> Unit
                    is ResourceWithString.Success -> {
                        _state.update { it.copy(breakDuration = resourceWithString.string ?: "") }
                        viewModelScope.launch {
                            setSavedBreakDurationUseCase(resourceWithString.data)
                        }
                    }
                }
            }
    }

    fun travelDistanceChanged(travelDistance: String) {
            travelDistance.toIntWithString().let { resourceWithString ->
                when (resourceWithString) {
                    is ResourceWithString.Error -> {
                        viewModelScope.launch {
                            _userMessage.emit(resourceWithString.message ?: "Error")
                        }
                    }
                    is ResourceWithString.Loading -> Unit
                    is ResourceWithString.Success -> {
                        viewModelScope.launch {
                            setTravelDistanceUseCase(resourceWithString.data)
                        }
                    }
                }
                _state.update { it.copy(traveledDistance = resourceWithString.string ?: "") }
            }
    }

    fun travelDayChanged(travelDaySelected: Boolean) {
        _state.update { it.copy(travelDay = travelDaySelected, noWorkday = false) }
        viewModelScope.launch {
            setIsSavedTravelDayUseCase(travelDaySelected)
        }
    }

    fun noWorkDayChanged(noWorkDaySelected: Boolean) {
        _state.update { it.copy(noWorkday = noWorkDaySelected, travelDay = false) }
        viewModelScope.launch {
            setIsWeekendUseCase(noWorkDaySelected)
        }
    }

    fun overTimeChanged(overTimeSelected: Boolean) {
        _state.update { it.copy(overTimeDay = overTimeSelected) }
        viewModelScope.launch {
            setIsOverTimeUseCase(overTimeSelected)
        }
    }


}