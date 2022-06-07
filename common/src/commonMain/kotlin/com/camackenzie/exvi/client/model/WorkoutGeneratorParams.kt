/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.model.ActualBodyStats
import com.camackenzie.exvi.core.model.BodyStats
import com.camackenzie.exvi.core.util.SelfSerializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
@Suppress("unused", "UNCHECKED_CAST")
data class WorkoutGeneratorParams(
    var minExercises: Int = 6,
    var maxExercises: Int = 9,
    var priorityRange: Double = 0.1,
    var bodyStats: BodyStats = ActualBodyStats.average(),
    var providers: Array<ExercisePriorityProvider> = emptyArray()
) : SelfSerializable {

    override val serializer: KSerializer<SelfSerializable>
        get() = serializer() as KSerializer<SelfSerializable>

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as WorkoutGeneratorParams

        if (minExercises != other.minExercises) return false
        if (maxExercises != other.maxExercises) return false
        if (priorityRange != other.priorityRange) return false
        if (bodyStats != other.bodyStats) return false
        if (!providers.contentEquals(other.providers)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = minExercises
        result = 31 * result + maxExercises
        result = 31 * result + priorityRange.hashCode()
        result = 31 * result + bodyStats.hashCode()
        result = 31 * result + providers.contentHashCode()
        return result
    }

}