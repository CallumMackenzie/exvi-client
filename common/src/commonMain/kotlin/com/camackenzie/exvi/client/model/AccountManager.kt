/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.util.SelfSerializable
import com.russhwolf.settings.Settings
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@kotlinx.serialization.Serializable
class AccountManager : SelfSerializable {

    var activeAccount: Account? = null

    fun hasActiveAccount(): Boolean = activeAccount != null

    fun signOut() {
        activeAccount = null
    }

    override fun getUID(): String = uid

    override fun toJson(): String = Json.encodeToString(this)

    companion object {
        const val uid = "AccountManager"
    }
}