package com.camackenzie.exvi.client.android

import com.camackenzie.exvi.client.view.App
import com.camackenzie.exvi.client.model.AndroidResourceDelegate
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.camackenzie.exvi.client.view.ExviMaterialTheme
import com.camackenzie.exvi.core.util.ExviLogger
import io.github.aakira.napier.DebugAntilog

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ExviLogger.base(DebugAntilog())

        super.onCreate(savedInstanceState)
        AndroidResourceDelegate.instance = this.application
        setContent {
            ExviMaterialTheme {
                App()
            }
        }
    }
}