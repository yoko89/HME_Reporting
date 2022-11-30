package com.neklaway.hme_reporting.feature_time_sheet.presentation.customer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neklaway.hme_reporting.feature_time_sheet.domain.model.Customer
import com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.customer_use_cases.DeleteCustomerUseCase
import com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.customer_use_cases.GetAllCustomersFlowUseCase
import com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.customer_use_cases.InsertCustomerUseCase
import com.neklaway.hme_reporting.feature_time_sheet.domain.use_cases.customer_use_cases.UpdateCustomerUseCase
import com.neklaway.hme_reporting.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

private const val TAG = "Customer ViewModel"

@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val getAllCustomersFlowUseCase: GetAllCustomersFlowUseCase,
    private val insertCustomerUseCase: InsertCustomerUseCase,
    private val updateCustomerUseCase: UpdateCustomerUseCase,
    private val deleteCustomerUseCase: DeleteCustomerUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CustomerState())
    val state: StateFlow<CustomerState> = _state.asStateFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage


    init {
        getCustomers()
    }

    private fun getCustomers() {

        getAllCustomersFlowUseCase().onEach { result ->
            Log.d(TAG, "getCustomers: Fetching Customers $result, Data = ${result.message}")
            when (result) {
                is Resource.Error -> {
                    _userMessage.emit(result.message ?: "Can't get customers")
                    _state.update { it.copy(loading = false) }
                }
                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> _state.update {
                    it.copy(
                        customers = result.data.orEmpty(),
                        loading = false
                    )
                }
            }
        }.launchIn(viewModelScope)

    }

    fun saveCustomer() {
        val name: String = state.value.customerName
        val city: String = state.value.customerCity
        val country: String = state.value.customerCountry

        insertCustomerUseCase(name, city, country).onEach { result ->
            when (result) {
                is Resource.Error -> {
                    _userMessage.emit(result.message ?: "Can't save customers")
                    _state.update { it.copy(loading = false) }
                }
                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> _state.update {
                    it.copy(
                        loading = false,
                        customerName = "",
                        customerCity = "",
                        customerCountry = ""
                    )
                }
            }
        }.launchIn(viewModelScope)
    }


    fun updateCustomer() {
        val name: String = state.value.customerName
        val city: String = state.value.customerCity
        val country: String = state.value.customerCountry

        val selectedCustomer = state.value.selectedCustomer

        if (selectedCustomer?.id != null) {
            updateCustomerUseCase(name, city, country, selectedCustomer.id).onEach { result ->
                when (result) {
                    is Resource.Error -> {
                        _userMessage.emit(result.message ?: "Can't save customers")
                        _state.update { it.copy(loading = false) }
                    }
                    is Resource.Loading -> _state.update { it.copy(loading = true) }
                    is Resource.Success -> _state.update { it.copy(loading = false) }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun customerSelected(customer: Customer) {
        _state.update {
            it.copy(
                selectedCustomer = customer,
                customerName = customer.name,
                customerCity = customer.city,
                customerCountry = customer.country
            )
        }
    }

    fun customerNameChange(name: String) {
        _state.update {
            it.copy(customerName = name)
        }
    }

    fun customerCityChange(city: String) {
        _state.update { it.copy(customerCity = city) }
    }

    fun customerCountryChange(country: String) {
        _state.update { it.copy(customerCountry = country) }
    }

    fun deleteCustomer(customer: Customer) {
        deleteCustomerUseCase(customer).onEach { result ->
            when (result) {
                is Resource.Error -> {
                    _userMessage.emit(result.message ?: "Can't delete customer")
                    _state.update { it.copy(loading = false) }
                }
                is Resource.Loading -> _state.update { it.copy(loading = true) }
                is Resource.Success -> _state.update { it.copy(loading = false) }
            }
        }.launchIn(viewModelScope)
    }

}