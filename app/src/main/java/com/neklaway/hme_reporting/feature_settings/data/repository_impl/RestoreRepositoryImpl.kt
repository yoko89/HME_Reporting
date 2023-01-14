package com.neklaway.hme_reporting.feature_settings.data.repository_impl

import android.content.Context
import android.net.Uri
import androidx.work.*
import com.neklaway.hme_reporting.feature_settings.data.worker.BackupWorker
import com.neklaway.hme_reporting.feature_settings.data.worker.RestoreWorker
import com.neklaway.hme_reporting.feature_settings.domain.repository.RestoreRepository
import javax.inject.Inject

class RestoreRepositoryImpl @Inject constructor(
    private val context: Context
) : RestoreRepository {

    override fun startRestore(uri:Uri) {
        val workManager = WorkManager.getInstance(context)

        val restoreWorkRequest = OneTimeWorkRequestBuilder<RestoreWorker>()
            .setInputData(workDataOf("folder_uri" to uri.toString()))
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        workManager.enqueue(
            restoreWorkRequest
        )
    }
}