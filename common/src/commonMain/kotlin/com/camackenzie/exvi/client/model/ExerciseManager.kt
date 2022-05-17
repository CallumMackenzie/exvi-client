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
    var exercisesByMuscle: MutableList<Exercise> = ArrayList(exercises.size)
        private set

    @Transient
    var exercisesByExperience: MutableList<Exercise> = ArrayList(exercises.size)
        private set

    @Transient
    var exercisesByMechanics: MutableList<Exercise> = ArrayList(exercises.size)
        private set

    override val serializer: KSerializer<SelfSerializable>
        get() = serializer() as KSerializer<SelfSerializable>

    constructor(vararg exs: Exercise) : this() {
        addAll(arrayOf(*exs))
    }

    constructor() : this(HashSet())

    fun addAll(exs: Array<Exercise>) {
        exercises.addAll(exs)
        for (ex in exs) {
            ListUtils.addToSortedArray(ex, exercisesByName, exercisesByNameComparator(ex.name))
            ListUtils.addToSortedArray(ex, exercisesByExperience, exercisesByExperienceComparator(ex.experienceLevel))
            ListUtils.addToSortedArray(ex, exercisesByMechanics, exercisesByMechanicsComparator(ex.mechanics))
            ListUtils.addToSortedArray(ex, exercisesByMuscle, exercisesByMuscleComparator(ex.musclesWorked))
        }
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

    companion object {

        inline fun exercisesByMuscleComparator(muscles: Array<MuscleWorkData>): SearchFun = {
            if (it.musclesWorked.isEmpty() && muscles.isEmpty()) 0
            else if (it.musclesWorked.isEmpty()) 1
            else if (muscles.isEmpty()) -1
            else it.musclesWorked[0].muscle.compareTo(muscles[0].muscle)
        }

        inline fun exercisesByExperienceComparator(experienceLevel: ExerciseExperienceLevel): SearchFun = {
            it.experienceLevel.compareTo(experienceLevel)
        }

        inline fun exercisesByMechanicsComparator(mechanics: ExerciseMechanics): SearchFun = {
            it.mechanics.compareTo(mechanics)
        }

        inline fun exercisesByNameComparator(name: String): SearchFun = { it.name.compareTo(name) }

    }
}