/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.model.ActualExercise
import com.camackenzie.exvi.core.model.Exercise
import com.camackenzie.exvi.core.model.ExviSerializer
import com.camackenzie.exvi.core.model.StandardExercise
import com.camackenzie.exvi.core.util.SelfSerializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
@Suppress("unused", "UNCHECKED_CAST")
data class ExerciseManager(
    @Transient
    var exercises: HashSet<Exercise> = HashSet()
) : SelfSerializable {

    override val serializer: KSerializer<SelfSerializable>
        get() = serializer() as KSerializer<SelfSerializable>

    constructor(vararg exs: Exercise) : this() {
        addAll(arrayOf(*exs))
    }

    constructor() : this(HashSet())

    fun addAll(exs: Array<Exercise>) = exercises.addAll(exs)

    fun loadStandardExercises() {
        val actualExercises = ExviSerializer.fromJson<Array<ActualExercise>>(readTextFile("exercises.json"))
        addAll(actualExercises as Array<Exercise>)
        StandardExercise.setStandardExerciseSet(actualExercises)
    }

    fun loadStandardExercisesIfEmpty() {
        if (!this.hasExercises()) this.loadStandardExercises()
    }

    fun hasExercises(): Boolean = exercises.size != 0

    fun getExercisesByFunction(add: (Exercise) -> Boolean): ArrayList<Exercise> {
        val ret: ArrayList<Exercise> = ArrayList()
        for (exercise in exercises) {
            if (add(exercise)) {
                ret.add(exercise)
            }
        }
        return ret
    }

    fun getFirstExerciseByFunction(add: (Exercise) -> Boolean): Exercise? {
        for (exercise in exercises) {
            if (add(exercise)) {
                return exercise
            }
        }
        return null
    }
}