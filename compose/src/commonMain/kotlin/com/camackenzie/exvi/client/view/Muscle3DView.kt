package com.camackenzie.exvi.client.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PointMode
import com.camackenzie.exvi.client.model.readTextFile
import com.camackenzie.exvi.client.rendering.Camera3D
import com.camackenzie.exvi.client.rendering.Mesh3D
import com.camackenzie.exvi.client.rendering.Renderer3D
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import com.camackenzie.exvi.client.rendering.toVectorArray
import com.soywiz.korma.geom.*
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock


object Muscle3DView {

    private fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()

    @Composable
    fun View(appState: AppState) {
        var delta by remember { mutableStateOf(0.0) }
        var lastFrame by remember { mutableStateOf(currentTimeMillis()) }
        val targetDelta = 16L
        val minDelta = 1L

        val scope = rememberCoroutineScope()
        remember {
            scope.launch {
                while (true) {
                    delay(minDelta)
                    if (currentTimeMillis() - lastFrame >= targetDelta) {
                        delta = (currentTimeMillis() - lastFrame) / 1000.0
                        lastFrame = currentTimeMillis()
                    }
                }
            }
        }

        var offset by remember { mutableStateOf(0.0) }

        val camera = remember {
            Camera3D(Vector3D(0.0, 0.0, -30.0))
        }

        val verts = remember { Mesh3D.fromObj(readTextFile("cube.obj")).toVectorArray() }

        offset += 3.0 * delta

        Text(delta.toString())
        Canvas(Modifier.fillMaxSize()) {
            camera.aspectRatio = size.height / size.width
            val renderer = Renderer3D(camera, postVertexShader = {
                it.points.forEach { pt ->
                    pt.x *= size.width
                    pt.y *= size.height
                }
            })

            val tris = renderer.renderToTriangles(
                Mesh3D(
                    verts,
                    Matrix3D().setToRotation(EulerRotation(offset.degrees, offset.degrees, offset.degrees))
                )
            )

            tris.forEach { (one, two, three) ->
                drawCircle(color = Color.Cyan, radius = 5f, center = Offset(one.x, one.y))
                drawCircle(color = Color.Cyan, radius = 5f, center = Offset(two.x, two.y))
                drawCircle(color = Color.Cyan, radius = 5f, center = Offset(three.x, three.y))
                drawLine(
                    color = Color.Red,
                    start = Offset(one.x, one.y),
                    end = Offset(two.x, two.y)
                )
                drawLine(
                    color = Color.Red,
                    start = Offset(two.x, two.y),
                    end = Offset(three.x, three.y)
                )
                drawLine(
                    color = Color.Red,
                    start = Offset(three.x, three.y),
                    end = Offset(one.x, one.y)
                )
            }
        }
    }

}