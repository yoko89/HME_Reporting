package com.neklaway.hme_reporting.common.presentation.common.component

import android.util.Log
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private const val TAG = "DropDown"

@Composable
fun <T> DropDown(
    dropDownList: List<T>,
    selectedValue: String,
    label: String,
    dropDownContentDescription: String,
    modifier: Modifier = Modifier,
    warning: Boolean = false,
    onSelect: (T) -> Unit,
) {
    val dropDown = remember {
        mutableStateOf(false)
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }
    LaunchedEffect(key1 = interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> dropDown.value = true
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val infiniteTransition = rememberInfiniteTransition()

        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            interactionSource = interactionSource,
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
                        Log.d(TAG, "DropDown: selected ${item.toString()}")
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