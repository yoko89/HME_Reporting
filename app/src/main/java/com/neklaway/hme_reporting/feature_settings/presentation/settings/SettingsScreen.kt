package com.neklaway.hme_reporting.feature_settings.presentation.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.*
import com.neklaway.hme_reporting.common.presentation.common.component.Selector
import com.neklaway.hme_reporting.common.ui.theme.HMEReportingTheme
import com.neklaway.hme_reporting.feature_signature.presentation.signature.SignatureScreen
import com.neklaway.hme_reporting.utils.Constants


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val userMessage = viewModel.userMessage

    LaunchedEffect(key1 = userMessage) {
        userMessage.collect {
            snackbarHostState.showSnackbar(it)
        }
    }


    HMEReportingTheme {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(5.dp)
            ) {

                OutlinedTextField(
                    value = state.userName,
                    onValueChange = { viewModel.setUserName(it) },
                    label = { Text(text = "User Name") },
                    modifier = Modifier
                        .fillMaxWidth(),
                )

                Selector(
                    text = "IBAU User",
                    checked = state.isIbauUser,
                    onCheckedChange = { viewModel.setIsIbau(it) })

                Selector(
                    text = "Auto Clear",
                    checked = state.isAutoClear,
                    onCheckedChange = { viewModel.setAutoClear(it) })

                OutlinedTextField(
                    value = state.breakDuration,
                    onValueChange = {
                        viewModel.breakDurationChanged(it)
                    },
                    label = { Text(text = "Break Duration") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                )

                OutlinedTextField(
                    value = state.visaReminder,
                    onValueChange = {
                        viewModel.setVisaReminder(it)
                    },
                    label = { Text(text = "Visa Reminder Days") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )

                state.signature?.let {
                    Spacer(modifier = Modifier.height(5.dp))
                    Image(
                        bitmap = it, contentDescription = "Signature", modifier = Modifier
                            .border(
                                width = 2.dp, color = Color.Black, shape = RoundedCornerShape(10.dp)
                            )
                            .align(Alignment.CenterHorizontally)
                            .clip(RoundedCornerShape(10.dp))
                    )
                }

                Button(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(5.dp),
                    onClick = { viewModel.signatureBtnClicked() }) {
                    Text(text = "Signature")
                }

                Button(modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(5.dp),
                    onClick = viewModel::backupButtonClicked) {
                    Text(text = "backup")
                }
            }


            AnimatedVisibility(visible = state.showSignaturePad) {
                SignatureScreen(
                    signatureFileName = Constants.USER_SIGNATURE,
                    signatureUpdatedAtExit = { signedSuccessfully, _ ->
                        viewModel.signatureScreenClosed()
                        if (signedSuccessfully)
                            viewModel.updateSignature()
                    })
            }
        }
    }
}