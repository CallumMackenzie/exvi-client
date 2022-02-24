/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.model.Exercise
import com.camackenzie.exvi.core.model.ExerciseSet
import com.camackenzie.exvi.core.model.Workout
import com.camackenzie.exvi.core.model.Muscle

/**
 *
 * @author callum
 */
interface ExercisePriorityProvider {
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

interface ExerciseSetGenerator {
    fun generateExerciseSet(g: WorkoutGenerator, ex: Exercise): ExerciseSet
}

@kotlinx.serialization.Serializable
abstract class BoundedPriorityProvider : ExercisePriorityProvider {
    abstract val start: Int
    abstract val end: Int

    fun isInBounds(i: Int): Boolean {
        return i in start until end
    }

    protected fun getPriority(index: Int, onValid: () -> Double, onInvalid: () -> Double = { 0.0 }): Double {
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
    val muscle: Muscle,
    val priority: Double = 1.0,
    override val start: Int = 0,
    override val end: Int = Int.MAX_VALUE
) : BoundedPriorityProvider() {

    override fun getPriority(g: WorkoutGenerator, exercise: Exercise, exerciseIndex: Int): Double {
        return this.getPriority(exerciseIndex, onValid = {
            if (exercise.worksMuscle(muscle)) priority else 0.0
        })
    }
}