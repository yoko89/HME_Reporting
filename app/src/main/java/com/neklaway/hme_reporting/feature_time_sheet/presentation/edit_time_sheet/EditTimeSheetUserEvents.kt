package com.neklaway.hme_reporting.feature_time_sheet.presentation.edit_time_sheet

import com.neklaway.hme_reporting.common.domain.model.Customer
import com.neklaway.hme_reporting.common.domain.model.HMECode
import com.neklaway.hme_reporting.common.domain.model.IBAUCode

sealed class EditTimeSheetUserEvents {
    object DateClicked : EditTimeSheetUserEvents()
    object TravelStartClicked : EditTimeSheetUserEvents()
    object WorkStartClicked : EditTimeSheetUserEvents()
    object WorkEndClicked : EditTimeSheetUserEvents()
    object TravelEndClicked : EditTimeSheetUserEvents()
    class DatePicked(val year:Int,val month:Int,val day:Int): EditTimeSheetUserEvents()
    class TravelStartPicked(val hour:Int,val minute:Int): EditTimeSheetUserEvents()
    class TravelEndPicked(val hour:Int,val minute:Int): EditTimeSheetUserEvents()
    class WorkStartPicked(val hour:Int,val minute:Int): EditTimeSheetUserEvents()
    class WorkEndPicked(val hour:Int,val minute:Int): EditTimeSheetUserEvents()
    object TimePickerShown: EditTimeSheetUserEvents()
    class HmeSelected(val hmeCode: HMECode): EditTimeSheetUserEvents()
    class IbauSelected(val ibauCode: IBAUCode) : EditTimeSheetUserEvents()
    class TravelDayChanged(val changed:Boolean): EditTimeSheetUserEvents()
    class NoWorkDayChanged(val changed:Boolean): EditTimeSheetUserEvents()
    class BreakDurationChanged(val duration:String): EditTimeSheetUserEvents()
    class TravelDistanceChanged(val distance:String): EditTimeSheetUserEvents()
    object DateShown:EditTimeSheetUserEvents()
    object DeleteTimeSheet:EditTimeSheetUserEvents()
    object UpdateTimeSheet:EditTimeSheetUserEvents()
}
