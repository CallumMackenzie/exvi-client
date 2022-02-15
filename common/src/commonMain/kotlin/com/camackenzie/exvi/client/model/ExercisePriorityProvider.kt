/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.model.Exercise
import com.camackenzie.exvi.core.model.ExerciseSet
import com.camackenzie.exvi.core.model.Workout

/**
 *
 * @author callum
 */
interface ExercisePriorityProvider {
    fun getPriority(g: WorkoutGenerator, exerciseIndex: Int): Double
    fun generateExerciseSet(g: WorkoutGenerator, ex: Exercise): ExerciseSet
    fun registerMatch(
        g: WorkoutGenerator,
        w: Workout,
        contributed: Double,
        total: Double
    )
}