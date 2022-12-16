package com.neklaway.hme_reporting.feature_settings.domain.use_cases.backup

import com.neklaway.hme_reporting.feature_settings.domain.repository.BackupRepository
import javax.inject.Inject

class StartBackup @Inject constructor(
    val repository: BackupRepository
) {

    operator fun invoke(){
        repository.startBackup()
    }
}