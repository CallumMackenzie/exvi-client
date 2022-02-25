/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.model.BodyStats
import com.camackenzie.exvi.core.util.SelfSerializable
import kotlinx.serialization.*
import kotlinx.serialization.json.*

/**
 *
 * @author callum
 */
@kotlinx.serialization.Serializable
class WorkoutGeneratorParams(
    var minExercises: Int = 6,
    var maxExercises: Int = 9,
    var priorityRange: Double = 0.1,
    var bodyStats: BodyStats = BodyStats.average(),
    var providers: Array<ExercisePriorityProvider> = emptyArray()
) : SelfSerializable {

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

    override fun getUID(): String {
        return uid
    }

    override fun toJson(): String {
        return Json.encodeToString(this)
    }

    companion object {
        const val uid = "WorkoutGeneratorParams"
    }
}