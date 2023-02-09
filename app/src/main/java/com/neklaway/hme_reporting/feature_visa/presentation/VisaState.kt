package com.neklaway.hme_reporting.feature_visa.presentation

import com.neklaway.hme_reporting.feature_visa.domain.model.Visa
import java.util.*

data class VisaState(
    val visas: List<Visa> = emptyList(),
    val loading: Boolean = false,
    val selectedVisa: Visa? = null,
    val country: String = "",
    val date: Calendar? = null,
    val showDatePicker: Boolean = false,
    val warningDays: Int = 0
)
