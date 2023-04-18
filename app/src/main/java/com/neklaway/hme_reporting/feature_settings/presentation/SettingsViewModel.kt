package com.neklaway.hme_reporting.feature_settings.presentation

import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.allowance.*
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.backup.StartBackup
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.backup.StartRestore
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.break_time.GetBreakDurationUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.break_time.SetBreakDurationUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.dark_theme.GetDarkThemeUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.dark_theme.SetDarkThemeUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.is_auto_clear.GetIsAutoClearUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.is_auto_clear.SetIsAutoClearUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.is_ibau.GetIsIbauUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.is_ibau.SetIsIbauUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.theme.GetThemeUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.theme.SetThemeUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.user_name.GetUserNameUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.user_name.SetUserNameUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.visa_reminder.GetVisaReminderUseCase
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.visa_reminder.SetVisaReminderUseCase
import com.neklaway.hme_reporting.feature_signature.domain.use_cases.bitmap_use_case.LoadBitmapUseCase
import com.neklaway.hme_reporting.utils.DarkTheme
import com.neklaway.hme_reporting.utils.Resource
import com.neklaway.hme_reporting.utils.ResourceWithString
import com.neklaway.hme_reporting.utils.Theme
import com.neklaway.hme_reporting.utils.toFloatWithString
import com.neklaway.hme_reporting.utils.toIntWithString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "Settings_ViewModel"

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getUserNameUseCase: GetUserNameUseCase,
    private val setUserNameUseCase: SetUserNameUseCase,
    private val setIsIbauUseCase: SetIsIbauUseCase,
    private val getIsIbauUseCase: GetIsIbauUseCase,
    private val getBreakDurationUseCase: GetBreakDurationUseCase,
    private val setBreakDurationUseCase: SetBreakDurationUseCase,
    private val getIsAutoClearUseCase: GetIsAutoClearUseCase,
    private val setIsAutoClearUseCase: SetIsAutoClearUseCase,
    private val loadBitmapUseCase: LoadBitmapUseCase,
    private val getVisaReminderUseCase: GetVisaReminderUseCase,
    private val setVisaReminderUseCase: SetVisaReminderUseCase,
    private val startBackup: StartBackup,
    private val startRestore: StartRestore,
    private val getFullDayAllowanceUseCase: GetFullDayAllowanceUseCase,
    private val get8HDayAllowanceUseCase: Get8HDayAllowanceUseCase,
    private val setFullDayAllowanceUseCase: SetFullDayAllowanceUseCase,
    private val set8HAllowanceUseCase: Set8HAllowanceUseCase,
    private val setSavingDeductibleUseCase: SetSavingDeductibleUseCase,
    private val getSavingDeductibleUseCase: GetSavingDeductibleUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    private val getThemeUseCase: GetThemeUseCase,
    private val setDarkThemeUseCase: SetDarkThemeUseCase,
    private val getDarkThemeUseCase: GetDarkThemeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage

    init {
        getUserName()
        getIsIbau()
        getBreakDuration()
        getSignature()
        getIsAutoClear()
        getVisaReminder()
        getFullDayAllowance()
        get8HDayAllowance()
        getSavingDeductible()
        getTheme()
        getDarkTheme()
    }


    private fun getUserName() {
        viewModelScope.launch {
            val userName = getUserNameUseCase.invoke()
            _state.update { it.copy(userName = userName) }
        }
    }
    private fun getDarkTheme() {
        viewModelScope.launch {
            val theme = getDarkThemeUseCase.invoke().first()
            _state.update { it.copy(darkTheme = theme) }
        }
    }
    private fun getTheme() {
        viewModelScope.launch {
            val theme = getThemeUseCase.invoke().first()
            _state.update { it.copy(theme = theme) }
        }
    }

    private fun getIsIbau() {
        viewModelScope.launch {
            val isIbau = getIsIbauUseCase.invoke()
            _state.update { it.copy(isIbauUser = isIbau) }
        }
    }

    private fun getIsAutoClear() {
        viewModelScope.launch {
            val isAutoClear = getIsAutoClearUseCase()
            _state.update { it.copy(isAutoClear = isAutoClear) }
        }
    }

    private fun getBreakDuration() {
        viewModelScope.launch {
            val breakTime = getBreakDurationUseCase()
            _state.update { it.copy(breakDuration = breakTime) }
        }
    }

    private fun getVisaReminder() {
        viewModelScope.launch {
            val visaReminder = getVisaReminderUseCase()
            _state.update { it.copy(visaReminder = visaReminder.toString()) }
        }
    }

    private fun getFullDayAllowance() {
        viewModelScope.launch {
            val fullDayAllowance = getFullDayAllowanceUseCase()
            _state.update { it.copy(fullDayAllowance = fullDayAllowance.toString()) }
        }
    }

    private fun get8HDayAllowance() {
        viewModelScope.launch {
            val _8HDayAllowance = get8HDayAllowanceUseCase()
            _state.update { it.copy(_8HAllowance = _8HDayAllowance.toString()) }
        }
    }

    private fun getSavingDeductible() {
        viewModelScope.launch {
            val savingDeductible = getSavingDeductibleUseCase()
            _state.update { it.copy(savingDeductible = savingDeductible.toString()) }
        }
    }


    fun setUserName(userName: String) {
        _state.update { it.copy(userName = userName) }
        viewModelScope.launch {
            setUserNameUseCase.invoke(userName)
        }
    }
    fun setTheme(theme: Theme) {
        _state.update { it.copy(theme = theme) }
        viewModelScope.launch {
            setThemeUseCase.invoke(theme)
        }
    }
    fun setDarkTheme(theme: DarkTheme) {
        _state.update { it.copy(darkTheme = theme) }
        viewModelScope.launch {
            setDarkThemeUseCase.invoke(theme)
        }
    }

    fun setVisaReminder(reminder: String) {
        viewModelScope.launch {
            reminder.toIntWithString().let { resourceWithString ->
                when (resourceWithString) {
                    is ResourceWithString.Error -> {
                        _userMessage.emit(resourceWithString.message ?: "Error")

                    }
                    is ResourceWithString.Loading -> Unit
                    is ResourceWithString.Success -> {
                        setVisaReminderUseCase(resourceWithString.data!!)
                    }
                }
                _state.update { it.copy(visaReminder = resourceWithString.string ?: "") }

            }
        }

    }

    fun setIsIbau(isIbau: Boolean) {
        _state.update { it.copy(isIbauUser = isIbau) }
        viewModelScope.launch {
            setIsIbauUseCase.invoke(isIbau)
        }
    }

    fun setAutoClear(autoClear: Boolean) {
        _state.update { it.copy(isAutoClear = autoClear) }
        viewModelScope.launch {
            setIsAutoClearUseCase.invoke(autoClear)
        }
    }

    fun breakDurationChanged(breakDuration: String) {
            breakDuration.toFloatWithString().let { resourceString ->
                when (resourceString) {
                    is ResourceWithString.Error -> {
                        viewModelScope.launch {
                            _userMessage.emit(resourceString.message ?: "error")
                        }
                    }
                    is ResourceWithString.Loading -> Unit
                    is ResourceWithString.Success -> {
                        viewModelScope.launch {
                            setBreakDurationUseCase(resourceString.data!!).collect { resource ->
                                when (resource) {
                                    is Resource.Error -> _userMessage.emit(
                                        resource.message ?: "error"
                                    )

                                    else -> Unit
                                }
                            }
                        }
                    }
                }
                _state.update { it.copy(breakDuration = resourceString.string ?: "") }
            }
    }

    fun signatureBtnClicked() {
        _state.update { it.copy(showSignaturePad = true) }
        Log.d(TAG, "signatureBtnClicked:  show pad = ${_state.value.showSignaturePad}")
    }

    fun backupButtonClicked() {
        startBackup()
    }


    fun restoreFolderSelected(uri: Uri) {
        startRestore(uri)
    }


    private fun getSignature() {
        viewModelScope.launch {
            when (val signatureResource =
                loadBitmapUseCase.invoke("signatures", "user_signature")) {
                is Resource.Success -> _state.update { it.copy(signature = signatureResource.data?.asImageBitmap()) }
                is Resource.Error -> {
                    _userMessage.emit("${signatureResource.message}")
                    _state.update { it.copy(isLoading = false) }
                }
                is Resource.Loading -> {}
            }

        }

    }

    fun signatureScreenClosed() {
        _state.update { it.copy(showSignaturePad = false) }
        Log.d(TAG, "signatureScreenClosed: show pad = ${_state.value.showSignaturePad}")
    }

    fun updateSignature() {
        getSignature()
    }

    fun setFullDayAllowance(allowance: String) {
            allowance.toIntWithString().let { resourceWithString ->
                when (resourceWithString) {
                    is ResourceWithString.Error -> {
                        viewModelScope.launch {
                            _userMessage.emit(resourceWithString.message ?: "Error")
                        }
                    }
                    is ResourceWithString.Loading -> Unit
                    is ResourceWithString.Success -> {
                        viewModelScope.launch {
                            setFullDayAllowanceUseCase(resourceWithString.data!!)
                        }
                    }
                }
                _state.update { it.copy(fullDayAllowance = resourceWithString.string ?: "") }

            }
    }

    fun set8HAllowance(allowance: String) {
            allowance.toIntWithString().let { resourceWithString ->
                when (resourceWithString) {
                    is ResourceWithString.Error -> {
                        viewModelScope.launch {
                            _userMessage.emit(resourceWithString.message ?: "Error")
                        }
                    }
                    is ResourceWithString.Loading -> Unit
                    is ResourceWithString.Success -> {
                            viewModelScope.launch {
                                set8HAllowanceUseCase(resourceWithString.data!!)
                            }
                    }
                }
                _state.update { it.copy(_8HAllowance = resourceWithString.string ?: "") }
        }
    }

    fun setSavingDeductible(deductible: String) {
            deductible.toIntWithString().let { resourceWithString ->
                when (resourceWithString) {
                    is ResourceWithString.Error -> {
                        viewModelScope.launch {
                            _userMessage.emit(resourceWithString.message ?: "Error")
                        }
                    }
                    is ResourceWithString.Loading -> Unit
                    is ResourceWithString.Success -> {
                        viewModelScope.launch {
                            setSavingDeductibleUseCase(resourceWithString.data!!)
                        }
                    }
                }
                _state.update { it.copy(savingDeductible = resourceWithString.string ?: "") }

            }
        }
}