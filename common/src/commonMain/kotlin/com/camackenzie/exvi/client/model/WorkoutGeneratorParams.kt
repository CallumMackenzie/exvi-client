/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.model.BodyStats

/**
 *
 * @author callum
 */
class WorkoutGeneratorParams constructor(
    minExercises: Int = 6,
    maxExercises: Int = 10,
    priorityRange: Double = 0.1,
    bodyStats: BodyStats = BodyStats.average(),
    providers: Array<ExercisePriorityProvider> = arrayOf<ExercisePriorityProvider>()
) {
    var minExercises = 6
    var maxExercises = 10
    var priorityRange = 0.1
    var providers: Array<ExercisePriorityProvider>
    var bodyStats: BodyStats

    init {
        this.minExercises = minExercises
        this.maxExercises = maxExercises
        this.priorityRange = priorityRange
        this.providers = providers
        this.bodyStats = bodyStats
    }

    fun withBodyStats(bs: BodyStats): WorkoutGeneratorParams {
        bodyStats = bs
        return this
    }

    fun withMinExercises(n: Int): WorkoutGeneratorParams {
        minExercises = n
        return this
    }

    fun withMaxExercises(n: Int): WorkoutGeneratorParams {
        maxExercises = n
        return this
    }

    fun withPriorityRange(d: Double): WorkoutGeneratorParams {
        priorityRange = d
        return this
    }

    fun withExercisePriorityProviders(
        exProviders: Array<ExercisePriorityProvider>
    ): WorkoutGeneratorParams {
        providers = exProviders
        return this
    }

    fun setExerciseCount(n: Int) {
        maxExercises = n
        minExercises = n
    }

    fun withExerciseCount(n: Int): WorkoutGeneratorParams {
        setExerciseCount(n)
        return this
    }
}