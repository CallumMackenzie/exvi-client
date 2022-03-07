package com.camackenzie.exvi.client.rendering

import com.soywiz.korma.geom.*
import kotlin.math.PI
import kotlin.math.tan

data class Renderer3D(
    var camera: Camera3D,
    var vertexShader: (Mesh3D, Vector3D) -> Unit = { mesh, vertex ->
        vertex.transform(mesh.transform)
            .transform(Matrix3D().setToTranslation(camera.position.x, camera.position.y, camera.position.z))
            .transform(camera.cameraMatrix)
            .transform(camera.projectionMatrix)
    },
    var postVertexShader: (Triangle3D) -> Unit = {}
) {
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

    /**
     * Returns an array of triangle coordinates
     */
    fun render(vararg meshes: Mesh3D): Array<Vector2D> =
        normaliseRenderedPoints(renderRaw(*meshes))

    fun renderFull(vararg meshes: Mesh3D): Array<Vector3D> =
        normaliseRenderedPointsRaw(renderRaw(*meshes))

    private fun pointsToTriangles(points: Array<Vector3D>): Array<Triangle3D> =
        Array(points.size / 3) {
            val p0 = points[it * 3]
            val p1 = points[it * 3 + 1]
            val p2 = points[it * 3 + 2]
            val tri = Triangle3D(p0, p1, p2)
            postVertexShader(tri)
            tri
        }

    fun renderToTriangles(vararg meshes: Mesh3D): Array<Triangle3D> =
        pointsToTriangles(renderFull(*meshes))

    private fun normaliseRenderedPointsRaw(transformedVerts: Array<Vector3D>): Array<Vector3D> =
        transformedVerts.map {
            it.x = (it.x / it.w) + 0.5f
            it.y = (it.y / it.w) + 0.5f
            it
        }.toTypedArray()

    private fun normaliseRenderedPoints(transformedVerts: Array<Vector3D>): Array<Vector2D> =
        normaliseRenderedPointsRaw(transformedVerts).map {
            Vector2D(it.x.toDouble(), it.y.toDouble())
        }.toTypedArray()
}

class Camera3D(
    position: Position3D = Vector3D(),
    rotation: Quaternion = Quaternion(),
    aspectRatio: Float = 1f,
    fov: Angle = 90.degrees,
    near: Float = 0.1f,
    far: Float = 100f
) {
    var position: Vector3D = position
        set(value) {
            calculateCamera = true
            field = value
        }
    var rotation: Quaternion = rotation
        set(value) {
            calculateCamera = true
            field = value
        }
    var aspectRatio: Float = aspectRatio
        set(value) {
            calculateProjection = true
            field = value
        }
    var fov: Angle = fov
        set(value) {
            calculateProjection = true
            field = value
        }
    var near: Float = near
        set(value) {
            calculateProjection = true
            field = value
        }
    var far: Float = far
        set(value) {
            calculateProjection = true
            field = value
        }

    var calculateProjection = true
    var calculateCamera = true

    var projectionMatrix: Matrix3D = Matrix3D()
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
        private set

    var cameraMatrix: Matrix3D = Matrix3D()
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
        private set
}

data class Triangle3D(
    var p0: Vector3D,
    var p1: Vector3D,
    var p2: Vector3D
) {
    val points: Array<Vector3D> = arrayOf(p0, p1, p2)
}

inline fun Array<Triangle3D>.toVectorArray(): Array<Vector3D> = Array(size * 3) {
    get(it / 3).points[it % 3]
}

data class Mesh3D(
    val points: Array<Vector3D> = emptyArray(),
    var transform: Matrix3D = Matrix3D()
) {

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

    companion object {
        private val vertexRegex = Regex("\\s+")
        private val faceRegex = Regex("(\\s|\\/)+")

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
                            val inds = line.split(faceRegex)
                                .filterIndexed { index, _ -> index != 0 }
                                .map { it.toInt() - 1 }

                            triangles.add(
                                if (hasTextures && hasNormals)
                                    Triangle3D(verts[inds[0]], verts[inds[3]], verts[inds[6]])
                                else if (hasTextures || hasNormals)
                                    Triangle3D(verts[inds[0]], verts[inds[2]], verts[inds[4]])
                                else
                                    Triangle3D(verts[inds[0]], verts[inds[1]], verts[inds[2]])
                            )
                        }
                    }
                }
            }
            return triangles.toTypedArray()
        }
    }
}