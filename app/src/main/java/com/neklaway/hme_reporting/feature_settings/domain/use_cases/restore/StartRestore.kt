package com.neklaway.hme_reporting.feature_settings.domain.use_cases.backup

import android.net.Uri
import com.neklaway.hme_reporting.feature_settings.domain.repository.RestoreRepository
import javax.inject.Inject

class StartRestore @Inject constructor(
    val repository: RestoreRepository
) {

    operator fun invoke(uri: Uri){
        repository.startRestore(uri)
    }
}