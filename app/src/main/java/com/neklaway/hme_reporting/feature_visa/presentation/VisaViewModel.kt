package com.neklaway.hme_reporting.feature_visa.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neklaway.hme_reporting.feature_settings.domain.use_cases.visa_reminder.GetVisaReminderUseCase
import com.neklaway.hme_reporting.feature_visa.domain.model.Visa
import com.neklaway.hme_reporting.feature_visa.domain.use_cases.*
import com.neklaway.hme_reporting.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

private const val TAG = "Visa ViewModel"

@HiltViewModel
class VisaViewModel @Inject constructor(
    val deleteVisaUseCase: DeleteVisaUseCase,
    val getAllVisasFlowUseCase: GetAllVisasFlowUseCase,
    val insertVisaUseCase: InsertVisaUseCase,
    val updateVisaUseCase: UpdateVisaUseCase,
    val getVisaReminderUseCase: GetVisaReminderUseCase,
    val visaReminderUseCase: VisaReminderWorkerUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(VisaState())
    val state = _state.asStateFlow()

    private val _userMessage = Channel<String>()
    val userMessage= _userMessage.receiveAsFlow()


    init {
        viewModelScope.launch {
            getVisas()
            getVisaReminder()
            visaReminderUseCase()
        }
    }

    private fun getVisas() {

        getAllVisasFlowUseCase().onEach { result ->
            when (result) {
                is Resource.Error -> {
                    _userMessage.send(result.message ?: "Can't get visas")
                    _state.update { it.copy(loading = false) }
                }
                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> {
                    _state.update { state ->
                        state.copy(
                            visas = result.data.orEmpty()
                                .sortedWith(compareBy({ it.date }, { it.country })), loading = false
                        )
                    }
                }
            }

        }.launchIn(viewModelScope)

    }

    private fun getVisaReminder() {
        viewModelScope.launch {
            _state.update { it.copy(warningDays = getVisaReminderUseCase()) }
        }
    }


    private fun saveVisa() {
        val country = state.value.country
        val date = state.value.date

        insertVisaUseCase(
            country, date
        ).onEach { result ->
            when (result) {
                is Resource.Error -> {
                    _userMessage.send(result.message ?: "Can't save Visa")
                    _state.update { it.copy(loading = false) }
                }
                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> clearState()

            }
        }.launchIn(viewModelScope)
    }

    private fun clearState() {
        _state.update {
            it.copy(
                loading = false,
                country = "",
                date = null,
            )
        }
    }


    private fun updateVisa() {
        val country = state.value.country
        val date = state.value.date

        val selectedVisa = state.value.selectedVisa

        if (selectedVisa != null) {
            updateVisaUseCase(
                country = country,
                date = date,
                checked = selectedVisa.selected,
                id = selectedVisa.id
            ).onEach { result ->
                when (result) {
                    is Resource.Error -> {
                        _userMessage.send(result.message ?: "Can't update Visa")
                        _state.update { it.copy(loading = false) }
                    }
                    is Resource.Loading -> _state.update { it.copy(loading = true) }
                    is Resource.Success -> clearState()
                }
            }.launchIn(viewModelScope)
        }
    }


    private fun deleteVisa(visa: Visa) {

        deleteVisaUseCase(visa).onEach { result ->
            when (result) {
                is Resource.Error -> {
                    _userMessage.send(result.message ?: "Can't delete Visa")
                    _state.update { it.copy(loading = false) }
                }
                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> clearState()
            }
        }.launchIn(viewModelScope)
    }


    private fun visaClicked(visa: Visa) {
        _state.update {
            it.copy(
                country = visa.country, date = visa.date
            )
        }
        _state.update { it.copy(selectedVisa = visa) }
    }

    private fun countryChanged(country: String) {
        _state.update {
            it.copy(country = country)
        }
    }


    private fun dateClicked() {
        _state.update { it.copy(showDatePicker = true) }
        Log.d(TAG, "dateClicked: ")
    }

    private fun datePicked(year: Int, month: Int, day: Int) {
        val date = Calendar.getInstance()
        date.timeZone = TimeZone.getTimeZone("Asia/Dubai")
        date.set(
            year, month, day, 0, 0, 0
        )
        date.set(Calendar.MILLISECOND, 0)
        _state.update { it.copy(date = date, showDatePicker = false) }
    }

    private fun datePickedCanceled() {
        _state.update { it.copy(showDatePicker = false) }
    }

    private fun visaSelected(visa: Visa, checked: Boolean) {
        updateVisaUseCase(
            country = visa.country, date = visa.date, checked = checked, id = visa.id
        ).onEach { result ->
            when (result) {
                is Resource.Error -> {
                    _userMessage.send(result.message ?: "Can't update Visa")
                    _state.update { it.copy(loading = false) }
                }
                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> {
                    _state.update {
                        it.copy(loading = false)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun userEvent(event: VisaUserEvents){
        when(event){
            is VisaUserEvents.CountryChanged -> countryChanged(event.country)
            VisaUserEvents.DateClicked -> dateClicked()
            is VisaUserEvents.DatePicked -> datePicked(event.year,event.month,event.day)
            VisaUserEvents.DatePickedCanceled -> datePickedCanceled()
            is VisaUserEvents.DeleteVisa -> deleteVisa(event.visa)
            VisaUserEvents.SaveVisa -> saveVisa()
            VisaUserEvents.UpdateVisa -> updateVisa()
            is VisaUserEvents.VisaClicked -> visaClicked(event.visa)
            is VisaUserEvents.VisaSelected -> visaSelected(event.visa,event.checked)
        }
    }
}


