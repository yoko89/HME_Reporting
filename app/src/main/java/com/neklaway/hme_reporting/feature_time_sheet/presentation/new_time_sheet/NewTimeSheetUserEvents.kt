package com.neklaway.hme_reporting.feature_time_sheet.presentation.new_time_sheet

import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.model.IBAUCode

sealed class NewTimeSheetUserEvents{
    object DateClicked:NewTimeSheetUserEvents()
    object TravelStartClicked:NewTimeSheetUserEvents()
    object WorkStartClicked:NewTimeSheetUserEvents()
    object WorkEndClicked:NewTimeSheetUserEvents()
    object TravelEndClicked:NewTimeSheetUserEvents()
    class DatePicked(val year:Int,val month:Int,val day:Int):NewTimeSheetUserEvents()
    object DatePickedCanceled:NewTimeSheetUserEvents()
    class TravelStartPicked(val hour:Int,val minute:Int):NewTimeSheetUserEvents()
    class TravelEndPicked(val hour:Int,val minute:Int):NewTimeSheetUserEvents()
    class WorkStartPicked(val hour:Int,val minute:Int):NewTimeSheetUserEvents()
    class WorkEndPicked(val hour:Int,val minute:Int):NewTimeSheetUserEvents()
    object TimePickerShown:NewTimeSheetUserEvents()
    object InsertTimeSheet:NewTimeSheetUserEvents()
    class CustomerSelected(val customer:Customer):NewTimeSheetUserEvents()
    class HmeSelected(val hmeCode: HMECode):NewTimeSheetUserEvents()
    class IbauSelected(val ibauCode: IBAUCode) : NewTimeSheetUserEvents()
    class TravelDayChanged(val changed:Boolean): NewTimeSheetUserEvents()
    class NoWorkDayChanged(val changed:Boolean): NewTimeSheetUserEvents()
    class OverTimeChanged(val changed:Boolean): NewTimeSheetUserEvents()
    class BreakDurationChanged(val duration:String): NewTimeSheetUserEvents()
    class TravelDistanceChanged(val distance:String): NewTimeSheetUserEvents()

}
