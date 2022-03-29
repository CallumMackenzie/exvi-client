package com.camackenzie.exvi.client.model

actual fun readTextFile(file: String): String = object {}.javaClass.getResource("/raw/$file")?.readText()!!
