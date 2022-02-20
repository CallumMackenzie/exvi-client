package com.camackenzie.exvi.client.model

import android.app.Application

object AndroidResourceDelegate {
    var instance: Application? = null
}

actual fun readTextFile(file: String): String = with(AndroidResourceDelegate.instance!!) {
    val resourceId = resources.getIdentifier(file.substringBefore("."), "raw", packageName)
    resources.openRawResource(resourceId).bufferedReader().readText()
}