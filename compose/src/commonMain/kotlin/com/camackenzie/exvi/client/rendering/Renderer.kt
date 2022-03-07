package com.camackenzie.exvi.client.rendering

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
import com.camackenzie.exvi.client.view.Muscle3DView
import com.soywiz.korma.geom.*
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock

class ComposableMesh3D(
    points: Array<Vector3D> = emptyArray(),
    transform: Matrix3D = Matrix3D()
) : Mesh3D {
    override val points by mutableStateOf(points)
    override var transform by mutableStateOf(transform)
}

class ComposableCamera3D(
    position: Position3D = Vector3D(),
    rotation: Quaternion = Quaternion(),
    aspectRatio: Float = 1f,
    fov: Angle = 90.degrees,
    near: Float = 0.1f,
    far: Float = 100f
) : ActualCamera3D(position, rotation, aspectRatio, fov, near, far) {
    override var position by mutableStateOf(position)
    override var rotation by mutableStateOf(rotation)
    override var aspectRatio by mutableStateOf(aspectRatio)
    override var fov by mutableStateOf(fov)
    override var near by mutableStateOf(near)
    override var far by mutableStateOf(far)
}

class RenderData(
    meshes: Array<ComposableMesh3D>,
    camera: ComposableCamera3D
) {
    var meshes by mutableStateOf(meshes)
    var camera by mutableStateOf(camera)
}

@Composable
fun Renderer3D(
    renderData: RenderData,
    modifier: Modifier = Modifier
) {
    fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()

    Canvas(modifier) {

        val newAspect = size.height / size.width
        if (newAspect != renderData.camera.aspectRatio) {
            renderData.camera.aspectRatio = newAspect
        }

        val renderer = Renderer3D(renderData.camera, postVertexShader = {
            it.points.forEach { pt ->
                pt.x *= size.width
                pt.y *= size.height
            }
        })

        val tris = renderer.renderToTriangles(*renderData.meshes)

        tris.forEach { (one, two, three) ->
            drawCircle(color = Color.Cyan, radius = 3f, center = Offset(one.x, one.y))
            drawCircle(color = Color.Cyan, radius = 3f, center = Offset(two.x, two.y))
            drawCircle(color = Color.Cyan, radius = 3f, center = Offset(three.x, three.y))
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