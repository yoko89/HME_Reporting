package com.neklaway.hme_reporting.feature_car_mileage.domain.use_cases

import com.google.common.truth.Truth.assertThat
import com.neklaway.hme_reporting.common.data.repo_impl.TestCarMileageRepository
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.Calendar

class InsertCarMileageUseCaseTest {
    private val repo = TestCarMileageRepository()
    val insertCarMileageUseCase = InsertCarMileageUseCase(repo)


    @Test
    fun `insert Empty StartDate should return Resource_error`() {
        val result = insertCarMileageUseCase(
            100,
            null,
            Calendar.getInstance(),
            Calendar.getInstance(),
            Calendar.getInstance(),
            100
        )
        runBlocking {
            assertThat(result.last()).isInstanceOf(Resource.Error::class.java)
        }
    }

@Test
    fun `insert Empty endDate should return Resource_error`() {
        val result = insertCarMileageUseCase(
            startMileage = 100,
            startDate = Calendar.getInstance(),
            startTime = Calendar.getInstance(),
            endDate = null,
            endTime = Calendar.getInstance(),
            endMileage = 100
        )
        runBlocking {
            assertThat(result.last()).isInstanceOf(Resource.Error::class.java)
        }
    }
    @Test
    fun `insert Empty startTime should return Resource_error`() {
        val result = insertCarMileageUseCase(
            startMileage = 100,
            startDate = Calendar.getInstance(),
            startTime = null,
            endDate = Calendar.getInstance(),
            endTime = Calendar.getInstance(),
            endMileage = 100
        )
        runBlocking {
            assertThat(result.last()).isInstanceOf(Resource.Error::class.java)
        }
    }
        @Test
    fun `insert Empty endTime should return Resource_error`() {
        val result = insertCarMileageUseCase(
            startMileage = 100,
            startDate = Calendar.getInstance(),
            startTime =  Calendar.getInstance(),
            endDate = Calendar.getInstance(),
            endTime = null,
            endMileage = 100
        )
        runBlocking {
            assertThat(result.last()).isInstanceOf(Resource.Error::class.java)
        }
    }
        @Test
    fun `insert negative startMileage should return Resource_error`() {
        val result = insertCarMileageUseCase(
            startMileage = -100,
            startDate = Calendar.getInstance(),
            startTime =  Calendar.getInstance(),
            endDate = Calendar.getInstance(),
            endTime = Calendar.getInstance(),
            endMileage = 100
        )
        runBlocking {
            assertThat(result.last()).isInstanceOf(Resource.Error::class.java)
        }
    }
        @Test
    fun `insert negative endMileage should return Resource_error`() {
        val result = insertCarMileageUseCase(
            startMileage = 100,
            startDate = Calendar.getInstance(),
            startTime =  Calendar.getInstance(),
            endDate = Calendar.getInstance(),
            endTime = Calendar.getInstance(),
            endMileage = -100
        )
        runBlocking {
            assertThat(result.last()).isInstanceOf(Resource.Error::class.java)
        }
    }
    @Test
    fun `insert endMileage less than startMileage should return Resource_error`() {
        val result = insertCarMileageUseCase(
            startMileage = 100,
            startDate = Calendar.getInstance(),
            startTime =  Calendar.getInstance(),
            endDate = Calendar.getInstance(),
            endTime = Calendar.getInstance(),
            endMileage = 50
        )
        runBlocking {
            assertThat(result.last()).isInstanceOf(Resource.Error::class.java)
        }
    }

    @Test
    fun `insert startDate after endDate should return Resource_error`() {
        val cal = Calendar.getInstance()
        val calMinus1Day = Calendar.getInstance()
        calMinus1Day.set(Calendar.DAY_OF_MONTH,cal.get(Calendar.DAY_OF_MONTH)-1)
        val result = insertCarMileageUseCase(
            startMileage = 50,
            startDate = cal,
            startTime =  cal,
            endDate = calMinus1Day,
            endTime = calMinus1Day,
            endMileage = 100
        )
        runBlocking {
            assertThat(result.last()).isInstanceOf(Resource.Error::class.java)
        }
    }
    @Test
    fun `insert startTime after endTime should return Resource_error`() {
        val cal = Calendar.getInstance()
        val calMinus1Day = Calendar.getInstance()
        calMinus1Day.set(Calendar.DAY_OF_MONTH,cal.get(Calendar.DAY_OF_MONTH)-1)
        val result = insertCarMileageUseCase(
            startMileage = 50,
            startDate = cal,
            startTime =  cal,
            endDate = cal,
            endTime = calMinus1Day,
            endMileage = 100
        )
        runBlocking {
            assertThat(result.last()).isInstanceOf(Resource.Error::class.java)
        }
    }

    @Test
    fun `insert carMileage return Resource_success`() {
        val cal = Calendar.getInstance()
        val calMinus1Day = Calendar.getInstance()
        calMinus1Day.set(Calendar.DAY_OF_MONTH,cal.get(Calendar.DAY_OF_MONTH)-1)
        val result = insertCarMileageUseCase(
            startMileage = 50,
            startDate = cal,
            startTime =  cal,
            endDate = cal,
            endTime = cal,
            endMileage = 100
        )
        runBlocking {
            assertThat(result.last()).isInstanceOf(Resource.Success::class.java)
        }
    }


}