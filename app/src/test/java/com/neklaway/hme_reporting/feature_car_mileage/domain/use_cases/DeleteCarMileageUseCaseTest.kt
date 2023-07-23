package com.neklaway.hme_reporting.feature_car_mileage.domain.use_cases

import com.google.common.truth.Truth.assertThat
import com.neklaway.hme_reporting.common.data.repo_impl.TestCarMileageRepository
import com.neklaway.hme_reporting.feature_car_mileage.domain.model.CarMileage
import com.neklaway.hme_reporting.feature_car_mileage.domain.model.toCarMileageEntity
import com.neklaway.hme_reporting.utils.Resource
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.Calendar

class DeleteCarMileageUseCaseTest {
    private val repo = TestCarMileageRepository()
    val deleteCarMileageUseCase = DeleteCarMileageUseCase(repo)


    @Test
    fun `insure delete is deleting `() {
        val carMileage = CarMileage(
            Calendar.getInstance(),
            Calendar.getInstance(),
            100,
            Calendar.getInstance(),
            Calendar.getInstance(),
            100,
            1
        )

        runBlocking {
            repo.insert(carMileage.toCarMileageEntity())

            assertThat(repo.getAll()).contains(carMileage.toCarMileageEntity())

            deleteCarMileageUseCase(
                carMileage
            ).collect {
                assertThat(it).isInstanceOf(Resource.Success::class.java)
            }
            assertThat(repo.getAll()).doesNotContain(carMileage.toCarMileageEntity())
        }
    }
}