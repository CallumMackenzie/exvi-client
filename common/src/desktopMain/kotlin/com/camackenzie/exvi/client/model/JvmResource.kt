package com.camackenzie.exvi.client.model

actual fun readTextFile(file: String): String {
    return object {}.javaClass.getResource("/raw/$file")?.readText()!!
}
