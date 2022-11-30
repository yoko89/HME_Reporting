package com.neklaway.hme_reporting.feature_time_sheet.presentation.edit_time_sheet

import java.util.*

data class EditTimeSheetState(
    val hmeId: Long? = null,
    val ibauID: Long? = null,
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
    val timeSheetId : Long = -1,
)
