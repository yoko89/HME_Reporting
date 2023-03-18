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
import kotlinx.coroutines.flow.*
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

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage


    init {
        getCurrencyExchangeList()
    }

    private fun getCurrencyExchangeList() {

        getAllCurrencyExchangeFlowUseCase().onEach { result ->
            Log.d(TAG, "getCurrency: Fetching currencyList $result, Data = ${result.data}")
            when (result) {
                is Resource.Error -> {
                    _userMessage.emit(result.message ?: "Can't get customers")
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

    fun saveCurrency() {
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
                    _userMessage.emit(result.message ?: "Can't save Currency")
                    _state.update { it.copy(loading = false) }
                }
                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> clearState()
            }
        }.launchIn(viewModelScope)
    }

    private fun sendEvent(massage: String) {
        viewModelScope.launch {
            _userMessage.emit(massage)
        }
    }


    fun updateCurrency() {
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
                    _userMessage.emit(result.message ?: "Can't update currency")
                    _state.update { it.copy(loading = false) }
                }
                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> clearState()
            }
        }.launchIn(viewModelScope)

    }

    fun currencySelected(currencyExchange: CurrencyExchange) {
        _state.update {
            it.copy(
                selectedCurrency = currencyExchange,
                currencyName = currencyExchange.currencyName,
                exchangeRate = currencyExchange.rate.toString()
            )
        }
    }

    fun currencyNameChange(name: String) {
        _state.update {
            it.copy(currencyName = name)
        }
    }

    fun currencyRateChanged(rate: String) {
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


    fun deleteRate(currencyExchange: CurrencyExchange) {
        deleteCurrencyExchangeUseCase(currencyExchange).onEach { result ->
            when (result) {
                is Resource.Error -> {
                    _userMessage.emit(result.message ?: "Can't delete Currency")
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

}