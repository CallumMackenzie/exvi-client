package com.camackenzie.exvi.client.rendering

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.soywiz.korma.geom.*
import kotlinx.coroutines.*
import androidx.compose.ui.unit.dp

typealias RenderFun = DrawScope.(Triangle3D) -> Unit

private val defaultRenderer: RenderFun = {
    val (one, two, three) = it
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

class ComposableMesh3D(
    points: Array<Vector3D> = emptyArray(),
    transform: Matrix3D = Matrix3D()
) : Mesh3D {

    constructor(points: Array<Triangle3D>, transform: Matrix3D = Matrix3D())
            : this(points = points.toVectorArray(), transform = transform)

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
fun RenderedSpinner(
    modifier: Modifier = Modifier.size(30.dp, 30.dp),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    renderer: RenderFun = defaultRenderer,
    meshData: Array<Triangle3D> = Mesh3D.fromObj(IcoSphereObjData)
) {
    val renderData = remember {
        RenderData(
            meshes = arrayOf(
                ComposableMesh3D(meshData)
            ),
            camera = ComposableCamera3D(position = Vector3D(0.0, 0.0, -20.0))
        )
    }
    remember {
        coroutineScope.launch(dispatcher) {
            while (true) {
                delay(20)
                renderData.meshes.forEach {
                    it.transform = Matrix3D()
                        .copyFrom(it.transform)
                        .rotate(EulerRotation(4.degrees, 4.degrees, 4.degrees))
                }
            }
        }
    }

    Renderer3D(
        renderData = renderData,
        modifier = modifier,
        triRenderer = renderer
    )
}

@Composable
fun Renderer3D(
    renderData: RenderData,
    modifier: Modifier = Modifier,
    triRenderer: RenderFun = defaultRenderer
) {
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

        renderer.renderToTriangles(*renderData.meshes).forEach {
            triRenderer(it)
        }
    }
}

private const val IcoSphereObjData = "v 0.000000 -1.000000 0.000000\n" +
        "v 0.723600 -0.447215 0.525720\n" +
        "v -0.276385 -0.447215 0.850640\n" +
        "v -0.894425 -0.447215 0.000000\n" +
        "v -0.276385 -0.447215 -0.850640\n" +
        "v 0.723600 -0.447215 -0.525720\n" +
        "v 0.276385 0.447215 0.850640\n" +
        "v -0.723600 0.447215 0.525720\n" +
        "v -0.723600 0.447215 -0.525720\n" +
        "v 0.276385 0.447215 -0.850640\n" +
        "v 0.894425 0.447215 0.000000\n" +
        "v 0.000000 1.000000 0.000000\n" +
        "s off\n" +
        "f 1 2 3\n" +
        "f 2 1 6\n" +
        "f 1 3 4\n" +
        "f 1 4 5\n" +
        "f 1 5 6\n" +
        "f 2 6 11\n" +
        "f 3 2 7\n" +
        "f 4 3 8\n" +
        "f 5 4 9\n" +
        "f 6 5 10\n" +
        "f 2 11 7\n" +
        "f 3 7 8\n" +
        "f 4 8 9\n" +
        "f 5 9 10\n" +
        "f 6 10 11\n" +
        "f 7 11 12\n" +
        "f 8 7 12\n" +
        "f 9 8 12\n" +
        "f 10 9 12\n" +
        "f 11 10 12\n"