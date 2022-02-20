package com.camackenzie.exvi.client.model

const val EXERCISES_FILE = "exercises.json"

expect fun readTextFile(file: String) : String
