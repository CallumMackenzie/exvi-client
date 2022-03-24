/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model

import com.soywiz.krypto.SecureRandom
import kotlin.math.roundToInt

/**
 * @author callum
 */
object Random {

    fun doubleInRange(min: Double, max: Double): Double = SecureRandom.nextDouble() * (max - min) + min

    fun intInRange(min: Int, max: Int): Int = doubleInRange(min.toDouble(), max.toDouble()).roundToInt()
}