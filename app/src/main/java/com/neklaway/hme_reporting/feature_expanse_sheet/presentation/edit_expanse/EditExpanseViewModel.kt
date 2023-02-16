package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.edit_expanse

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.CurrencyExchange
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.Expanse
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.currency_exchange_use_cases.GetAllCurrencyExchangeFlowUseCase
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.currency_exchange_use_cases.GetCurrencyExchangeByIdUseCase
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.expanse_use_cases.DeleteExpanseUseCase
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.expanse_use_cases.GetExpanseByIdUseCase
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.expanse_use_cases.UpdateExpanseUseCase
import com.neklaway.hme_reporting.utils.Resource
import com.neklaway.hme_reporting.utils.ResourceWithString
import com.neklaway.hme_reporting.utils.toFloatWithString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

private const val TAG = "EditExpanseViewModel"

@HiltViewModel
class EditExpanseViewModel @Inject constructor(
    private val updateExpanseUseCase: UpdateExpanseUseCase,
    private val getExpanseByIdUseCase: GetExpanseByIdUseCase,
    private val deleteExpanseUseCase: DeleteExpanseUseCase,
    private val getCurrencyExchangeByIdUseCase: GetCurrencyExchangeByIdUseCase,
    private val getAllCurrencyExchangeFlowUseCase: GetAllCurrencyExchangeFlowUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        const val EXPANSE_ID = "expanse_id"
    }

    private val _state = MutableStateFlow(EditExpanseState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<EditExpanseEvents>()
    val event: SharedFlow<EditExpanseEvents> = _event

    private val expanseId: Long
    private var expanseDeferred: Deferred<Expanse?>
    private lateinit var returnedExpanse: Expanse

    init {
        getCurrencyList()

        expanseId = savedStateHandle[EXPANSE_ID] ?: -1
        _state.update { it.copy(expanseId = expanseId) }
        Log.d(TAG, ": ${state.value.expanseId}")

        expanseDeferred = viewModelScope.async(Dispatchers.IO) {
            getExpanseByIdUseCase(expanseId).onEach { result ->
                Log.d(TAG, "Expanse: $result")
                when (result) {
                    is Resource.Error -> {
                        _event.emit(
                            EditExpanseEvents.UserMessage(
                                result.message ?: "Error can't retrieve Data "
                            )
                        )
                        _state.update { it.copy(loading = false) }
                    }
                    is Resource.Loading -> _state.update { it.copy(loading = true) }
                    is Resource.Success -> {
                        val expanse = result.data ?: return@onEach
                        val currency =
                            getCurrencyExchangeByIdUseCase(expanse.currencyID).last().data
                        _state.update {
                            it.copy(
                                date = expanse.date,
                                invoiceNumber = expanse.invoiceNumber,
                                description = expanse.description,
                                personallyPaid = expanse.personallyPaid,
                                amount = expanse.amount.toString(),
                                amountAED = expanse.amountAED.toString(),
                                invoicesUri = expanse.invoicesUri,
                                selectedCurrency = currency,
                                expanseId = expanseId,
                                loading = false
                            )
                        }
                        returnedExpanse = result.data
                    }
                }
            }.launchIn(viewModelScope)
            return@async returnedExpanse
        }
    }


    fun updateExpanse() {
        viewModelScope.launch {
            if (!::returnedExpanse.isInitialized) {
                return@launch
            }

            updateExpanseUseCase.invoke(
                HMEId = returnedExpanse.HMEId,
                date = state.value.date,
                invoiceNumber = state.value.invoiceNumber,
                description = state.value.description,
                personallyPaid = state.value.personallyPaid,
                amount = state.value.amount.toFloat(),
                currencyID = state.value.selectedCurrency?.id,
                amountAED = state.value.amountAED.toFloat(),
                invoiceUris = state.value.invoicesUri,
                id = state.value.expanseId,
            ).collect { result ->
                when (result) {
                    is Resource.Error -> {
                        _event.emit(
                            EditExpanseEvents.UserMessage(
                                result.message ?: "Error can't update"
                            )
                        )
                        _state.update { it.copy(loading = false) }
                    }
                    is Resource.Loading -> _state.update { it.copy(loading = true) }
                    is Resource.Success -> {
                        _state.update { it.copy(loading = false) }
                        _event.emit(EditExpanseEvents.PopBackStack)
                    }
                }

            }
        }
    }

    fun dateClicked() {
        _state.update { it.copy(showDatePicker = true) }
        Log.d(TAG, "dateClicked: ")
    }

    fun datePicked(year: Int, month: Int, day: Int) {
        val date = Calendar.getInstance()
        date.set(
            year,
            month,
            day,
            0,
            0,
            0
        )
        date.set(Calendar.MILLISECOND, 0)
        _state.update { it.copy(date = date, showDatePicker = false) }
    }

    fun dateShown() {
        _state.update { it.copy(showDatePicker = false) }
    }

    fun deleteExpanse() {
        viewModelScope.launch {
            val expanse = expanseDeferred.await()

            if (expanse == null) {
                _event.emit(EditExpanseEvents.UserMessage("Can't retrieve Expanse"))
                return@launch
            }

            deleteExpanseUseCase(expanse).collect { result ->
                when (result) {
                    is Resource.Error -> {
                        _event.emit(
                            EditExpanseEvents.UserMessage(
                                result.message ?: "Error can't delete Expanse"
                            )
                        )
                        _state.update { it.copy(loading = false) }
                    }
                    is Resource.Loading -> _state.update { it.copy(loading = true) }
                    is Resource.Success -> {
                        _event.emit(EditExpanseEvents.UserMessage("Expanse Deleted"))
                        _event.emit(EditExpanseEvents.PopBackStack)
                        _state.update { it.copy(loading = false) }

                    }
                }
            }
        }
    }


    fun currencySelected(currencyExchange: CurrencyExchange) {
        viewModelScope.launch {
            _state.update { it.copy(selectedCurrency = currencyExchange) }
            calculateAmountInAED()
        }

    }

    private suspend fun calculateAmountInAED() {
        val amountInAED = state.value.amount.toFloatWithString().let { resource ->
            when (resource) {
                is ResourceWithString.Error -> {
                    _event.emit(EditExpanseEvents.UserMessage(resource.message ?: "Error"))
                    ""
                }
                is ResourceWithString.Loading -> ""
                is ResourceWithString.Success -> {
                    resource.data?.let { amount ->
                        (amount.times(state.value.selectedCurrency?.rate ?: 0f).toString())
                    } ?: ""
                }
            }
        }
        _state.update { it.copy(amountAED = amountInAED) }
    }

    fun invoiceNumberChanged(invoiceNumber: String) {
        _state.update { it.copy(invoiceNumber = invoiceNumber) }
    }

    fun descriptionChanged(description: String) {
        _state.update { it.copy(description = description) }
    }

    fun amountChanged(amount: String) {
        viewModelScope.launch {
            amount.toFloatWithString().let { resourceWithString ->
                when (resourceWithString) {
                    is ResourceWithString.Error -> {
                        _event.emit(
                            EditExpanseEvents.UserMessage(
                                resourceWithString.message ?: "Error in Amount"
                            )
                        )
                        _state.update { it.copy(amount = resourceWithString.string ?: "") }
                    }
                    is ResourceWithString.Loading -> Unit
                    is ResourceWithString.Success -> {
                        calculateAmountInAED()
                        _state.update { it.copy(amount = resourceWithString.string ?: "") }
                    }
                }
            }
        }
    }

    fun amountAEDChanged(amount: String) {
        viewModelScope.launch {
            amount.toFloatWithString().let { resourceWithString ->
                when (resourceWithString) {
                    is ResourceWithString.Error -> {
                        _event.emit(
                            EditExpanseEvents.UserMessage(
                                resourceWithString.message ?: "Error in Amount"
                            )
                        )
                        _state.update { it.copy(amountAED = resourceWithString.string ?: "") }
                    }
                    is ResourceWithString.Loading -> Unit
                    is ResourceWithString.Success -> {
                        _state.update { it.copy(amountAED = resourceWithString.string ?: "") }
                    }
                }
            }
        }
    }

    private fun getCurrencyList() {
        viewModelScope.launch {
            getAllCurrencyExchangeFlowUseCase().collect { resource ->
                when (resource) {
                    is Resource.Error -> {
                        _event.emit(
                            EditExpanseEvents.UserMessage(
                                resource.message ?: "Error can't get Currency List"
                            )
                        )
                        _state.update { it.copy(loading = false) }
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(loading = true) }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                currencyList = resource.data ?: emptyList(),
                                loading = false
                            )
                        }
                    }
                }
            }
        }
    }
}