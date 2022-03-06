package com.camackenzie.exvi.client.rendering

import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.triangle.Triangle
import kotlin.math.PI
import kotlin.math.tan

data class Renderer3D(
    var camera: Camera3D,
    var vertexShader: (Mesh3D, Vector3D) -> Unit = { mesh, vertex ->
        val new = vertex + camera.position
        vertex.setTo(new.x, new.y, new.z)
        vertex.transform(mesh.transform)
            .transform(camera.cameraMatrix)
            .transform(camera.projectionMatrix)
    }
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
        render(renderRaw(*meshes))

    private fun pointsToTriangles(points: Array<Vector2D>): Array<Triangle> =
        Array(points.size / 3) {
            val p0 = points[it * 3]
            val p1 = points[it * 3 + 1]
            val p2 = points[it * 3 + 2]
            Triangle(p0, p1, p2, fixOrientation = true, checkOrientation = false)
        }

    fun renderToTriangles(vararg meshes: Mesh3D): Array<Triangle> =
        pointsToTriangles(render(*meshes))

    private fun render(transformedVerts: Array<Vector3D>): Array<Vector2D> =
        transformedVerts.map {
            val vec = Vector2D()
            vec.x = (it.x / it.w).toDouble() + 0.5
            vec.y = (it.y / it.w).toDouble() + 0.5
            vec
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

    val projectionMatrix: Matrix3D = Matrix3D()
        get() {
            if (calculateProjection) {
                val fovRad: Double = 1.0 / tan(fov.degrees * 0.5 * (PI / 180.0)).toFloat()
                field.identity()
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

    val cameraMatrix: Matrix3D = Matrix3D()
        get() {
            if (calculateCamera) {
                val vUp = Vector3D(0f, 1f, 0f)
                var target = Vector3D(0f, 0f, 1f)
                field.setToRotation(rotation)
                target = position + target.transform(field)
                field.setToLookAt(position, target, vUp).invert()
                calculateCamera = false
            }
            return field
        }
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
        private val splitRegex = Regex("\\s+")

        fun fromObj(data: String): Mesh3D {
            val verts = ArrayList<Vector3D>()
            for (line in data.lines()) {
                if (line.isNotBlank()) {
                    when (line[0]) {
                        'v' -> when (line[1]) {
                            ' ' -> {
                                val ns = line.split(splitRegex)
                                    .filterIndexed { index, _ -> index != 0 }
                                    .map { it.toFloat() }
                                verts.add(Vector3D().setTo(ns[0], ns[1], ns[2]))
                            }
                        }
                    }
                }
            }
            return Mesh3D(verts.toTypedArray())
        }
    }
}