package com.neklaway.hme_reporting.utils

import com.neklaway.hme_reporting.feature_settings.domain.use_cases.allowance.Get8HDayAllowanceUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.allowance.GetFullDayAllowanceUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.allowance.GetNoAllowanceUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.allowance.GetSavingDeductibleUseCase
import javax.inject.Inject

class CalculateAllowance @Inject constructor(
    private val getFullDayAllowanceUseCase: GetFullDayAllowanceUseCase,
    private val getSavingDeductibleUseCase: GetSavingDeductibleUseCase,
    private val get8HDayAllowanceUseCase: Get8HDayAllowanceUseCase,
    private val getNoAllowanceUseCase: GetNoAllowanceUseCase
) {

    suspend operator fun invoke(fullDay:Int, lessThan24H:Int, noAllowance:Int): Float {
        return((fullDay*(getFullDayAllowanceUseCase()-getSavingDeductibleUseCase()))
                + (lessThan24H*get8HDayAllowanceUseCase())
                +(noAllowance*getNoAllowanceUseCase())).toFloat()
    }
}