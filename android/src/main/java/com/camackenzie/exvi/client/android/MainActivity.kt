package com.camackenzie.exvi.client.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.camackenzie.exvi.client.model.AndroidResourceDelegate
import com.camackenzie.exvi.client.view.App
import com.camackenzie.exvi.client.view.ExviMaterialTheme
import com.camackenzie.exvi.core.util.setDefaultLogger
import io.github.aakira.napier.LogLevel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setDefaultLogger({ logLevel, output ->
            when (logLevel) {
                LogLevel.ERROR,
                LogLevel.WARNING -> System.err.println(output)
                else -> println(output)
            }
        })

        super.onCreate(savedInstanceState)
        AndroidResourceDelegate.instance = this.application
        setContent {
            ExviMaterialTheme {
                App()
            }
        }
    }
}