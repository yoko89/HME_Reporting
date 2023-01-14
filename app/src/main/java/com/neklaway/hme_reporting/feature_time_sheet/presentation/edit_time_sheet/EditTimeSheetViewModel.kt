package com.neklaway.hme_reporting.feature_time_sheet.presentation.edit_time_sheet

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neklaway.hme_reporting.common.domain.model.TimeSheet
import com.neklaway.hme_reporting.common.domain.use_cases.time_sheet_use_cases.DeleteTimeSheetUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.time_sheet_use_cases.GetTimeSheetByIdUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.time_sheet_use_cases.UpdateTimeSheetUseCase
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
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        const val TIME_SHEET_ID = "time_sheet_id"
    }

    private val _state = MutableStateFlow(EditTimeSheetState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<EditTimeSheetEvents>()
    val event: SharedFlow<EditTimeSheetEvents> = _event

    private val timeSheetId: Long
    var timeSheet:Deferred<TimeSheet?>

    init {

        timeSheetId =
            savedStateHandle[TIME_SHEET_ID]
                ?: -1
        _state.update { it.copy(timeSheetId = timeSheetId) }
        Log.d(TAG, ": ${state.value.timeSheetId}")

       timeSheet = viewModelScope.async(Dispatchers.IO) {
            var returnedTimesheet: TimeSheet? = null
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
                                travelDay = result.data?.travelDay?:false,
                                noWorkday = result.data?.noWorkDay?:false,
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


    fun updateTimeSheet() {
viewModelScope.launch {
    val timeSheet = timeSheet.await()
    updateTimeSheetUseCase.invoke(
        HMEId = timeSheet?.HMEId,
        IBAUId = timeSheet?.IBAUId,
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
        created = false
    ).collect{ result ->
        when(result){
            is Resource.Error -> {
                _event.emit(EditTimeSheetEvents.UserMessage(result.message ?: "Error can't update"))
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
        date.set(Calendar.MILLISECOND,0)
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

        if (breakDuration == ".") {
            _state.update { it.copy(breakDuration = "") }
            return
        }

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

        val breakString = breakFloat?.toString() ?: ""
        val breakDurationSplit = breakDuration.split(".")
        if (breakDurationSplit.size < 2)
            _state.update { it.copy(breakDuration = breakDuration) }
        else
            _state.update { it.copy(breakDuration = breakString) }
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
            timeSheet?.let {
                deleteTimeSheetUseCase(timeSheet).collect{result ->
                    when(result){
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
    }

}