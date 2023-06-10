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
}