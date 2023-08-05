package com.neklaway.hme_reporting.feature_settings.presentation

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.neklaway.hme_reporting.common.presentation.common.component.DropDown
import com.neklaway.hme_reporting.common.presentation.common.component.Selector
import com.neklaway.hme_reporting.feature_signature.presentation.signature.SignatureScreen
import com.neklaway.hme_reporting.utils.Constants
import com.neklaway.hme_reporting.utils.DarkTheme
import com.neklaway.hme_reporting.utils.NotificationPermissionRequest
import com.neklaway.hme_reporting.utils.ResourceWithString
import com.neklaway.hme_reporting.utils.Theme
import kotlinx.coroutines.flow.Flow


@Composable
fun SettingsScreen(
    state: SettingsState,
    userMessage: Flow<String>,
    userEvent: (SettingsUserEvents) -> Unit,
    showNavigationMenu: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()


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
                        userEvent(SettingsUserEvents.RestoreFolderSelected(uri))
                    }
                }
            })

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(title = { Text(text = "Settings") },
                navigationIcon = {
                    IconButton(onClick = showNavigationMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                scrollBehavior = scrollBehavior)
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(5.dp)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                OutlinedTextField(
                    value = state.userName,
                    onValueChange = { userEvent(SettingsUserEvents.SetUserName(it)) },
                    label = { Text(text = "User Name") },
                    modifier = Modifier
                        .fillMaxWidth(),
                )
            }

            item {
                Selector(
                    text = "IBAU User",
                    checked = state.isIbauUser,
                    onCheckedChange = { userEvent(SettingsUserEvents.SetIsIbau(it)) })
            }

            item {
                Selector(
                    text = "Auto Clear",
                    checked = state.isAutoClear,
                    onCheckedChange = { userEvent(SettingsUserEvents.SetAutoClear(it)) })
            }

            item {
                OutlinedTextField(
                    value = state.breakDuration.string?:"",
                    onValueChange = { userEvent(SettingsUserEvents.BreakDurationChanged(it)) },
                    label = { Text(text = "Break Duration") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    supportingText = { Text(text = state.breakDuration.message?:"")},
                    isError = state.breakDuration is ResourceWithString.Error
                )
            }

            item {
                Divider(modifier = Modifier.padding(vertical = 5.dp))

                OutlinedTextField(
                    value = state.visaReminder.string?:"",
                    onValueChange = {
                        userEvent(SettingsUserEvents.SetVisaReminder(it))
                    },
                    label = { Text(text = "Visa Reminder Days") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    supportingText = { Text(text = state.visaReminder.message?:"")},
                    isError = state.visaReminder is ResourceWithString.Error
                )
            }



            item {
                Divider(modifier = Modifier.padding(vertical = 5.dp))

                OutlinedTextField(
                    value = state.fullDayAllowance.string?:"",
                    onValueChange = {
                        userEvent(SettingsUserEvents.SetFullDayAllowance(it))
                    },
                    label = { Text(text = "Full Day Allowance") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    supportingText = { Text(text = state.fullDayAllowance.message?:"")},
                    isError = state.fullDayAllowance is ResourceWithString.Error
                )
            }

            item {
                OutlinedTextField(
                    value = state._8HAllowance.string?:"",
                    onValueChange = {
                        userEvent(SettingsUserEvents.Set8HAllowance(it))
                    },
                    label = { Text(text = "8H Allowance") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    supportingText = { Text(text = state._8HAllowance.message?:"")},
                    isError = state._8HAllowance is ResourceWithString.Error
                )
            }

            item {
                OutlinedTextField(
                    value = state.savingDeductible.string?:"",
                    onValueChange = {
                        userEvent(SettingsUserEvents.SetSavingDeductible(it))
                    },
                    label = { Text(text = "Deductible to Saving") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    supportingText = { Text(text = state.savingDeductible.message?:"")},
                    isError = state.savingDeductible is ResourceWithString.Error
                )
            }
            item {
                Divider(modifier = Modifier.padding(vertical = 5.dp))

                DropDown(
                    dropDownList = Theme.values().toList(),
                    selectedValue = state.theme.name,
                    label = "Theme Color",
                    dropDownContentDescription = "theme color selection",
                    onSelect = {
                        userEvent(SettingsUserEvents.SetTheme(it))
                    }
                )
            }
            item {
                DropDown(
                    dropDownList = DarkTheme.values().toList(),
                    selectedValue = state.darkTheme.name,
                    label = "Dark Theme",
                    dropDownContentDescription = "dark theme color selection",
                    onSelect = {
                        userEvent(SettingsUserEvents.SetDarkTheme(it))
                    }
                )
            }

            item {
                Divider(modifier = Modifier.padding(vertical = 5.dp))

                state.signature?.let {
                    Spacer(modifier = Modifier.height(5.dp))
                    Image(
                        bitmap = it, contentDescription = "Signature", modifier = Modifier
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }

            item {
                Button(
                    onClick = { userEvent(SettingsUserEvents.SignatureBtnClicked) }) {
                    Text(text = "Signature")
                }
            }

            item {

                Divider(modifier = Modifier.padding(vertical = 5.dp))

                OutlinedButton(
                    onClick = {
                        requestPermission = true
                        userEvent(SettingsUserEvents.BackupButtonClicked)
                    }) {
                    Text(text = "Backup")
                }
            }

            item {
                OutlinedButton(
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
    }


    AnimatedVisibility(visible = state.showSignaturePad) {
        SignatureScreen(
            signatureFileName = Constants.USER_SIGNATURE,
            signatureUpdatedAtExit = { signedSuccessfully, _ ->
                userEvent(SettingsUserEvents.SignatureScreenClosed)
                if (signedSuccessfully)
                    userEvent(SettingsUserEvents.UpdateSignature)
            })
    }


    if (requestPermission) {
        NotificationPermissionRequest(context = context)
        requestPermission = false
    }

}