/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.util.SelfSerializable
import kotlinx.serialization.*
import kotlinx.serialization.json.*

//@androidx.annotation.Keep
@kotlinx.serialization.Serializable
class AccountManager : SelfSerializable {

    var activeAccount: Account? = null
        set(account) {
            if (activeAccount != null) {
                activeAccount!!.signOut()
            }
            field = account
        }

    fun hasActiveAccount(): Boolean {
        return activeAccount != null
    }

    override fun getUID(): String {
        return "AccountManager"
    }

    override fun toJson(): String {
        return Json.encodeToString(this)
    }

}