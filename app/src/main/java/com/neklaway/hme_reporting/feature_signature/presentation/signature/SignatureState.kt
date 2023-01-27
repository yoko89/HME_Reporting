package com.neklaway.hme_reporting.feature_signature.presentation.signature

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path

data class SignatureState(
    val path: Path = Path(),
    val signature: ImageBitmap? = null,
    val signatureUpdated: Boolean = false,
    val exit: Boolean = false,
    val errorSignerName: Boolean = false,
    val requireSignerName: Boolean = false,
    val signerName: String = ""
)
