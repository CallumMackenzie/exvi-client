package com.camackenzie.exvi.client.android

import com.camackenzie.exvi.client.view.App
import com.camackenzie.exvi.client.model.AndroidResourceDelegate
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidResourceDelegate.instance = this.application
        setContent {
            MaterialTheme {
                App()
            }
        }
    }
}