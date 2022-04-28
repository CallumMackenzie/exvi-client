package com.camackenzie.exvi.client.components

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterialApi::class)
@Composable
actual fun AlertDialog(
    onDismissRequest: (() -> Unit),
    buttons: (@Composable () -> Unit),
    modifier: Modifier,
    title: (@Composable () -> Unit)?,
    text: (@Composable () -> Unit)?,
) = androidx.compose.material.AlertDialog(
    onDismissRequest = onDismissRequest,
    buttons = buttons,
    modifier = modifier,
    title = title,
    text = text
)