/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model

/**
 *
 * @author callum
 */
object APIEndpoints {
    private const val STAGE_NAME = "test"
    const val ROOT = "https://s36irvth41.execute-api.us-east-2.amazonaws.com/"
    private const val STAGE = ROOT + STAGE_NAME
    const val VERIFICATION = "$STAGE/verification"
    const val SIGN_UP = "$STAGE/signup"
    const val LOGIN = "$STAGE/login"
    const val GET_SALT = "$STAGE/salt"
    const val DATA = "$STAGE/data"
}