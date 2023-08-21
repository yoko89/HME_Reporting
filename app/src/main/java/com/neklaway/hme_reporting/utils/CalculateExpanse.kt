package com.neklaway.hme_reporting.utils

import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.Expense
import javax.inject.Inject

class CalculateExpanse @Inject constructor() {

    operator fun invoke(expenseList:List<Expense>): Float {
        val cashExpanses = expenseList.filter { it.personallyPaid }
        var totalCash = 0f
        cashExpanses.forEach {
            totalCash += it.amountAED
        }
        return totalCash
    }
}