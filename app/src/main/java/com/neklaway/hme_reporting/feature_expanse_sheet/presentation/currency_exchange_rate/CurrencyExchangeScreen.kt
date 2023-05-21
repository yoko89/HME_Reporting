package com.neklaway.hme_reporting.feature_expanse_sheet.presentation.currency_exchange_rate

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow


@Composable
fun CurrencyExchangeScreen(
    state:CurrencyExchangeState,
    userMessage: Flow<String>,
    userEvent:(CurrencyExchangeUserEvent) ->Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = userMessage) {
        userMessage.collect {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        floatingActionButton = {
            Row {
                AnimatedVisibility(
                    state.selectedCurrency != null,
                    enter = slideInVertically(initialOffsetY = { it }).plus(fadeIn()),
                    exit = slideOutVertically(targetOffsetY = { it }).plus(fadeOut())
                ) {
                    Row {
                        FloatingActionButton(onClick = {
                            userEvent(CurrencyExchangeUserEvent.UpdateCurrency)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Currency"
                            )
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                    }
                }

                FloatingActionButton(onClick = {
                    userEvent(CurrencyExchangeUserEvent.SaveCurrency)
                }) {

                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Currency")
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(5.dp)
        ) {

            OutlinedTextField(
                value = state.currencyName,
                onValueChange = { name ->
                    userEvent(CurrencyExchangeUserEvent.CurrencyNameChange(name))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Currency Name") },
                singleLine = true,
                maxLines = 1
            )



            OutlinedTextField(
                value = state.exchangeRate, onValueChange = { rate ->
                    userEvent(CurrencyExchangeUserEvent.CurrencyRateChanged(rate))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Exchange Rate") },
                singleLine = true,
                maxLines = 1
            )


            LazyColumn(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                item {
                    AnimatedVisibility(
                        visible = state.loading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        CircularProgressIndicator()
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Currency",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Exchange Rate",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.weight(0.5f))
                    }
                }

                items(items = state.currencyExchangeList) { currencyExchange ->
                    val visibility = remember {
                        mutableStateOf(false)
                    }

                    SideEffect {
                        visibility.value = true
                    }

                    AnimatedVisibility(
                        visible = visibility.value,
                        enter = slideInHorizontally(),
                        exit = slideOutHorizontally()
                    ) {

                        Card(
                            modifier = Modifier
                                .padding(vertical = 2.dp)
                                .clickable {
                                    userEvent(
                                        CurrencyExchangeUserEvent.CurrencySelected(
                                            currencyExchange
                                        )
                                    )
                                }
                        ) {

                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = currencyExchange.currencyName,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = currencyExchange.rate.toString(),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f)
                                )


                                OutlinedIconButton(
                                    onClick = {
                                        userEvent(
                                            CurrencyExchangeUserEvent.DeleteRate(
                                                currencyExchange
                                            )
                                        )
                                    },
                                    modifier = Modifier.weight(0.5f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Currency",
                                        tint = Color.Red,
                                        modifier = Modifier.alpha(.6f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



