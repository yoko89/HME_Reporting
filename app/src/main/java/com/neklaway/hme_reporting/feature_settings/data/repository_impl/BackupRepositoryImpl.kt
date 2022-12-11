package com.neklaway.hme_reporting.feature_settings.data.repository_impl

import android.app.Application
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.neklaway.hme_reporting.feature_settings.data.worker.BackupWorker
import com.neklaway.hme_reporting.feature_settings.domain.repository.BackupRepository
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class BackupRepositoryImpl @Inject constructor(
    @ActivityContext private val context: Context
):BackupRepository {

    override fun startBackup() {
        val workManager = WorkManager.getInstance(context)

        val backupWorkRequest = OneTimeWorkRequestBuilder<BackupWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        workManager.enqueue(
            backupWorkRequest
        )
    }
}