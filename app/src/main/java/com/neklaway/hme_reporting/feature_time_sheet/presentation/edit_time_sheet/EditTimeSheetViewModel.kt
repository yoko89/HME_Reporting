package com.neklaway.hme_reporting.feature_time_sheet.presentation.edit_time_sheet

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.model.IBAUCode
import com.neklaway.hme_reporting.common.domain.model.TimeSheet
import com.neklaway.hme_reporting.common.domain.use_cases.hme_code_use_cases.GetHMECodeByCustomerIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.hme_code_use_cases.GetHMECodeByIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.ibau_code_use_cases.GetIBAUCodeByHMECodeIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.hme_id.SetHMEIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.ibau_id.GetIBAUIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.time_sheet.ibau_id.SetIBAUIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.time_sheet_use_cases.DeleteTimeSheetUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.time_sheet_use_cases.GetTimeSheetByIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.time_sheet_use_cases.UpdateTimeSheetUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.is_ibau.GetIsIbauUseCase
import com.neklaway.hme_reporting.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

private const val TAG = "EditTimeSheetViewModel"

@HiltViewModel
class EditTimeSheetViewModel @Inject constructor(
    private val updateTimeSheetUseCase: UpdateTimeSheetUseCase,
    private val getTimeSheetByIdUseCase: GetTimeSheetByIdUseCase,
    private val deleteTimeSheetUseCase: DeleteTimeSheetUseCase,
    private val getHMECodeByIdUseCase: GetHMECodeByIdUseCase,
    private val getHMECodeByCustomerIdUseCase: GetHMECodeByCustomerIdUseCase,
    private val setHMEIdUseCase: SetHMEIdUseCase,
    private val getIsIBAUUseCase: GetIsIbauUseCase,
    private val getIBAUCodeByHMECodeIdUseCase: GetIBAUCodeByHMECodeIdUseCase,
    private val getIBAUIdUseCase: GetIBAUIdUseCase,
    private val setIBAUIdUseCase: SetIBAUIdUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        const val TIME_SHEET_ID = "time_sheet_id"
    }

    private val _state = MutableStateFlow(EditTimeSheetState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<EditTimeSheetEvents>()
    val event: SharedFlow<EditTimeSheetEvents> = _event

    private var timeSheetId: Long = -1
    private lateinit var timeSheet: Deferred<TimeSheet?>

    init {
        getTimeSheet()
        getHmeCodes()
        viewModelScope.launch {
            _state.update { it.copy(isIbau = getIsIBAUUseCase.invoke()) }
        }

    }

    private fun getTimeSheet() {
        timeSheetId = savedStateHandle[TIME_SHEET_ID] ?: -1
        _state.update { it.copy(timeSheetId = timeSheetId) }
        Log.d(TAG, ": ${state.value.timeSheetId}")
        var returnedTimesheet: TimeSheet? = null


        timeSheet = viewModelScope.async(Dispatchers.IO) {
            getTimeSheetByIdUseCase(timeSheetId).collect { result ->
                Log.d(TAG, "TimeSheet: $result")
                when (result) {
                    is Resource.Error -> {
                        _event.emit(
                            EditTimeSheetEvents.UserMessage(
                                result.message ?: "Error can't retrieve Data "
                            )
                        )
                        _state.update { it.copy(loading = false) }
                    }
                    is Resource.Loading -> _state.update { it.copy(loading = true) }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                date = result.data?.date,
                                travelStart = result.data?.travelStart,
                                travelEnd = result.data?.travelEnd,
                                workStart = result.data?.workStart,
                                workEnd = result.data?.workEnd,
                                traveledDistance = result.data?.traveledDistance?.toString() ?: "",
                                breakDuration = result.data?.breakDuration?.toString() ?: "",
                                travelDay = result.data?.travelDay ?: false,
                                noWorkday = result.data?.noWorkDay ?: false,
                                overTimeDay = result.data?.overTimeDay ?: false,
                                loading = false
                            )
                        }
                        returnedTimesheet = result.data
                    }
                }
            }
            return@async returnedTimesheet
        }
    }

    private fun getHmeCodes() {
        viewModelScope.launch {
            val timeSheet = timeSheet.await() ?: return@launch

            getHMECodeByIdUseCase(timeSheet.HMEId).collect { result ->
                when (result) {
                    is Resource.Error -> {
                        _event.emit(
                            EditTimeSheetEvents.UserMessage(
                                result.message ?: "Error can't retrieve Data "
                            )
                        )
                        _state.update { it.copy(loading = false) }
                    }
                    is Resource.Loading -> _state.update { it.copy(loading = true) }
                    is Resource.Success -> {
                        val currentHmeCode = result.data
                        val customer = result.data?.customerId
                        customer?.let { customerId ->
                            getHMECodeByCustomerIdUseCase(customerId).collect { result ->
                                when (result) {
                                    is Resource.Error -> {
                                        _event.emit(
                                            EditTimeSheetEvents.UserMessage(
                                                result.message ?: "Error can't retrieve Data "
                                            )
                                        )
                                        _state.update { it.copy(loading = false) }
                                    }
                                    is Resource.Loading -> _state.update { it.copy(loading = true) }
                                    is Resource.Success -> {
                                        _state.update {
                                            it.copy(
                                                hmeCodes = result.data ?: emptyList(),
                                                loading = false,
                                                selectedHMECode = currentHmeCode,
                                            )
                                        }
                                    }
                                }

                            }
                        }
                    }
                }

            }
        }

    }

    fun updateTimeSheet() {
        viewModelScope.launch {
            val timeSheet = timeSheet.await()
            if (timeSheet == null) {
                _event.emit(EditTimeSheetEvents.UserMessage("Can't retrieve TimeSheet"))
                return@launch
            }
            updateTimeSheetUseCase.invoke(
                HMEId = state.value.selectedHMECode?.id ?: timeSheet.HMEId,
                IBAUId = state.value.selectedIBAUCode?.id ?: timeSheet.IBAUId,
                date = state.value.date,
                travelStart = state.value.travelStart,
                workStart = state.value.workStart,
                workEnd = state.value.workEnd,
                travelEnd = state.value.travelEnd,
                breakDuration = state.value.breakDuration.toFloatOrNull(),
                traveledDistance = state.value.traveledDistance.toIntOrNull(),
                overTimeDay = state.value.overTimeDay,
                travelDay = state.value.travelDay,
                noWorkDay = state.value.noWorkday,
                id = state.value.timeSheetId,
                created = false,
                dailyAllowance = timeSheet.dailyAllowance
            ).collect { result ->
                when (result) {
                    is Resource.Error -> {
                        _event.emit(
                            EditTimeSheetEvents.UserMessage(
                                result.message ?: "Error can't update"
                            )
                        )
                        _state.update { it.copy(loading = false) }
                    }
                    is Resource.Loading -> _state.update { it.copy(loading = true) }
                    is Resource.Success -> {
                        _state.update { it.copy(loading = false) }
                        _event.emit(EditTimeSheetEvents.PopBackStack)
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
        var breakFloat: Float?

        try {
            breakFloat = breakDuration.toFloat()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            breakFloat = null
            if (breakDuration.isNotBlank()) {
                viewModelScope.launch {
                    _event.emit(EditTimeSheetEvents.UserMessage("Error in Break Time " + e.message))
                }
                _state.update { it.copy(loading = false) }
            }
        }
        _state.update { it.copy(breakDuration = if (breakFloat == null) "" else breakDuration) }
    }

    fun travelDistanceChanged(travelDistance: String) {
        var travelInt: Int?
        try {
            travelInt = travelDistance.toInt()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            travelInt = null
            if (travelDistance.isNotBlank()) {
                viewModelScope.launch {
                    _event.emit(EditTimeSheetEvents.UserMessage("Error in Travel Distance " + e.message))
                }
                _state.update { it.copy(loading = false) }
            }
        }
        val travelString = travelInt?.toString() ?: ""

        _state.update { it.copy(traveledDistance = travelString) }
    }

    fun travelDayChanged(travelDaySelected: Boolean) {
        _state.update { it.copy(travelDay = travelDaySelected, noWorkday = false) }
    }

    fun noWorkDayChanged(noWorkDaySelected: Boolean) {
        _state.update { it.copy(noWorkday = noWorkDaySelected, travelDay = false) }
    }

    fun dateShown() {
        _state.update { it.copy(showDatePicker = false) }
    }

    fun deleteTimeSheet() {
        viewModelScope.launch {
            val timeSheet = timeSheet.await()

            if (timeSheet == null) {
                _event.emit(EditTimeSheetEvents.UserMessage("Can't retrieve TimeSheet"))
                return@launch
            }

            deleteTimeSheetUseCase(timeSheet).collect { result ->
                when (result) {
                    is Resource.Error -> {
                        _event.emit(
                            EditTimeSheetEvents.UserMessage(
                                result.message ?: "Error can't delete TimeSheet"
                            )
                        )
                        _state.update { it.copy(loading = false) }
                    }
                    is Resource.Loading -> _state.update { it.copy(loading = true) }
                    is Resource.Success -> {
                        _event.emit(EditTimeSheetEvents.UserMessage("TimeSheet Deleted"))
                        _event.emit(EditTimeSheetEvents.PopBackStack)
                        _state.update { it.copy(loading = false) }

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
                            _event.emit(
                                EditTimeSheetEvents.UserMessage(
                                    result.message ?: "Can't get IBAU codes"
                                )
                            )
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
}