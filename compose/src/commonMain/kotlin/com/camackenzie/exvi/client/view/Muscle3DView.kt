package com.camackenzie.exvi.client.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PointMode
import com.camackenzie.exvi.client.model.readTextFile
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import com.camackenzie.exvi.client.rendering.*
import com.soywiz.korma.geom.*
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock


object Muscle3DView {

    private fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()

    @Composable
    fun View(appState: AppState) {
        val coroutineScope = rememberCoroutineScope()
        val mesh by remember {
            mutableStateOf(
                ComposableMesh3D(
                    Mesh3D.fromObj(readTextFile("cube.obj")).toVectorArray(),
                    Matrix3D().setToScale(0.1, 0.1, 0.1)
                )
            )
        }
        val camera by remember {
            mutableStateOf(
                ComposableCamera3D(
                    position = Vector3D(0.0, 0.0, 10.0)
                )
            )
        }
        val rd = remember { RenderData(arrayOf(mesh), camera) }

        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row {
                Button(onClick = {
                    mesh.transform = mesh.transform.scale(1.1, 1.1, 1.1)
                }) {
                    Text("Scale Up")
                }
                Button(onClick = {
                    mesh.transform = mesh.transform.scale(0.9, 0.9, 0.9)
                }) {
                    Text("Scale Down")
                }
                Button(onClick = {
                    mesh.transform = mesh.transform.rotate(10.degrees, Vector3D(0.0, 1.0, 0.0))
                }) {
                    Text("Rotate Y")
                }
                Button(onClick = {
                    mesh.transform = mesh.transform.rotate(10.degrees, Vector3D(1.0, 0.0, 0.0))
                }) {
                    Text("Rotate X")
                }
                Button(onClick = {
                    mesh.transform = mesh.transform.rotate(10.degrees, Vector3D(0.0, 0.0, 1.0))
                }) {
                    Text("Rotate Z")
                }
            }
            Renderer3D(rd, Modifier.fillMaxSize())
        }
    }

}