/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.model.*
import com.camackenzie.exvi.core.util.SelfSerializable
import kotlinx.serialization.*
import kotlinx.serialization.json.*

/**
 *
 * @author callum
 */
sealed interface ExercisePriorityProvider : SelfSerializable {
    fun getPriority(g: WorkoutGenerator, exercise: Exercise, exerciseIndex: Int): Double

    fun generateExerciseSet(g: WorkoutGenerator, ex: Exercise): ExerciseSet {
        return DefaultExerciseSetGenerator.generateExerciseSet(g, ex)
    }

    fun registerMatch(
        g: WorkoutGenerator,
        w: Workout,
        contributed: Double,
        total: Double
    ) {
    }
}

sealed interface ExerciseSetGenerator {
    fun generateExerciseSet(g: WorkoutGenerator, ex: Exercise): ExerciseSet
}

@kotlinx.serialization.Serializable
sealed class BoundedPriorityProvider : ExercisePriorityProvider {
    abstract val start: Int
    abstract val end: Int

    fun isInBounds(i: Int): Boolean {
        return i in start until end
    }

    protected fun getPriorityBounded(index: Int, onInvalid: () -> Double = { 0.0 }, onValid: () -> Double): Double {
        return if (this.isInBounds(index)) onValid() else onInvalid()
    }
}

@kotlinx.serialization.Serializable
object DefaultExerciseSetGenerator : ExerciseSetGenerator {
    override fun generateExerciseSet(g: WorkoutGenerator, ex: Exercise): ExerciseSet {
        return ExerciseSet(ex, "rep", arrayOf(10, 10, 10))
    }
}

@kotlinx.serialization.Serializable
class ExerciseMusclePriority(
    val muscle: MuscleWorkData,
    val priority: Double = 1.0,
    override val start: Int = 0,
    override val end: Int = Int.MAX_VALUE
) : BoundedPriorityProvider() {

    override fun getPriority(g: WorkoutGenerator, exercise: Exercise, exerciseIndex: Int): Double {
        return this.getPriorityBounded(exerciseIndex) {
            if (exercise.worksMuscle(muscle)) priority else 0.0
        }
    }

    override fun getUID(): String {
        return uid
    }

    override fun toJson(): String {
        return Json.encodeToString(this)
    }

    companion object {
        const val uid = "ExerciseMusclePriority"
    }
}

@kotlinx.serialization.Serializable
class ExerciseTypePriority(
    val type: ExerciseType,
    val priority: Double = 1.0,
    override val start: Int = 0,
    override val end: Int = Int.MAX_VALUE
) : BoundedPriorityProvider() {

    override fun getPriority(g: WorkoutGenerator, exercise: Exercise, exerciseIndex: Int): Double {
        return this.getPriorityBounded(exerciseIndex) {
            if (exercise.isType(type)) priority else 0.0
        }
    }

    override fun getUID(): String {
        return uid
    }

    override fun toJson(): String {
        return Json.encodeToString(this)
    }

    companion object {
        const val uid = "ExerciseTypePriority"
    }
}

@kotlinx.serialization.Serializable
class ExerciseEquipmentPriority(
    val equipment: ExerciseEquipment,
    val priority: Double = 1.0,
    override val start: Int = 0,
    override val end: Int = Int.MAX_VALUE
) : BoundedPriorityProvider() {

    override fun getPriority(g: WorkoutGenerator, exercise: Exercise, exerciseIndex: Int): Double {
        return this.getPriorityBounded(exerciseIndex) {
            if (exercise.usesEquipment(equipment)) priority else 0.0
        }
    }

    override fun getUID(): String {
        return uid
    }

    override fun toJson(): String {
        return Json.encodeToString(this)
    }

    companion object {
        const val uid = "ExerciseEquipmentPriority"
    }
}

@kotlinx.serialization.Serializable
class ExerciseExperiencePriority(
    val experience: ExerciseExperienceLevel,
    val priority: Double = 1.0,
    override val start: Int = 0,
    override val end: Int = Int.MAX_VALUE
) : BoundedPriorityProvider() {

    override fun getPriority(g: WorkoutGenerator, exercise: Exercise, exerciseIndex: Int): Double {
        return this.getPriorityBounded(exerciseIndex) {
            if (exercise.experienceLevel == experience) priority else 0.0
        }
    }

    override fun getUID(): String {
        return uid
    }

    override fun toJson(): String {
        return Json.encodeToString(this)
    }

    companion object {
        const val uid = "ExerciseExperiencePriority"
    }
}

@kotlinx.serialization.Serializable
class ExerciseForceTypePriority(
    val forceType: ExerciseForceType,
    val priority: Double = 1.0,
    override val start: Int = 0,
    override val end: Int = Int.MAX_VALUE
) : BoundedPriorityProvider() {

    override fun getPriority(g: WorkoutGenerator, exercise: Exercise, exerciseIndex: Int): Double {
        return this.getPriorityBounded(exerciseIndex) {
            if (exercise.forceType == forceType) priority else 0.0
        }
    }

    override fun getUID(): String {
        return uid
    }

    override fun toJson(): String {
        return Json.encodeToString(this)
    }

    companion object {
        const val uid = "ExerciseForceTypePriority"
    }
}

@kotlinx.serialization.Serializable
class ExerciseMechanicsPriority(
    val mechanics: ExerciseMechanics,
    val priority: Double = 1.0,
    override val start: Int = 0,
    override val end: Int = Int.MAX_VALUE
) : BoundedPriorityProvider() {

    override fun getPriority(g: WorkoutGenerator, exercise: Exercise, exerciseIndex: Int): Double {
        return this.getPriorityBounded(exerciseIndex) {
            if (exercise.mechanics == mechanics) priority else 0.0
        }
    }

    override fun getUID(): String {
        return uid
    }

    override fun toJson(): String {
        return Json.encodeToString(this)
    }

    companion object {
        const val uid = "ExerciseMechanicsPriority"
    }
}