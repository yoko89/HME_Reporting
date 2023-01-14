package com.neklaway.hme_reporting.feature_time_sheet.presentation.new_time_sheet

import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.model.IBAUCode
import java.util.*

data class NewTimeSheetState(
    val customers: List<Customer> = emptyList(),
    val selectedCustomer: Customer? = null,
    val hmeCodes: List<HMECode> = emptyList(),
    val selectedHMECode: HMECode? = null,
    val ibauCodes: List<IBAUCode> = emptyList(),
    val selectedIBAUCode: IBAUCode? = null,
    val noWorkday: Boolean = false,
    val travelDay: Boolean = false,
    val date: Calendar? = null,
    val travelStart: Calendar? = null,
    val workStart: Calendar? = null,
    val workEnd: Calendar? = null,
    val travelEnd: Calendar? = null,
    val breakDuration: String = "",
    val traveledDistance: String = "",
    val overTimeDay: Boolean = false,
    val created: Boolean = false,
    val loading: Boolean = false,
    val showDatePicker: Boolean = false,
    val showTimePickerTravelStart:Boolean = false,
    val showTimePickerWorkStart:Boolean = false,
    val showTimePickerWorkEnd:Boolean = false,
    val showTimePickerTravelEnd:Boolean = false,
    val isIbau:Boolean = false,
)
