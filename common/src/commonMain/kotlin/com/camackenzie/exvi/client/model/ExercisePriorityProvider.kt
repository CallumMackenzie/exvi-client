@file:Suppress("UNCHECKED_CAST")
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.model.*
import com.camackenzie.exvi.core.util.SelfSerializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

data class WeightedExerciseSet(
    val exerciseSet: ExerciseSet,
    val weight: Double = 1.0
)

@Serializable
sealed class ExercisePriorityProvider : SelfSerializable {
    abstract fun getPriority(g: WorkoutGenerator, exercise: Exercise, index: Int): Double

    abstract fun generateExerciseSet(g: WorkoutGenerator, ex: Exercise): WeightedExerciseSet

    abstract fun registerMatch(
        g: WorkoutGenerator,
        w: Workout,
        contributed: Double,
        total: Double
    )
}

@Serializable
sealed class ExerciseSetGenerator {
    abstract fun generateExerciseSet(g: WorkoutGenerator, ex: Exercise): WeightedExerciseSet
}

@Serializable
sealed class BoundedPriorityProvider : ExercisePriorityProvider() {
    abstract val start: Int
    abstract val end: Int
    abstract val setGenerator: ExerciseSetGenerator

    fun isInBounds(i: Int): Boolean = i in start until end

    protected fun getPriorityBounded(index: Int, onInvalid: () -> Double = { 0.0 }, onValid: () -> Double): Double =
        if (this.isInBounds(index)) onValid() else onInvalid()

    override fun generateExerciseSet(g: WorkoutGenerator, ex: Exercise): WeightedExerciseSet =
        setGenerator.generateExerciseSet(g, ex)

    override fun registerMatch(g: WorkoutGenerator, w: Workout, contributed: Double, total: Double) {}
}

@Serializable
object DefaultExerciseSetGenerator : ExerciseSetGenerator() {
    override fun generateExerciseSet(g: WorkoutGenerator, ex: Exercise): WeightedExerciseSet =
        WeightedExerciseSet(ExerciseSet(ex, "rep", arrayOf(8, 8, 8)))
}

/**
 * Prioritizes exercises which work the given muscle
 */
@Serializable
class ExerciseMusclePriority(
    val muscle: MuscleWorkData,
    val priority: Double = 1.0,
    override val start: Int = 0,
    override val end: Int = Int.MAX_VALUE,
    override val setGenerator: ExerciseSetGenerator = DefaultExerciseSetGenerator
) : BoundedPriorityProvider() {

    override val serializer: KSerializer<SelfSerializable>
        get() = serializer() as KSerializer<SelfSerializable>

    override fun getPriority(g: WorkoutGenerator, exercise: Exercise, index: Int): Double =
        this.getPriorityBounded(index) {
            if (exercise.worksMuscle(muscle)) priority else 0.0
        }

}

/**
 * Prioritizes exercises with the given exercise type
 */
@Serializable
class ExerciseTypePriority(
    val exerciseType: ExerciseType,
    val priority: Double = 1.0,
    override val start: Int = 0,
    override val end: Int = Int.MAX_VALUE,
    override val setGenerator: ExerciseSetGenerator = DefaultExerciseSetGenerator
) : BoundedPriorityProvider() {

    override val serializer: KSerializer<SelfSerializable>
        get() = serializer() as KSerializer<SelfSerializable>

    override fun getPriority(g: WorkoutGenerator, exercise: Exercise, index: Int): Double =
        this.getPriorityBounded(index) {
            if (exercise.isType(exerciseType)) priority else 0.0
        }
}

/**
 * Prioritizes exercises with the given equipment
 */
@Serializable
class ExerciseEquipmentPriority(
    val equipment: ExerciseEquipment,
    val priority: Double = 1.0,
    override val start: Int = 0,
    override val end: Int = Int.MAX_VALUE,
    override val setGenerator: ExerciseSetGenerator = DefaultExerciseSetGenerator
) : BoundedPriorityProvider() {

    override val serializer: KSerializer<SelfSerializable>
        get() = serializer() as KSerializer<SelfSerializable>

    override fun getPriority(g: WorkoutGenerator, exercise: Exercise, index: Int): Double =
        this.getPriorityBounded(index) {
            if (exercise.usesEquipment(equipment)) priority else 0.0
        }
}

/**
 * Prioritizes exercises with the given experience level
 */
@Serializable
class ExerciseExperiencePriority(
    val experience: ExerciseExperienceLevel,
    val priority: Double = 1.0,
    override val start: Int = 0,
    override val end: Int = Int.MAX_VALUE,
    override val setGenerator: ExerciseSetGenerator = DefaultExerciseSetGenerator
) : BoundedPriorityProvider() {

    override val serializer: KSerializer<SelfSerializable>
        get() = serializer() as KSerializer<SelfSerializable>

    override fun getPriority(g: WorkoutGenerator, exercise: Exercise, index: Int): Double =
        this.getPriorityBounded(index) {
            if (exercise.experienceLevel == experience) priority else 0.0
        }
}

/**
 * Prioritizes exercises with the given force type
 */
@Serializable
class ExerciseForceTypePriority(
    val forceType: ExerciseForceType,
    val priority: Double = 1.0,
    override val start: Int = 0,
    override val end: Int = Int.MAX_VALUE,
    override val setGenerator: ExerciseSetGenerator = DefaultExerciseSetGenerator
) : BoundedPriorityProvider() {

    override val serializer: KSerializer<SelfSerializable>
        get() = serializer() as KSerializer<SelfSerializable>

    override fun getPriority(g: WorkoutGenerator, exercise: Exercise, index: Int): Double =
        this.getPriorityBounded(index) {
            if (exercise.forceType == forceType) priority else 0.0
        }

}

/**
 * Prioritizes exercises with the given mechanics
 */
@Serializable
class ExerciseMechanicsPriority(
    val mechanics: ExerciseMechanics,
    val priority: Double = 1.0,
    override val start: Int = 0,
    override val end: Int = Int.MAX_VALUE,
    override val setGenerator: ExerciseSetGenerator = DefaultExerciseSetGenerator
) : BoundedPriorityProvider() {

    override val serializer: KSerializer<SelfSerializable>
        get() = serializer() as KSerializer<SelfSerializable>

    override fun getPriority(g: WorkoutGenerator, exercise: Exercise, index: Int): Double {
        return this.getPriorityBounded(index) {
            if (exercise.mechanics == mechanics) priority else 0.0
        }
    }
}

/**
 * Prioritizes exercises with more complete information
 */
@Serializable
class ExerciseCompletionPriority(
    val priority: Double,
    override val start: Int = 0,
    override val end: Int = Int.MAX_VALUE,
    override val setGenerator: ExerciseSetGenerator = DefaultExerciseSetGenerator
) : BoundedPriorityProvider() {

    override val serializer: KSerializer<SelfSerializable>
        get() = serializer() as KSerializer<SelfSerializable>

    override fun getPriority(g: WorkoutGenerator, exercise: Exercise, index: Int): Double =
        this.getPriorityBounded(index) {
            var sum = 0
            if (exercise.hasDescription()) sum += 1
            if (exercise.hasOverview()) sum += 1
            if (exercise.hasTips()) sum += 1
            if (exercise.hasVideoLink()) sum += 1
            sum * priority
        }
}