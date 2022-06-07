/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.model.*
import com.camackenzie.exvi.core.util.ExviLogger
import com.camackenzie.exvi.core.util.SelfSerializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


private typealias SearchFun = (Exercise) -> Int

private const val LOG_TAG = "EXERCISE_MANAGER"

@Serializable
@Suppress("unused", "UNCHECKED_CAST")
class ExerciseManager(
    @Transient
    var exercises: HashSet<Exercise> = HashSet()
) : SelfSerializable {

    @Transient
    var exercisesByMuscle: Map<Muscle?, Set<Exercise>> = emptyMap()
        private set

    @Transient
    var standardEquipment: Array<ExerciseEquipment> = emptyArray()
        private set

    override val serializer: KSerializer<SelfSerializable>
        get() = serializer() as KSerializer<SelfSerializable>

    constructor(vararg exs: Exercise) : this() {
        addAll(arrayOf(*exs))
    }

    constructor() : this(HashSet())

    fun addAll(exs: Array<Exercise>) {
        exercises.addAll(exs)
        exercisesByMuscle = ListUtils.groupByToSet(exs) {
            if (it.musclesWorked.isEmpty()) null
            else it.musclesWorked[0].muscle
        }
        val stdEquipment = HashSet<ExerciseEquipment>()
        for (exercise in exs) stdEquipment.addAll(exercise.equipment)
        standardEquipment = stdEquipment.toTypedArray()
        standardEquipment.sortWith { a, b -> a.name.compareTo(b.name) }
    }

    fun loadStandardExercises() {
        ExviLogger.i(tag = LOG_TAG) { "Loading standard exercise set" }
        val actualExercises = ExviSerializer.fromJson<Array<ActualExercise>>(readTextFile("exercises.json"))
        addAll(actualExercises as Array<Exercise>)
        StandardExercise.setStandardExerciseSet(actualExercises)
    }

    fun loadStandardExercisesIfEmpty() {
        if (!this.hasExercises()) this.loadStandardExercises()
        else ExviLogger.i(tag = LOG_TAG) { "Standard exercises already loaded" }
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