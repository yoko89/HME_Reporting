package com.neklaway.hme_reporting.feature_car_mileage.domain.use_cases

import com.google.common.truth.Truth.assertThat
import com.neklaway.hme_reporting.common.data.entity.toCarMileage
import com.neklaway.hme_reporting.common.data.repo_impl.TestCarMileageRepository
import com.neklaway.hme_reporting.feature_car_mileage.domain.model.CarMileage
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class InsertCarMileageListUseCaseTest{
    private val repo = TestCarMileageRepository()
    private val insertCarMileageListUseCase = InsertCarMileageListUseCase(repo)

    @Test
    fun `insert Car Mileage list`(){
        val dateTime = Calendar.getInstance()
        dateTime.timeZone = TimeZone.getTimeZone("Asia/Dubai")

        val  carMileageList = mutableListOf(
            CarMileage(dateTime, dateTime, 100, dateTime, dateTime, 1000, 1),
            CarMileage(dateTime, dateTime, 100, dateTime, dateTime, 1000, 2),
            CarMileage(dateTime, dateTime, 100, dateTime, dateTime, 1000, 3)
        )

        runBlocking {
            insertCarMileageListUseCase.invoke(carMileageList).collect()
        }
        assertThat(repo.getAll().map { it.toCarMileage() }).isEqualTo(carMileageList)
    }


}