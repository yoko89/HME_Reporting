package com.neklaway.hme_reporting.feature_settings.presentation.settings

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.*
import com.neklaway.hme_reporting.common.presentation.common.component.Selector
import com.neklaway.hme_reporting.common.ui.theme.HMEReportingTheme
import com.neklaway.hme_reporting.feature_settings.presentation.SettingsViewModel
import com.neklaway.hme_reporting.feature_signature.presentation.signature.SignatureScreen
import com.neklaway.hme_reporting.utils.Constants
import com.neklaway.hme_reporting.utils.NotificationPermissionRequest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val userMessage = viewModel.userMessage

    var requestPermission by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    LaunchedEffect(key1 = userMessage) {
        userMessage.collect {
            snackbarHostState.showSnackbar(it)
        }
    }

    val activityResult =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult(),
            onResult = { activityResult ->
                if (activityResult.resultCode == RESULT_OK) {
                    activityResult.data?.data?.also { uri ->
                        viewModel.restoreFolderSelected(uri)
                    }
                }
            })

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
                    onValueChange = viewModel::breakDurationChanged,
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
                    Spacer(modifier = Modifier.height(5.dp))
                }

                Button(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    onClick = { viewModel.signatureBtnClicked() }) {
                    Text(text = "Signature")
                }

                Button(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    onClick = {
                        requestPermission = true
                        viewModel.backupButtonClicked()
                    }) {
                    Text(text = "Backup")
                }

                Button(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    onClick = {
                        requestPermission = true
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                            putExtra(
                                DocumentsContract.EXTRA_INITIAL_URI,
                                Uri.decode("/storage/emulated/0/Documents")
                            )
                        }

                        activityResult.launch(intent)

                    }
                ) {
                    Text(text = "Restore")
                }
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



        if (requestPermission) {
            NotificationPermissionRequest(context = context)
            requestPermission = false
        }

    }
}
