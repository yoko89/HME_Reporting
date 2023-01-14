package com.neklaway.hme_reporting.feature_settings.domain.repository

import android.net.Uri

interface RestoreRepository {

    fun startRestore(uri: Uri)
}