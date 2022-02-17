/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model

class AccountManager {

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

}