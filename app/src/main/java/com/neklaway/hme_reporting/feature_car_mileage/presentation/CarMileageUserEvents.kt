package com.neklaway.hme_reporting.feature_car_mileage.presentation

import com.neklaway.hme_reporting.feature_car_mileage.domain.model.CarMileage

sealed class CarMileageUserEvents{
    object StartDateClicked:CarMileageUserEvents()
    object EndDateClicked:CarMileageUserEvents()
    object StartTimeClicked:CarMileageUserEvents()
    object EndTimeClicked:CarMileageUserEvents()
    class StartDatePicked(val year:Int,val month:Int,val day:Int):CarMileageUserEvents()
    object DateTimePickedHide:CarMileageUserEvents()
    class StartTimePicked(val hour:Int,val minute:Int):CarMileageUserEvents()
    class EndDatePicked(val year:Int,val month:Int,val day:Int):CarMileageUserEvents()
    class EndTimePicked(val hour:Int,val minute:Int):CarMileageUserEvents()
    object UpdateCarMileage:CarMileageUserEvents()
    object SaveCarMileage:CarMileageUserEvents()
    class StartMileageChanged(val mileage:String):CarMileageUserEvents()
    class EndMileageChanged(val mileage:String):CarMileageUserEvents()
    class CarMileageClicked(val carMileage: CarMileage):CarMileageUserEvents()
    class DeleteCarMileage(val carMileage: CarMileage):CarMileageUserEvents()
}
