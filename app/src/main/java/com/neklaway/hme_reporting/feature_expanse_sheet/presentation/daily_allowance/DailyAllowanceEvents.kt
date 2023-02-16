package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.daily_allowance

import java.io.File

sealed class DailyAllowanceEvents{
    class UserMessage(val message:String): DailyAllowanceEvents()
}
