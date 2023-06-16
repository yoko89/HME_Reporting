package com.neklaway.hme_reporting.feature_car_mileage.domain.use_cases

import com.google.common.truth.Truth
import com.neklaway.hme_reporting.common.data.repo_impl.TestCarMileageRepository
import com.neklaway.hme_reporting.feature_car_mileage.domain.model.CarMileage
import com.neklaway.hme_reporting.feature_car_mileage.domain.model.toCarMileageEntity
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.Calendar

class UpdateCarMileageUseCaseTest {

    private val repo = TestCarMileageRepository()
    val updateCarMileageUseCase = UpdateCarMileageUseCase(repo)


    @Test
    fun `insert Empty StartDate should return Resource_error`() {
        val result = updateCarMileageUseCase(
            100,
            null,
            Calendar.getInstance(),
            Calendar.getInstance(),
            Calendar.getInstance(),
            100,
            1
        )
        runBlocking {
            Truth.assertThat(result.last()).isInstanceOf(Resource.Error::class.java)
        }
    }

    @Test
    fun `insert Empty endDate should return Resource_error`() {
        val result = updateCarMileageUseCase(
            startMileage = 100,
            startDate = Calendar.getInstance(),
            startTime = Calendar.getInstance(),
            endDate = null,
            endTime = Calendar.getInstance(),
            endMileage = 100,
            1
        )
        runBlocking {
            Truth.assertThat(result.last()).isInstanceOf(Resource.Error::class.java)
        }
    }

    @Test
    fun `insert Empty startTime should return Resource_error`() {
        val result = updateCarMileageUseCase(
            startMileage = 100,
            startDate = Calendar.getInstance(),
            startTime = null,
            endDate = Calendar.getInstance(),
            endTime = Calendar.getInstance(),
            endMileage = 100,
            1
        )
        runBlocking {
            Truth.assertThat(result.last()).isInstanceOf(Resource.Error::class.java)
        }
    }

    @Test
    fun `insert Empty endTime should return Resource_error`() {
        val result = updateCarMileageUseCase(
            startMileage = 100,
            startDate = Calendar.getInstance(),
            startTime = Calendar.getInstance(),
            endDate = Calendar.getInstance(),
            endTime = null,
            endMileage = 100,
            1
        )
        runBlocking {
            Truth.assertThat(result.last()).isInstanceOf(Resource.Error::class.java)
        }
    }

    @Test
    fun `insert negative startMileage should return Resource_error`() {
        val result = updateCarMileageUseCase(
            startMileage = -100,
            startDate = Calendar.getInstance(),
            startTime = Calendar.getInstance(),
            endDate = Calendar.getInstance(),
            endTime = Calendar.getInstance(),
            endMileage = 100,
            1
        )
        runBlocking {
            Truth.assertThat(result.last()).isInstanceOf(Resource.Error::class.java)
        }
    }

    @Test
    fun `insert negative endMileage should return Resource_error`() {
        val result = updateCarMileageUseCase(
            startMileage = 100,
            startDate = Calendar.getInstance(),
            startTime = Calendar.getInstance(),
            endDate = Calendar.getInstance(),
            endTime = Calendar.getInstance(),
            endMileage = -100,
            1
        )
        runBlocking {
            Truth.assertThat(result.last()).isInstanceOf(Resource.Error::class.java)
        }
    }

    @Test
    fun `insert endMileage less than startMileage should return Resource_error`() {
        val result = updateCarMileageUseCase(
            startMileage = 100,
            startDate = Calendar.getInstance(),
            startTime = Calendar.getInstance(),
            endDate = Calendar.getInstance(),
            endTime = Calendar.getInstance(),
            endMileage = 50,
            1
        )
        runBlocking {
            Truth.assertThat(result.last()).isInstanceOf(Resource.Error::class.java)
        }
    }

    @Test
    fun `insert startDate after endDate should return Resource_error`() {
        val cal = Calendar.getInstance()
        val calMinus1Day = Calendar.getInstance()
        calMinus1Day.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1)
        val result = updateCarMileageUseCase(
            startMileage = 50,
            startDate = cal,
            startTime = cal,
            endDate = calMinus1Day,
            endTime = calMinus1Day,
            endMileage = 100,
            1
        )
        runBlocking {
            Truth.assertThat(result.last()).isInstanceOf(Resource.Error::class.java)
        }
    }

    @Test
    fun `insert startTime after endTime should return Resource_error`() {
        val cal = Calendar.getInstance()
        val calMinus1Day = Calendar.getInstance()
        calMinus1Day.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1)
        val result = updateCarMileageUseCase(
            startMileage = 50,
            startDate = cal,
            startTime = cal,
            endDate = cal,
            endTime = calMinus1Day,
            endMileage = 100,
            1
        )
        runBlocking {
            Truth.assertThat(result.last()).isInstanceOf(Resource.Error::class.java)
        }
    }

    @Test
    fun `insert carMileage return Resource_success`() {
        val cal = Calendar.getInstance()
        val calPlus1Day = Calendar.getInstance()
        calPlus1Day.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1)

        runBlocking {
            repo.insert(
                CarMileage
                    (
                    Calendar.getInstance(),
                    Calendar.getInstance(),
                    100,
                    Calendar.getInstance(),
                    Calendar.getInstance(),
                    endMileage = 50,
                    id = 1
                ).toCarMileageEntity()
            )
        }

        val result = updateCarMileageUseCase(
            startMileage = 50,
            startDate = cal,
            startTime = cal,
            endDate = calPlus1Day,
            endTime = calPlus1Day,
            endMileage = 100,
            1
        )
        runBlocking {
            Truth.assertThat(result.last()).isInstanceOf(Resource.Success::class.java)
        }
    }


}