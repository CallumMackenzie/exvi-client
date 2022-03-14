package com.camackenzie.exvi.client.rendering

import com.soywiz.korma.geom.*
import kotlinx.coroutines.*
import kotlin.math.PI
import kotlin.math.tan

typealias VertexShader = (Mesh3D, Vector3D) -> Unit
typealias PostVertexShader = (Triangle3D) -> Unit

private fun defaultVertexShader(camera: Camera3D): VertexShader =
    { mesh, vertex ->
        vertex.transform(mesh.transform)
            .transform(Matrix3D().setToTranslation(camera.position.x, camera.position.y, camera.position.z))
            .transform(camera.cameraMatrix)
            .transform(camera.projectionMatrix)
    }

interface Renderer3D {
    var postVertexShader: PostVertexShader
    var vertexShader: VertexShader
    var camera: Camera3D
    var sortTris: Boolean

    fun renderRaw(vararg meshes: Mesh3D): Array<Vector3D> {
        camera.calculateProjection = true
        camera.calculateCamera = true
        var nVerts = 0
        val verts = Array(meshes.map { it.points.size }.reduce { a, b -> a + b }) { Vector3D() }
        for (mesh in meshes) {
            for (vert in mesh.points) {
                vertexShader(
                    mesh,
                    verts[nVerts].setTo(vert.x, vert.y, vert.z, vert.w)
                )
                ++nVerts
            }
        }
        return verts
    }

    fun renderToTriangles(vararg meshes: Mesh3D): Array<Triangle3D> =
        pointsToTriangles(renderFull(*meshes))

    fun normaliseRenderedPointsRaw(transformedVerts: Array<Vector3D>): Array<Vector3D> =
        transformedVerts.map {
            it.x = (it.x / it.w) + 0.5f
            it.y = (it.y / it.w) + 0.5f
            it
        }.toTypedArray()

    fun normaliseRenderedPoints(transformedVerts: Array<Vector3D>): Array<Vector2D> =
        normaliseRenderedPointsRaw(transformedVerts).map {
            Vector2D(it.x.toDouble(), it.y.toDouble())
        }.toTypedArray()

    fun render(vararg meshes: Mesh3D): Array<Vector2D> =
        normaliseRenderedPoints(renderRaw(*meshes))

    fun renderFull(vararg meshes: Mesh3D): Array<Vector3D> =
        normaliseRenderedPointsRaw(renderRaw(*meshes))

    fun pointsToTriangles(points: Array<Vector3D>): Array<Triangle3D> {
        val tris = Array(points.size / 3) {
            val p0 = points[it * 3]
            val p1 = points[it * 3 + 1]
            val p2 = points[it * 3 + 2]
            val tri = Triangle3D(p0, p1, p2)
            postVertexShader(tri)
            tri
        }
        if (sortTris) {
            tris.sortWith { a, b ->
                a.center.w.compareTo(b.center.w)
            }
        }
        return tris
    }

    companion object {
        operator fun invoke(
            camera: Camera3D = Camera3D(),
            vertexShader: VertexShader = defaultVertexShader(camera),
            postVertexShader: (Triangle3D) -> Unit = {}
        ): Renderer3D = ActualRenderer3D(camera, vertexShader, postVertexShader)
    }

}

data class AsyncRenderer3D(
    override var camera: Camera3D = Camera3D(),
    override var vertexShader: VertexShader = defaultVertexShader(camera),
    override var postVertexShader: (Triangle3D) -> Unit = {},
    override var sortTris: Boolean = true
) : Renderer3D {

    fun renderAsyncToTriangles(
        coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        nThreads: Int = 2,
        threadCompleteListener: (Int) -> Unit = {},
        meshes: Array<out Mesh3D>,
        onRenderComplete: (Array<Triangle3D>) -> Unit
    ): Job = coroutineScope.launch(dispatcher) {
        val tris = Array(nThreads) { emptyArray<Triangle3D>() }
        List(nThreads) {
            coroutineScope.launch(dispatcher) {
                val renderer = Renderer3D(camera, vertexShader, postVertexShader)
                tris[it] = renderer.renderToTriangles(*meshes)
                threadCompleteListener(it)
            }
        }.joinAll()
        onRenderComplete(tris.reduce { l, r -> l + r })
    }

}

data class ActualRenderer3D(
    override var camera: Camera3D = Camera3D(),
    override var vertexShader: VertexShader = defaultVertexShader(camera),
    override var postVertexShader: (Triangle3D) -> Unit = {},
    override var sortTris: Boolean = true
) : Renderer3D

interface Camera3D {
    var position: Vector3D
    var rotation: Quaternion
    var aspectRatio: Float
    var fov: Angle
    var near: Float
    var far: Float
    var calculateProjection: Boolean
    var calculateCamera: Boolean

    var projectionMatrix: Matrix3D
    var cameraMatrix: Matrix3D

    val lookVector: Vector3D
        get() = Matrix3D().rotate(rotation).transform(Vector3D(0f, 0f, 1f))

    companion object {
        operator fun invoke(
            position: Position3D = Vector3D(),
            rotation: Quaternion = Quaternion(),
            aspectRatio: Float = 1f,
            fov: Angle = 90.degrees,
            near: Float = 0.1f,
            far: Float = 100f
        ): Camera3D = ActualCamera3D(
            position, rotation, aspectRatio, fov, near, far
        )
    }
}

open class ActualCamera3D(
    position: Position3D = Vector3D(),
    rotation: Quaternion = Quaternion(),
    aspectRatio: Float = 1f,
    fov: Angle = 90.degrees,
    near: Float = 0.1f,
    far: Float = 100f
) : Camera3D {
    override var position: Vector3D = position
        set(value) {
            calculateCamera = true
            field = value
        }
    override var rotation: Quaternion = rotation
        set(value) {
            calculateCamera = true
            field = value
        }
    override var aspectRatio: Float = aspectRatio
        set(value) {
            calculateProjection = true
            field = value
        }
    override var fov: Angle = fov
        set(value) {
            calculateProjection = true
            field = value
        }
    override var near: Float = near
        set(value) {
            calculateProjection = true
            field = value
        }
    override var far: Float = far
        set(value) {
            calculateProjection = true
            field = value
        }

    override var calculateProjection = true
    override var calculateCamera = true

    override var projectionMatrix: Matrix3D = Matrix3D()
        get() {
            if (calculateProjection) {
                val fovRad: Double = 1.0 / tan(fov.degrees * 0.5 * (PI / 180.0)).toFloat()
                field = Matrix3D()
                field[0, 0] = aspectRatio * fovRad
                field[1, 1] = fovRad
                field[2, 2] = far / (far - near)
                field[3, 2] = -far * near / (far - near)
                field[2, 3] = 1f
                field[3, 3] = 0f
                calculateProjection = false
            }
            return field
        }

    override var cameraMatrix: Matrix3D = Matrix3D()
        get() {
            if (calculateCamera) {
                val up = Vector3D(0f, 1f, 0f)
                val target = position + Vector3D(0f, 0f, 1f)
                    .transform(Matrix3D().setToRotation(rotation))
                field = Matrix3D().setToLookAt(position, target, up).invert()
                calculateCamera = false
            }
            return field
        }
}

fun Array<Vector3D>.average(): Vector3D = this.reduce { a, b -> a + b } * (1f / size.toFloat())

data class Triangle3D(
    var p0: Vector3D,
    var p1: Vector3D,
    var p2: Vector3D
) {
    val points: Array<Vector3D>
        get() = arrayOf(p0, p1, p2)
    val center: Vector3D
        get() = points.average()

    val p0p1: Vector3D
        get() = arrayOf(p0, p1).average()
    val p1p2: Vector3D
        get() = arrayOf(p1, p2).average()
    val p0p2: Vector3D
        get() = arrayOf(p0, p2).average()

    val normal: Vector3D
        get() = p0.cross(p1, p2)
}

fun Array<Triangle3D>.toVectorArray(): Array<Vector3D> = Array(size * 3) {
    get(it / 3).points[it % 3]
}

interface Mesh3D {
    val points: Array<Vector3D>
    var transform: Matrix3D

    companion object {
        private val vertexRegex = Regex("\\s+")
        private val faceRegex = Regex("(\\s|\\/)+")

        operator fun invoke(
            points: Array<Vector3D> = emptyArray(),
            transform: Matrix3D = Matrix3D()
        ): Mesh3D = ActualMesh3D(points, transform)

        fun fromObj(data: String): Array<Triangle3D> {
            val verts = ArrayList<Vector3D>()
            val triangles = ArrayList<Triangle3D>()
            var hasTextures = false
            var hasNormals = false
            for (line in data.lines()) {
                if (line.isNotBlank()) {
                    when (line[0]) {
                        'v' -> when (line[1]) {
                            ' ' -> {
                                val ns = line.split(vertexRegex)
                                    .filterIndexed { index, _ -> index != 0 }
                                    .map { it.toFloat() }
                                verts.add(Vector3D(ns[0], ns[1], ns[2]))
                            }
                            'n' -> hasNormals = true
                            't' -> hasTextures = true
                        }
                        'f' -> {
                            val ins = line.split(faceRegex)
                                .filterIndexed { index, _ -> index != 0 }
                                .map { it.toInt() - 1 }

                            triangles.add(
                                if (hasTextures && hasNormals)
                                    Triangle3D(verts[ins[0]], verts[ins[3]], verts[ins[6]])
                                else if (hasTextures || hasNormals)
                                    Triangle3D(verts[ins[0]], verts[ins[2]], verts[ins[4]])
                                else
                                    Triangle3D(verts[ins[0]], verts[ins[1]], verts[ins[2]])
                            )
                        }
                    }
                }
            }
            return triangles.toTypedArray()
        }
    }
}

open class ActualMesh3D(
    override val points: Array<Vector3D> = emptyArray(),
    override var transform: Matrix3D = Matrix3D()
) : Mesh3D {

    constructor(vararg verts: Vector3D) : this(arrayOf(*verts))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Mesh3D

        if (!points.contentEquals(other.points)) return false
        if (transform != other.transform) return false

        return true
    }

    override fun hashCode(): Int {
        var result = points.contentHashCode()
        result = 31 * result + transform.hashCode()
        return result
    }

}