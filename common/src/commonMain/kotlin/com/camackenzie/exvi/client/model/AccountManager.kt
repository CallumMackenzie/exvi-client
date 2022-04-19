/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.util.SelfSerializable
import kotlinx.serialization.KSerializer

@Suppress("UNCHECKED_CAST")
@kotlinx.serialization.Serializable
class AccountManager : SelfSerializable {

    override val serializer: KSerializer<SelfSerializable>
        get() = serializer() as KSerializer<SelfSerializable>
    var activeAccount: Account? = null

    fun hasActiveAccount(): Boolean = activeAccount != null

    fun signOut() {
        activeAccount = null
    }
}