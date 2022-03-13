/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.model.Exercise
import com.camackenzie.exvi.core.util.SelfSerializable
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlinx.serialization.*
import kotlinx.serialization.json.*


@kotlinx.serialization.Serializable
data class ExerciseManager(
    @Transient
    var exercises: HashSet<Exercise> = HashSet()
) : SelfSerializable {

    constructor(vararg exs: Exercise) : this() {
        addAll(arrayOf(*exs))
    }

    constructor() : this(HashSet())

    fun addAll(exs: Array<Exercise>) {
        for (ex in exs) {
            exercises.add(ex)
        }
    }

    fun loadStandardExercises() = addAll(Json.decodeFromString(readTextFile("exercises.json")))

    fun loadStandardExercisesIfEmpty() {
        if (!this.hasExercises()) {
            this.loadStandardExercises()
        }
    }

    fun hasExercises(): Boolean = exercises.size != 0

    override fun getUID(): String = uid

    override fun toJson(): String = Json.encodeToString(this)

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

    companion object {
        const val uid = "ExerciseManager"
    }
}