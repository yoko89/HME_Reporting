package com.neklaway.hme_reporting.feature_visa.presentation

import com.neklaway.hme_reporting.feature_visa.domain.model.Visa

sealed class VisaUserEvents {
    object DateClicked : VisaUserEvents()
    object DatePickedCanceled : VisaUserEvents()
    object UpdateVisa : VisaUserEvents()
    object SaveVisa : VisaUserEvents()
    class DatePicked(val year:Int,val month:Int,val day:Int):VisaUserEvents()
    class CountryChanged(val country:String):VisaUserEvents()
    class VisaClicked(val visa: Visa):VisaUserEvents()
    class DeleteVisa(val visa: Visa):VisaUserEvents()
    class VisaSelected(val visa: Visa,val checked:Boolean):VisaUserEvents()
}
