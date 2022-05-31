package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.model.Workout
import com.camackenzie.exvi.core.util.EncodedStringCache
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor

class FriendWorkout(
    val owner: EncodedStringCache,
    base: Workout,
) : Workout by base