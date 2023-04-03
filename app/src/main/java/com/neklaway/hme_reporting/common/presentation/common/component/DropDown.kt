package com.neklaway.hme_reporting.common.presentation.common.component

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropDown(
    dropDownList: List<T>,
    selectedValue: String,
    label: String,
    dropDownContentDescription: String,
    warning: Boolean = false,
    onSelect: ((T) -> Unit),
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val dropDown = remember {
            mutableStateOf(false)
        }
        val infiniteTransition = rememberInfiniteTransition()

        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            label = { Text(text = label) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(false) {}
                .background(
                    if (warning) {
                        infiniteTransition.animateColor(
                            initialValue = MaterialTheme.colorScheme.background,
                            targetValue = Color.Yellow,
                            animationSpec = infiniteRepeatable(
                                animation = tween(
                                    durationMillis = 500,
                                    delayMillis = 1000,
                                    easing = LinearEasing
                                ), repeatMode = RepeatMode.Reverse
                            )
                        ).value
                    } else MaterialTheme.colorScheme.background
                ),
            readOnly = true,
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = dropDownContentDescription,
                    modifier = Modifier.clickable {
                        dropDown.value = true
                    }
                )
            },
        )

        if (dropDown.value) {
            Spacer(modifier = Modifier.height(5.dp))
        }

        DropdownMenu(
            expanded = dropDown.value,
            onDismissRequest = { dropDown.value = false },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 0.dp, max = 400.dp)
                .clip(RoundedCornerShape(10.dp))
        ) {
            dropDownList.onEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item.toString(),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(2.dp)
                                .fillMaxWidth()
                        )
                    },
                    onClick = {
                        onSelect(item)
                        dropDown.value = false
                    },
                    modifier = Modifier
                        .padding(5.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            shape = RoundedCornerShape(3.dp)
                        )
                )
            }
        }
    }
}