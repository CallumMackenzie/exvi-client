package com.camackenzie.exvi.client.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.camackenzie.exvi.core.model.*
import com.camackenzie.exvi.core.util.SelfSerializable
import kotlinx.serialization.KSerializer

fun BodyStats.toComposable() = ComposeBodyStats(sex, totalMass, height)

@Suppress("UNCHECKED_CAST")
open class ComposeBodyStats(
    sex: GeneticSex,
    totalMass: Mass,
    height: Distance,
) : BodyStats {
    override val serializer: KSerializer<SelfSerializable>
        get() = ActualBodyStats.serializer() as KSerializer<SelfSerializable>

    override var height: Distance by mutableStateOf(height)
    override var sex: GeneticSex by mutableStateOf(sex)
    override var totalMass: Mass by mutableStateOf(totalMass)
}