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
import com.soywiz.korma.geom.Vector3D
import com.soywiz.korma.geom.radians
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color


class Muscle3DView {

    @Composable
    fun View(appState: AppState) {
        val camera = remember {
            Camera3D(
                position = Vector3D().setTo(0.0, 0.0, 0.0)
            )
        }
        var ctr by remember { mutableStateOf(0) }
        var mesh = remember { Mesh3D.fromObj(readTextFile("cube.obj")) }
        val renderer = Renderer3D(camera = camera)

        val scope = rememberCoroutineScope()
        val job = remember {
            scope.launch {
                while (true) {
                    kotlinx.coroutines.delay(10)
                    mesh.transform.setToRotation(ctr.radians, Vector3D(1.0, 0.0, 0.0))
                        .setToTranslation(
                            0.0, 0.0, 40.0
                        )
                    ++ctr
                }
            }
        }

        Text(text = ctr.toString())
        Canvas(Modifier.fillMaxSize()) {
            camera.aspectRatio = size.height / size.width

            val pts = renderer.render(mesh)
            drawPoints(
                points = pts.map { pt ->
                    Offset(pt.x.toFloat() * size.width, pt.y.toFloat() * size.height)
                },
                PointMode.Points,
                Color.Black,
                strokeWidth = 10f
            )

//            for (pt in renderer.render(mesh)) {
//                val color = Color.Black

//                if (tri.p0.x.isFinite() && tri.p0.y.isFinite()
//                    && tri.p1.x.isFinite() && tri.p1.y.isFinite()
//                )
//                    drawLine(
//                        color = color,
//                        start = Offset(
//                            tri.p0.x.toFloat() * size.width, tri.p0.y.toFloat() * size.height
//                        ),
//                        end = Offset(
//                            tri.p1.x.toFloat() * size.width, tri.p1.y.toFloat() * size.height
//                        )
//                    )
//
//                if (tri.p2.x.isFinite() && tri.p2.y.isFinite()
//                    && tri.p1.x.isFinite() && tri.p1.y.isFinite()
//                )
//                    drawLine(
//                        color = color,
//                        start = Offset(
//                            tri.p1.x.toFloat() * size.width, tri.p1.y.toFloat() * size.height
//                        ),
//                        end = Offset(
//                            tri.p2.x.toFloat() * size.width, tri.p2.y.toFloat() * size.height
//                        )
//                    )
//
//                if (tri.p0.x.isFinite() && tri.p0.y.isFinite()
//                    && tri.p2.x.isFinite() && tri.p2.y.isFinite()
//                )
//                    drawLine(
//                        color = color,
//                        start = Offset(
//                            tri.p2.x.toFloat() * size.width, tri.p2.y.toFloat() * size.height
//                        ),
//                        end = Offset(
//                            tri.p0.x.toFloat() * size.width, tri.p0.y.toFloat() * size.height
//                        )
//                    )
//            }
        }
    }

}