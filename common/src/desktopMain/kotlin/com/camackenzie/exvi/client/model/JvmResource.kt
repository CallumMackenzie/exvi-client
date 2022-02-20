package com.camackenzie.exvi.client.model

import androidx.compose.ui.res.useResource

actual fun readTextFile(file: String): String = useResource("raw/$file") {
    it.bufferedReader().readText()
}