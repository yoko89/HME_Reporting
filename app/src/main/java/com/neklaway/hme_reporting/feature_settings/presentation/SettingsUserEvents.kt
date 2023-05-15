package com.neklaway.hme_reporting.feature_settings.presentation

import android.net.Uri
import com.neklaway.hme_reporting.utils.DarkTheme
import com.neklaway.hme_reporting.utils.Theme

sealed class SettingsUserEvents{
    class RestoreFolderSelected(val uri:Uri):SettingsUserEvents()
    class SetUserName(val userName:String):SettingsUserEvents()
    class SetIsIbau(val ibau:Boolean):SettingsUserEvents()
    class SetAutoClear(val autoClear:Boolean):SettingsUserEvents()
    class SetVisaReminder(val reminder:String):SettingsUserEvents()
    class BreakDurationChanged(val duration:String):SettingsUserEvents()
    class SetFullDayAllowance(val allowance:String):SettingsUserEvents()
    class Set8HAllowance(val allowance:String):SettingsUserEvents()
    class SetSavingDeductible(val deductible:String):SettingsUserEvents()
    class SetTheme(val theme: Theme):SettingsUserEvents()
    class SetDarkTheme(val darkTheme: DarkTheme):SettingsUserEvents()
    object SignatureBtnClicked:SettingsUserEvents()
    object BackupButtonClicked:SettingsUserEvents()
    object SignatureScreenClosed:SettingsUserEvents()
    object UpdateSignature:SettingsUserEvents()
}
