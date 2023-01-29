package com.neklaway.hme_reporting.feature_settings.data.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class RestartReceiver @Inject constructor() : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val packageManager = context?.packageManager
        val launchIntent = packageManager?.getLaunchIntentForPackage(context.packageName)
        val mainIntent = Intent.makeRestartActivityTask(launchIntent?.component)
        context?.startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }
}