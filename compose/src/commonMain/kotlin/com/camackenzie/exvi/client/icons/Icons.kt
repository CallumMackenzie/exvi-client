package com.camackenzie.exvi.client.icons

import androidx.compose.ui.graphics.vector.ImageVector

object ExviIcons {

    val Add: ImageVector
        get() = addIcon()

    val Stop: ImageVector
        get() = stopIcon()

    val Visibility: ImageVector
        get() = visibilityIcon()

    val VisibilityOff: ImageVector
        get() = visibilityOffIcon()

    val ArrowDown: ImageVector
        get() = arrowDownIcon()

    val ArrowUp: ImageVector
        get() = arrowUpIcon()

    val Target: ImageVector
        get() = targetIcon()
}

internal expect fun addIcon(): ImageVector
internal expect fun stopIcon(): ImageVector
internal expect fun visibilityIcon(): ImageVector
internal expect fun visibilityOffIcon(): ImageVector
internal expect fun arrowDownIcon(): ImageVector
internal expect fun arrowUpIcon(): ImageVector
internal expect fun targetIcon(): ImageVector