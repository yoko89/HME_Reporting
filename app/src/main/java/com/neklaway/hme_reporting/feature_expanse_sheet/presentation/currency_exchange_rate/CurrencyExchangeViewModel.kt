package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.currency_exchange_rate

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.model.CurrencyExchange
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.currency_exchange_use_cases.DeleteCurrencyExchangeUseCase
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.currency_exchange_use_cases.GetAllCurrencyExchangeFlowUseCase
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.currency_exchange_use_cases.InsertCurrencyExchangeUseCase
import com.neklaway.hme_reporting.feature_expanse_sheet.domain.use_cases.currency_exchange_use_cases.UpdateCurrencyExchangeUseCase
import com.neklaway.hme_reporting.utils.Resource
import com.neklaway.hme_reporting.utils.ResourceWithString
import com.neklaway.hme_reporting.utils.toFloatWithString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CurrencyExchangeViewModel"

@HiltViewModel
class CurrencyExchangeViewModel @Inject constructor(
    private val insertCurrencyExchangeUseCase: InsertCurrencyExchangeUseCase,
    private val updateCurrencyExchangeUseCase: UpdateCurrencyExchangeUseCase,
    private val deleteCurrencyExchangeUseCase: DeleteCurrencyExchangeUseCase,
    private val getAllCurrencyExchangeFlowUseCase: GetAllCurrencyExchangeFlowUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CurrencyExchangeState())
    val state: StateFlow<CurrencyExchangeState> = _state.asStateFlow()

    private val _userMessage = Channel<String>()
    val userMessage = _userMessage.receiveAsFlow()


    init {
        getCurrencyExchangeList()
    }

    private fun getCurrencyExchangeList() {

        getAllCurrencyExchangeFlowUseCase().onEach { result ->
            Log.d(TAG, "getCurrency: Fetching currencyList $result, Data = ${result.data}")
            when (result) {
                is Resource.Error -> {
                    _userMessage.send(result.message ?: "Can't get customers")
                    _state.update { it.copy(loading = false) }
                }

                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> _state.update {
                    it.copy(
                        currencyExchangeList = result.data.orEmpty(),
                        loading = false
                    )
                }
            }
        }.launchIn(viewModelScope)

    }

    private fun saveCurrency() {
        val currencyName: String = state.value.currencyName
        val rate = state.value.exchangeRate.toFloatWithString().let { resource ->
            when (resource) {
                is ResourceWithString.Error -> {
                    sendEvent(resource.message ?: "Error")
                    0f
                }

                is ResourceWithString.Loading -> {
                    0f
                }

                is ResourceWithString.Success -> {
                    resource.data ?: 0f
                }
            }
        }

        insertCurrencyExchangeUseCase(currencyName, rate).onEach { result ->
            when (result) {
                is Resource.Error -> {
                    _userMessage.send(result.message ?: "Can't save Currency")
                    _state.update { it.copy(loading = false) }
                }

                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> clearState()
            }
        }.launchIn(viewModelScope)
    }

    private fun sendEvent(massage: String) {
        viewModelScope.launch {
            _userMessage.send(massage)
        }
    }


    private fun updateCurrency() {
        val currencyName: String = state.value.currencyName
        val rate = state.value.exchangeRate.toFloatWithString().let { resource ->
            when (resource) {
                is ResourceWithString.Error -> {
                    sendEvent(resource.message ?: "Error")
                    0f
                }

                is ResourceWithString.Loading -> {
                    0f
                }

                is ResourceWithString.Success -> {
                    resource.data ?: 0f
                }
            }
        }

        val selectedCurrency = state.value.selectedCurrency

        if (selectedCurrency?.id == null) return

        updateCurrencyExchangeUseCase(currencyName, rate, selectedCurrency.id).onEach { result ->
            when (result) {
                is Resource.Error -> {
                    _userMessage.send(result.message ?: "Can't update currency")
                    _state.update { it.copy(loading = false) }
                }

                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> clearState()
            }
        }.launchIn(viewModelScope)

    }

    private fun currencySelected(currencyExchange: CurrencyExchange) {
        _state.update {
            it.copy(
                selectedCurrency = currencyExchange,
                currencyName = currencyExchange.currencyName,
                exchangeRate = currencyExchange.rate.toString()
            )
        }
    }

    private fun currencyNameChange(name: String) {
        _state.update {
            it.copy(currencyName = name)
        }
    }

    private fun currencyRateChanged(rate: String) {
        rate.toFloatWithString().let { resource ->
            when (resource) {
                is ResourceWithString.Error -> {
                    sendEvent(resource.message ?: "Error")
                    _state.update { it.copy(exchangeRate = "") }
                }

                is ResourceWithString.Loading -> Unit
                is ResourceWithString.Success -> {
                    _state.update { it.copy(exchangeRate = rate) }
                }
            }
        }
    }


    private fun deleteRate(currencyExchange: CurrencyExchange) {
        deleteCurrencyExchangeUseCase(currencyExchange).onEach { result ->
            when (result) {
                is Resource.Error -> {
                    _userMessage.send(result.message ?: "Can't delete Currency")
                    _state.update { it.copy(loading = false) }
                }

                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> _state.update { it.copy(loading = false) }
            }
        }.launchIn(viewModelScope)
    }

    private fun clearState() {
        _state.update {
            it.copy(
                loading = false,
                exchangeRate = "",
                currencyName = "",
                selectedCurrency = null
            )
        }
    }

    fun userEvent(event: CurrencyExchangeUserEvent) {
        when (event) {
            is CurrencyExchangeUserEvent.CurrencyNameChange -> currencyNameChange(event.name)
            is CurrencyExchangeUserEvent.CurrencyRateChanged -> currencyRateChanged(event.rate)
            is CurrencyExchangeUserEvent.CurrencySelected -> currencySelected(event.currencyExchange)
            is CurrencyExchangeUserEvent.DeleteRate -> deleteRate(event.currencyExchange)
            CurrencyExchangeUserEvent.SaveCurrency -> saveCurrency()
            CurrencyExchangeUserEvent.UpdateCurrency -> updateCurrency()
        }
    }

}