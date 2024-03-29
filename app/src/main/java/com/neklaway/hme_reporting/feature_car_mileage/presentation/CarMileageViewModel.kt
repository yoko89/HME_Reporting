package com.neklaway.hme_reporting.feature_car_mileage.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.car_mileage.end_date.GetCarMileageEndDateUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.car_mileage.end_date.SetCarMileageEndDateUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.car_mileage.end_mileage.GetCarMileageEndMileageUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.car_mileage.end_mileage.SetCarMileageEndMileageUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.car_mileage.end_time.GetCarMileageEndTimeUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.car_mileage.end_time.SetCarMileageEndTimeUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.car_mileage.start_Mileage.GetCarMileageStartMileageUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.car_mileage.start_Mileage.SetCarMileageStartMileageUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.car_mileage.start_date.GetCarMileageStartDateUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.car_mileage.start_date.SetCarMileageStartDateUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.car_mileage.start_time.GetCarMileageStartTimeUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.saved_data_use_case.car_mileage.start_time.SetCarMileageStartTimeUseCase
import com.neklaway.hme_reporting.feature_car_mileage.domain.model.CarMileage
import com.neklaway.hme_reporting.feature_car_mileage.domain.use_cases.DeleteCarMileageUseCase
import com.neklaway.hme_reporting.feature_car_mileage.domain.use_cases.GetAllCarMileageFlowUseCase
import com.neklaway.hme_reporting.feature_car_mileage.domain.use_cases.InsertCarMileageUseCase
import com.neklaway.hme_reporting.feature_car_mileage.domain.use_cases.UpdateCarMileageUseCase
import com.neklaway.hme_reporting.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

private const val TAG = "Car Mileage ViewModel"

@HiltViewModel
class CarMileageViewModel @Inject constructor(
    private val deleteCarMileageUseCase: DeleteCarMileageUseCase,
    private val getAllCarMileageFlowUseCase: GetAllCarMileageFlowUseCase,
    private val insertCarMileage: InsertCarMileageUseCase,
    private val updateCarMileageUseCase: UpdateCarMileageUseCase,
    private val getCarMileageStartDateUseCase: GetCarMileageStartDateUseCase,
    private val getCarMileageStartTimeUseCase: GetCarMileageStartTimeUseCase,
    private val getCarMileageStartMileageUseCase: GetCarMileageStartMileageUseCase,
    private val getCarMileageEndDateUseCase: GetCarMileageEndDateUseCase,
    private val getCarMileageEndTimeUseCase: GetCarMileageEndTimeUseCase,
    private val getCarMileageEndMileageUseCase: GetCarMileageEndMileageUseCase,
    private val setCarMileageStartDateUseCase: SetCarMileageStartDateUseCase,
    private val setCarMileageStartTimeUseCase: SetCarMileageStartTimeUseCase,
    private val setCarMileageStartMileageUseCase: SetCarMileageStartMileageUseCase,
    private val setCarMileageEndDateUseCase: SetCarMileageEndDateUseCase,
    private val setCarMileageEndTimeUseCase: SetCarMileageEndTimeUseCase,
    private val setCarMileageEndMileageUseCase: SetCarMileageEndMileageUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CarMileageState())
    val state = _state.asStateFlow()

    private val _userMessage = Channel<String>()
    val userMessage = _userMessage.receiveAsFlow()


    init {
        viewModelScope.launch {
            getCarMileages()
            getSavedData()
        }
    }

    private fun getSavedData() {
        viewModelScope.launch {
            val carMileageStartDate = getCarMileageStartDateUseCase()
            val carMileageStartTime = getCarMileageStartTimeUseCase()
            val carMileageStartMileage = getCarMileageStartMileageUseCase()
            val carMileageEndDate = getCarMileageEndDateUseCase()
            val carMileageEndTime = getCarMileageEndTimeUseCase()
            val carMileageEndMileage = getCarMileageEndMileageUseCase()

            _state.update {
                it.copy(
                    startTime = carMileageStartTime,
                    startDate = carMileageStartDate,
                    startMileage = carMileageStartMileage?.toString() ?: "",
                    endDate = carMileageEndDate,
                    endTime = carMileageEndTime,
                    endMileage = carMileageEndMileage?.toString() ?: "",
                )
            }
        }
    }

    private fun getCarMileages() {

        getAllCarMileageFlowUseCase().onEach { result ->
            when (result) {
                is Resource.Error -> {
                    _userMessage.send(result.message ?: "Can't get Car Mileages")
                    _state.update { it.copy(loading = false) }
                }

                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            carMileageList = result.data.orEmpty()
                                .sortedWith(
                                    compareBy<CarMileage>(
                                        { carMileage ->
                                            carMileage.startDate
                                        },
                                        { carMileage ->
                                            carMileage.startTime
                                        }).reversed()
                                ),
                            loading = false
                        )
                    }
                }
            }

        }.launchIn(viewModelScope)

    }


    private fun saveCarMileage() {
        val startDate = state.value.startDate
        val startTime = state.value.startTime
        val startMileage = state.value.startMileage.toLongOrNull()
        val endDate = state.value.endDate
        val endTime = state.value.endTime
        val endMileage = state.value.endMileage.toLongOrNull()

        insertCarMileage(
            startMileage, startDate, startTime, endDate, endTime, endMileage
        ).onEach { result ->
            when (result) {
                is Resource.Error -> {
                    _userMessage.send(result.message ?: "Can't save Car Mileage")
                    _state.update { it.copy(loading = false) }
                }

                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> clearState()

            }
        }.launchIn(viewModelScope)
    }

    private fun clearState() {
        _state.update {
            it.copy(
                loading = false,
                startDate = null,
                startTime = null,
                startMileage = "",
                endDate = null,
                endTime = null,
                endMileage = ""
            )
        }

        viewModelScope.launch {
            setCarMileageStartTimeUseCase(null)
            setCarMileageStartDateUseCase(null)
            setCarMileageEndMileageUseCase(null)
            setCarMileageStartMileageUseCase(null)
            setCarMileageEndDateUseCase(null)
            setCarMileageEndTimeUseCase(null)
        }
    }


    private fun updateCarMileage() {
        val startDate = state.value.startDate
        val startTime = state.value.startTime
        val startMileage = state.value.startMileage.toLongOrNull()
        val endDate = state.value.endDate
        val endTime = state.value.endTime
        val endMileage = state.value.endMileage.toLongOrNull()

        val selectedCarMileage = state.value.selectedCarMileage

        if (selectedCarMileage != null) {
            updateCarMileageUseCase(
                startMileage,
                startDate,
                startTime,
                endDate,
                endTime,
                endMileage,
                selectedCarMileage.id
            ).onEach { result ->
                when (result) {
                    is Resource.Error -> {
                        _userMessage.send(result.message ?: "Can't update Car Mileage")
                        _state.update { it.copy(loading = false) }
                    }

                    is Resource.Loading -> _state.update { it.copy(loading = true) }
                    is Resource.Success -> clearState()
                }
            }.launchIn(viewModelScope)
        }
    }


    private fun deleteCarMileage(carMileage: CarMileage) {

        deleteCarMileageUseCase(carMileage).onEach { result ->
            when (result) {
                is Resource.Error -> {
                    _userMessage.send(result.message ?: "Can't delete Car Mileage")
                    _state.update { it.copy(loading = false) }
                }

                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> clearState()
            }
        }.launchIn(viewModelScope)
    }


    private fun carMileageClicked(carMileage: CarMileage) {
        _state.update {
            it.copy(
                startMileage = carMileage.startMileage.toString(),
                startDate = carMileage.startDate,
                startTime = carMileage.startTime,
                endMileage = carMileage.endMileage.toString(),
                endDate = carMileage.endDate,
                endTime = carMileage.endTime,
                selectedCarMileage = carMileage
            )
        }
    }

    private fun startMileageChanged(startMileage: String) {
        var startMileageLong: Long?
        try {
            startMileageLong = startMileage.toLong()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            startMileageLong = null
            if (startMileage.isNotBlank())
                viewModelScope.launch {
                    _userMessage.send("Error in Start mileage Distance " + e.message)
                }
        }
        viewModelScope.launch {
            setCarMileageStartMileageUseCase(startMileageLong)
        }
        val startMileageString = startMileageLong?.toString() ?: ""

        _state.update { it.copy(startMileage = startMileageString) }
    }

    private fun endMileageChanged(endMileage: String) {
        var endMileageLong: Long?
        try {
            endMileageLong = endMileage.toLong()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            endMileageLong = null
            if (endMileage.isNotBlank())
                viewModelScope.launch {
                    _userMessage.send("Error in End mileage Distance " + e.message)
                }
        }
        viewModelScope.launch {
            setCarMileageEndMileageUseCase(endMileageLong)
        }
        val endMileageString = endMileageLong?.toString() ?: ""

        _state.update { it.copy(endMileage = endMileageString) }
    }


    private fun startDateClicked() {
        _state.update { it.copy(showStartDatePicker = true) }
        Log.d(TAG, "startDateClicked: ")
    }

    private fun startDatePicked(year: Int, month: Int, day: Int) {
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
        _state.update { it.copy(startDate = date) }
        state.value.startTime?.let {startTime->
            startTime.set(year,month,day)
            _state.update { it.copy(startTime = startTime) }
        }
        dateTimePickedHide()
        viewModelScope.launch { setCarMileageStartDateUseCase(date) }
    }

    private fun endDateClicked() {
        _state.update { it.copy(showEndDatePicker = true) }
        Log.d(TAG, "endDateClicked: ")
    }

    private fun endDatePicked(year: Int, month: Int, day: Int) {
        val date = Calendar.getInstance()
        TimeZone.getTimeZone("Asia/Dubai")
        date.set(
            year,
            month,
            day,
            0,
            0,
            0
        )
        date.set(Calendar.MILLISECOND, 0)
        _state.update { it.copy(endDate = date) }
        state.value.endTime?.let {endTime->
            endTime.set(year,month,day)
            _state.update { it.copy(endTime = endTime) }
        }
        dateTimePickedHide()
        viewModelScope.launch { setCarMileageEndDateUseCase(date) }
    }

    private fun dateTimePickedHide() {
        _state.update {
            it.copy(
                showStartDatePicker = false,
                showEndDatePicker = false,
                showEndTimePicker = false,
                showStartTimePicker = false
            )
        }
    }

    private fun startTimeClicked() {
        _state.update { it.copy(showStartTimePicker = true) }
    }

    private fun startTimePicked(hour: Int, minute: Int) {
        val date = state.value.startDate!!.clone() as Calendar
        date.set(Calendar.HOUR_OF_DAY, hour)
        date.set(Calendar.MINUTE, minute)
        _state.update { it.copy(startTime = date) }
        dateTimePickedHide()
        viewModelScope.launch {
            setCarMileageStartTimeUseCase(date)
        }
    }

    private fun endTimeClicked() {
        _state.update { it.copy(showEndTimePicker = true) }
    }

    private fun endTimePicked(hour: Int, minute: Int) {
        val date = state.value.endDate!!.clone() as Calendar
        date.set(Calendar.HOUR_OF_DAY, hour)
        date.set(Calendar.MINUTE, minute)
        _state.update { it.copy(endTime = date) }
        dateTimePickedHide()
        viewModelScope.launch {
            setCarMileageEndTimeUseCase(date)
        }
    }

    fun userEvent(event: CarMileageUserEvents) {
        when (event) {
            is CarMileageUserEvents.CarMileageClicked -> carMileageClicked(event.carMileage)
            CarMileageUserEvents.DateTimePickedHide -> dateTimePickedHide()
            is CarMileageUserEvents.DeleteCarMileage -> deleteCarMileage(event.carMileage)
            CarMileageUserEvents.EndDateClicked -> endDateClicked()
            is CarMileageUserEvents.EndDatePicked -> endDatePicked(
                event.year,
                event.month,
                event.day
            )

            is CarMileageUserEvents.EndMileageChanged -> endMileageChanged(event.mileage)
            CarMileageUserEvents.EndTimeClicked -> endTimeClicked()
            is CarMileageUserEvents.EndTimePicked -> endTimePicked(event.hour, event.minute)
            CarMileageUserEvents.SaveCarMileage -> saveCarMileage()
            CarMileageUserEvents.StartDateClicked -> startDateClicked()
            is CarMileageUserEvents.StartDatePicked -> startDatePicked(
                event.year,
                event.month,
                event.day
            )

            is CarMileageUserEvents.StartMileageChanged -> startMileageChanged(event.mileage)
            CarMileageUserEvents.StartTimeClicked -> startTimeClicked()
            is CarMileageUserEvents.StartTimePicked -> startTimePicked(event.hour, event.minute)
            CarMileageUserEvents.UpdateCarMileage -> updateCarMileage()
        }
    }

}