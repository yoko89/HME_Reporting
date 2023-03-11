package com.neklaway.hme_reporting.utils

import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.Expanse
import javax.inject.Inject

class CalculateExpanse @Inject constructor() {

    operator fun invoke(expanseList:List<Expanse>): Float {
        val cashExpanses = expanseList.filter { it.personallyPaid }
        var totalCash = 0f
        cashExpanses.forEach {
            totalCash += it.amountAED
        }
        return totalCash
    }
}