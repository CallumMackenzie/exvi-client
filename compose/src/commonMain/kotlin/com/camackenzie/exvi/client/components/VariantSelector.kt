package com.camackenzie.exvi.client.components

import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.material.*
import androidx.compose.ui.Modifier

@Composable
fun <T> VariantSelector(
    variants: Array<T>,
    value: T?,
    onValueChanged: (T?) -> Unit,
    content: @Composable (T?) -> Unit,
    modifier: Modifier = Modifier,
    dropdownContent: @Composable (T?) -> Unit = content,
    dropdownExpanded: Boolean,
    onDropdownExpandedChanged: (Boolean) -> Unit,
) {
    Button(modifier = modifier, onClick = { onDropdownExpandedChanged(true) }) { content(value) }
    com.camackenzie.exvi.client.components.DropdownMenu(
        expanded = dropdownExpanded,
        onDismissRequest = { onDropdownExpandedChanged(false) },
    ) {
        DropdownMenuItem(onClick = {
            onValueChanged(null)
            onDropdownExpandedChanged(false)
        }) { dropdownContent(null) }
        for (item in variants) {
            DropdownMenuItem(onClick = {
                onValueChanged(item)
                onDropdownExpandedChanged(false)
            }) { dropdownContent(item) }
        }
    }
}