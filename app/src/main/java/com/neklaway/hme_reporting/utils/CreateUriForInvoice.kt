package com.neklaway.hme_reporting.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.util.Calendar

fun createUriForInvoice(context: Context, hmeCode: String): Pair<Uri, Uri> {
    val directory =
        File(context.filesDir.path + "/" + hmeCode, Constants.EXPANSE_INVOICES_FOLDER)
    if (!directory.exists()) {
        directory.mkdirs()
    }
    var file = File(directory, hmeCode + Calendar.getInstance().timeInMillis + ".jpg")
    while (file.exists()) {
        file = File(directory, hmeCode + Calendar.getInstance().timeInMillis + ".jpg")
    }
    val providerUri =
        FileProvider.getUriForFile(context, "com.neklaway.hme_reporting.provider", file)
    val uri = Uri.fromFile(file)
    return Pair(providerUri, uri)
}
