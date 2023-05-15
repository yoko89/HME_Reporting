package com.neklaway.hme_reporting.feature_time_sheet.presentation.customer

import com.neklaway.hme_reporting.common.domain.model.Customer

sealed class CustomerUserEvents {
    object UpdateCustomer : CustomerUserEvents()
    object SaveCustomer : CustomerUserEvents()
    class CustomerNameChange(val customerName:String):CustomerUserEvents()
    class CustomerCityChange(val city:String):CustomerUserEvents()
    class CustomerCountryChange(val country:String):CustomerUserEvents()
    class CustomerSelected(val customer: Customer):CustomerUserEvents()
    class DeleteCustomer(val customer: Customer):CustomerUserEvents()
}
