package com.camackenzie.exvi.client.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.camackenzie.exvi.core.model.*

fun BodyStats.toComposable() = ComposeBodyStats(sex, totalMass, height)

open class ComposeBodyStats(
    sex: GeneticSex,
    totalMass: Mass,
    height: Distance,
) : BodyStats {
    override var height: Distance by mutableStateOf(height)
    override var sex: GeneticSex by mutableStateOf(sex)
    override var totalMass: Mass by mutableStateOf(totalMass)

    override fun getUID(): String = uid
    override fun toJson(): String = toActual().toJson()

    companion object {
        const val uid = "ComposeBodyStats"
    }
}