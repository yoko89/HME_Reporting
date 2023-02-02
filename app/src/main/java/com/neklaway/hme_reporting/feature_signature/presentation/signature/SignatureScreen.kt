@file:OptIn(ExperimentalMaterial3Api::class)

package com.neklaway.hme_reporting.feature_signature.presentation.signature

import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.neklaway.hme_reporting.common.ui.theme.HMEReportingTheme


@Composable
fun SignatureScreen(
    signatureFileName: String,
    modifier: Modifier = Modifier,
    signatureUpdatedAtExit: (signed: Boolean, signerName: String?) -> Unit,
    requireSignerName: Boolean = false,
) {

    val viewModel = hiltViewModel<SignatureViewModel>()

    val state by remember {
        viewModel.state
    }.collectAsState()

    var canvasSize: Size? = null

    LaunchedEffect(Unit) {
        viewModel.signatureName(signatureFileName)
        viewModel.requireSignerName(requireSignerName)
    }

    if (state.signatureUpdated) {
        signatureUpdatedAtExit(true, state.signerName)
        viewModel.exitDone()
    }

    if (state.exit) {
        signatureUpdatedAtExit(false, null)
        viewModel.exitDone()
    }

    HMEReportingTheme {
        Dialog(onDismissRequest = { }
        ) {
            Box(
                modifier = modifier
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                        2.dp, MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(10.dp)
                    )
                    .background(MaterialTheme.colorScheme.background)
            ) {

                Column(
                    modifier = Modifier
                        .padding(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Close, modifier = Modifier
                            .align(Alignment.End)
                            .clickable { viewModel.exit() }, contentDescription = "Close"
                    )
                    Spacer(modifier = Modifier.height(5.dp))

                    Signature(viewModel = viewModel, path = state.path,
                        canvasSize = {
                            canvasSize = it
                        })

                    AnimatedVisibility(visible = requireSignerName) {

                        OutlinedTextField(value = state.signerName,
                            label = { Text(text = "Signer Name") },
                            modifier = Modifier.padding(5.dp),
                            isError = state.errorSignerName,
                            onValueChange = {
                                viewModel.signerNameChanged(it)
                            })
                    }

                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        Button(onClick = viewModel::clearSignature) {
                            Text(text = "Clear")
                        }

                        Button(onClick = { viewModel.saveSignature(canvasSize) }) {
                            Text(text = "Save")
                        }
                    }


                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Signature(
    viewModel: SignatureViewModel,
    path: Path,
    modifier: Modifier = Modifier,
    canvasSize: (Size) -> Unit,
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .height(200.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.LightGray)
            .border(
                width = 2.dp,
                color = Color.Black,
                shape = RoundedCornerShape(10.dp)
            )
            .pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        viewModel.pathMoveTo(it.x, it.y)
                    }
                    MotionEvent.ACTION_MOVE -> {
                        viewModel.pathLineTo(it.x, it.y)
                    }
                    else -> Unit
                }
                true
            }) {
        canvasSize(this.size)
        clipRect {
            drawPath(path, color = Color.Blue, style = Stroke(2f))
        }
    }
}

