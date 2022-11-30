package com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.saved_data_use_case.customer_id

import com.neklaway.hme_reporting.feature_time_sheet.domain.repository.SavedDataRepository
import javax.inject.Inject

class SetCustomerIdUseCase @Inject constructor(
    private val savedDataRepository: SavedDataRepository
) {

    suspend operator fun invoke(customerId: Long) {
        savedDataRepository.setCustomerId(customerId)
    }
}