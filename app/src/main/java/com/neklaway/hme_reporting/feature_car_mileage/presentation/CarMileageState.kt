package com.neklaway.hme_reporting.feature_car_mileage.presentation

import com.neklaway.hme_reporting.feature_car_mileage.domain.model.CarMileage
import java.util.*

data class CarMileageState(
    val carMileageList: List<CarMileage> = emptyList(),
    val loading: Boolean = false,
    val selectedCarMileage: CarMileage? = null,
    val showStartDatePicker: Boolean = false,
    val showEndDatePicker: Boolean = false,
    val showStartTimePicker: Boolean = false,
    val showEndTimePicker: Boolean = false,
    val startDate: Calendar? = null,
    val startTime: Calendar? = null,
    val startMileage: String = "",
    val endDate: Calendar? = null,
    val endTime: Calendar? = null,
    val endMileage: String = "",
)
