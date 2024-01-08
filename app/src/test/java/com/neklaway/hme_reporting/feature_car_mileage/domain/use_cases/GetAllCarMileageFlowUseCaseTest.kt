package com.neklaway.hme_reporting.feature_car_mileage.domain.use_cases

import com.google.common.truth.Truth.assertThat
import com.neklaway.hme_reporting.common.data.repo_impl.TestCarMileageRepository
import com.neklaway.hme_reporting.feature_car_mileage.domain.model.CarMileage
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

import org.junit.Before
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class GetAllCarMileageFlowUseCaseTest {
    private val repo = TestCarMileageRepository()
    private val getAllCarMileageFlowUseCase = GetAllCarMileageFlowUseCase(repo)
    private lateinit var carMileageList: MutableList<CarMileage>

    @Before
    fun setUp() {
        val dateTime = Calendar.getInstance()
        dateTime.timeZone = TimeZone.getTimeZone("Asia/Dubai")
        carMileageList = mutableListOf(
            CarMileage(dateTime, dateTime, 100, dateTime, dateTime, 1000, 1),
            CarMileage(dateTime, dateTime, 100, dateTime, dateTime, 1000, 2),
            CarMileage(dateTime, dateTime, 100, dateTime, dateTime, 1000, 3)
        )
        runBlocking {
            InsertCarMileageListUseCase(repo).invoke(carMileageList).collect()
        }
    }

    @Test
    fun `get all items in the repo`() = runBlocking {
        getAllCarMileageFlowUseCase.invoke().collect { result ->
            when (result) {
                is Resource.Error -> assertThat(true).isFalse()
                is Resource.Loading -> Unit
                is Resource.Success -> {
                    assertThat(result.data).isEqualTo(carMileageList)
                }
            }

        }
    }


}