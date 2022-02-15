/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.model.Exercise
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlinx.serialization.*
import kotlinx.serialization.json.*

/**
 *
 * @author callum
 */
class ExerciseManager(var exercises: MutableSet<Exercise>) {

    constructor(json: String) : this() {
        addAllFromJson(json)
    }

    constructor(vararg exs: Exercise) : this() {
        addAll(arrayOf(*exs))
    }

    constructor() : this(HashSet()) {}

    fun addAll(exs: Array<Exercise>) {
        for (ex in exs) {
            exercises.add(ex)
        }
    }

    fun addAllFromJson(json: String) {
        addAll(Json.decodeFromString<Array<Exercise>>(json))
    }

    fun toJson(): String {
        return Json.encodeToString(exercises.toTypedArray())
    }

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