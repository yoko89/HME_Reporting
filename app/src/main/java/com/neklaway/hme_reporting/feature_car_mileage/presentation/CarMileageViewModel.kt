package com.neklaway.hme_reporting.feature_car_mileage.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neklaway.hme_reporting.common.domain.model.CarMileage
import com.neklaway.hme_reporting.common.domain.use_cases.car_mileage_use_cases.DeleteCarMileageUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.car_mileage_use_cases.GetAllCarMileageFlowUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.car_mileage_use_cases.InsertCarMileageUseCase
import com.neklaway.hme_reporting.common.domain.use_cases.car_mileage_use_cases.UpdateCarMileageUseCase
import com.neklaway.hme_reporting.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
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
    //Todo("Get saved Data")
) : ViewModel() {

    private val _state = MutableStateFlow(CarMileageState())
    val state = _state.asStateFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage


    init {
        viewModelScope.launch {
            getCarMileages()
            //TODO("Get saved data")
        }
    }

    private fun getCarMileages() {

        getAllCarMileageFlowUseCase().onEach { result ->
            when (result) {
                is Resource.Error -> {
                    _userMessage.emit(result.message ?: "Can't get Car Mileages")
                    _state.update { it.copy(loading = false) }
                }
                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            carMileageList = result.data.orEmpty()
                                .sortedWith(compareBy<CarMileage>({ it.startDate }, { it.startTime }).reversed()),
                            loading = false
                        )
                    }
                }
            }

        }.launchIn(viewModelScope)

    }


    fun saveCarMileage() {
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
                    _userMessage.emit(result.message ?: "Can't save Car Mileage")
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
    }


    fun updateCarMileage() {
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
                        _userMessage.emit(result.message ?: "Can't update Car Mileage")
                        _state.update { it.copy(loading = false) }
                    }
                    is Resource.Loading -> _state.update { it.copy(loading = true) }
                    is Resource.Success -> clearState()
                }
            }.launchIn(viewModelScope)
        }
    }


    fun deleteCarMileage(carMileage: CarMileage) {

        deleteCarMileageUseCase(carMileage).onEach { result ->
            when (result) {
                is Resource.Error -> {
                    _userMessage.emit(result.message ?: "Can't delete Car Mileage")
                    _state.update { it.copy(loading = false) }
                }
                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> clearState()
            }
        }.launchIn(viewModelScope)
    }


    fun carMileageClicked(carMileage: CarMileage) {
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

    fun startMileageChanged(startMileage: String) {
        var startMileageLong: Long?
        try {
            startMileageLong = startMileage.toLong()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            startMileageLong = null
            if (startMileage.isNotBlank())
                viewModelScope.launch {
                    _userMessage.emit("Error in Start mileage Distance " + e.message)
                }
        }
        viewModelScope.launch {
            // TODO("save start Mileage")
        }
        val startMileageString = startMileageLong?.toString() ?: ""

        _state.update { it.copy(startMileage = startMileageString) }
    }

    fun endMileageChanged(endMileage: String) {
        var endMileageLong: Long?
        try {
            endMileageLong = endMileage.toLong()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            endMileageLong = null
            if (endMileage.isNotBlank())
                viewModelScope.launch {
                    _userMessage.emit("Error in End mileage Distance " + e.message)
                }
        }
        viewModelScope.launch {
            // TODO("save end Mileage")
        }
        val endMileageString = endMileageLong?.toString() ?: ""

        _state.update { it.copy(endMileage = endMileageString) }
    }


    fun startDateClicked() {
        _state.update { it.copy(showStartDatePicker = true) }
        Log.d(TAG, "startDateClicked: ")
    }

    fun startDatePicked(year: Int, month: Int, day: Int) {
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
        _state.update { it.copy(startDate = date) }
        dateTimePickedHide()
    }

    fun endDateClicked() {
        _state.update { it.copy(showEndDatePicker = true) }
        Log.d(TAG, "endDateClicked: ")
    }

    fun endDatePicked(year: Int, month: Int, day: Int) {
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
        _state.update { it.copy(endDate = date) }
        dateTimePickedHide()
    }

    fun dateTimePickedHide() {
        _state.update {
            it.copy(
                showStartDatePicker = false,
                showEndDatePicker = false,
                showEndTimePicker = false,
                showStartTimePicker = false
            )
        }
    }

    fun startTimeClicked() {
        _state.update { it.copy(showStartTimePicker = true) }
    }

    fun startTimePicked(hour: Int, minute: Int) {
        val date = state.value.startDate!!.clone() as Calendar
        date.set(Calendar.HOUR_OF_DAY, hour)
        date.set(Calendar.MINUTE, minute)
        _state.update { it.copy(startTime = date) }
        dateTimePickedHide()
        viewModelScope.launch {
            //TODO("save Time")
        }
    }

    fun endTimeClicked() {
        _state.update { it.copy(showEndTimePicker = true) }
    }

    fun endTimePicked(hour: Int, minute: Int) {
        val date = state.value.endDate!!.clone() as Calendar
        date.set(Calendar.HOUR_OF_DAY, hour)
        date.set(Calendar.MINUTE, minute)
        _state.update { it.copy(endTime = date) }
        dateTimePickedHide()
        viewModelScope.launch {
            //TODO("save Time")
        }
    }

}