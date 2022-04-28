package com.camackenzie.exvi.client.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun AlertDialog(
    onDismissRequest: () -> Unit,
    buttons: @Composable (() -> Unit),
    modifier: Modifier,
    title: @Composable (() -> Unit)?,
    text: @Composable (() -> Unit)?
) = androidx.compose.material.AlertDialog(
    onDismissRequest = onDismissRequest,
    buttons = buttons,
    modifier = modifier,
    title = title,
    text = text,
)