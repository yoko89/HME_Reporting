package com.neklaway.hme_reporting.feature_signature.presentation.signature

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.neklaway.hme_reporting.feature_signature.domain.use_cases.bitmap_use_case.CreateBitmapFromPathUseCase
import com.neklaway.hme_reporting.feature_signature.domain.use_cases.bitmap_use_case.SaveBitmapUseCase
import com.neklaway.hme_reporting.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SignatureViewModel @Inject constructor(
    private val createBitmapFromPathUseCase: CreateBitmapFromPathUseCase,
    private val saveBitmapUseCase: SaveBitmapUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        const val SIGNATURE_NAME = "signature_name"
    }

    private val _state = MutableStateFlow(SignatureState())
    val state = _state.asStateFlow()

    lateinit var signatureName: String


    fun saveSignature(canvasSize: Size?) {

        if (state.value.requireSignerName && state.value.signerName.isBlank()){
            _state.update { it.copy(errorSignerName = true) }
            return
        }

        canvasSize?.let { size ->
            val signatureBitmap = createBitmapFromPathUseCase.invoke(state.value.path, size)

            val result = saveBitmapUseCase.invoke(signatureBitmap, Constants.SIGNATURES_FOLDER, signatureName)
            if (result){
                _state.update { it.copy(signatureUpdated = true) }
                clearSignature()
            }
        }
    }

    fun pathMoveTo(x: Float, y: Float) {
        val path = state.value.path
        path.moveTo(x, y)
        _state.update { it.copy(path = path) }
    }

    fun pathLineTo(x: Float, y: Float) {
        val path = state.value.path
        path.lineTo(x, y)
        _state.update { it.copy(path = path, drawLine = Offset(x, y)
        ) }
    }


    fun signatureName(signatureName :String){
        this.signatureName = signatureName
    }

    fun exitDone() {
        _state.update { it.copy(signatureUpdated = false,exit = false) }
    }

    fun clearSignature() {
        _state.value.path.reset()
        _state.update {
            it.copy(path = _state.value.path, drawLine = Offset(0f, 0f))
        }
    }

    fun exit() {
        clearSignature()
        _state.update { it.copy(exit = true) }
    }

    fun requireSignerName(requireSignerName: Boolean) {
        _state.update { it.copy(requireSignerName = requireSignerName) }
    }

    fun signerNameChanged(signerName: String) {
        _state.update { it.copy(signerName = signerName, errorSignerName = false) }

    }
}