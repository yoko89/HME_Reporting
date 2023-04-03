package com.neklaway.hme_reporting.utils

import com.neklaway.hme_reporting.feature_settings.domain.use_cases.allowance.Get8HDayAllowanceUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.allowance.GetFullDayAllowanceUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.allowance.GetSavingDeductibleUseCase
import javax.inject.Inject

class CalculateAllowance @Inject constructor(
    private val getFullDayAllowanceUseCase: GetFullDayAllowanceUseCase,
    private val getSavingDeductibleUseCase: GetSavingDeductibleUseCase,
    private val get8HDayAllowanceUseCase: Get8HDayAllowanceUseCase,
) {

    suspend operator fun invoke(fullDay:Int, lessThan24H:Int): Float {
        return((fullDay*(getFullDayAllowanceUseCase()-getSavingDeductibleUseCase()))
                + (lessThan24H*get8HDayAllowanceUseCase())).toFloat()
    }
}