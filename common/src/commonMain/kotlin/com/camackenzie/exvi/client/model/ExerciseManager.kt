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
import kotlinx.serialization.Transient


private typealias SearchFun = (Exercise) -> Int

@Serializable
@Suppress("unused", "UNCHECKED_CAST")
class ExerciseManager(
    @Transient
    var exercises: HashSet<Exercise> = HashSet()
) : SelfSerializable {

    @Transient
    var exercisesByName: MutableList<Exercise> = ArrayList(exercises.size)
        private set

    @Transient
    var exercisesByMuscle: Map<Muscle?, Set<Exercise>> = emptyMap()
        private set

    @Transient
    var exercisesByExperience: Map<ExerciseExperienceLevel, List<Exercise>> = emptyMap()
        private set

    @Transient
    var exercisesByMechanics: Map<ExerciseMechanics, List<Exercise>> = emptyMap()
        private set

    override val serializer: KSerializer<SelfSerializable>
        get() = serializer() as KSerializer<SelfSerializable>

    constructor(vararg exs: Exercise) : this() {
        addAll(arrayOf(*exs))
    }

    constructor() : this(HashSet())

    fun addAll(exs: Array<Exercise>) {
        exercises.addAll(exs)
        ListUtils.addAllToSortedArray(exs, exercisesByName) { a, b -> b.name.compareTo(a.name) }
        exercisesByMuscle = ListUtils.groupByToSet(exs) {
            if (it.musclesWorked.isEmpty()) null
            else it.musclesWorked[0].muscle
        }
        exercisesByExperience = ListUtils.groupBy(exs) { it.experienceLevel }
        exercisesByMechanics = ListUtils.groupBy(exs) { it.mechanics }
    }

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