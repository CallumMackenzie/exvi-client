/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.api.APIRequest
import com.camackenzie.exvi.core.api.APIResult
import com.camackenzie.exvi.core.api.BooleanResult
import com.camackenzie.exvi.core.api.CompatibleVersionRequest
import com.camackenzie.exvi.core.model.ExviSerializer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 *
 * @author callum
 */
object APIInfo {
    private const val STAGE_NAME = "test"
    const val ROOT = "https://s36irvth41.execute-api.us-east-2.amazonaws.com/"
    private const val STAGE = ROOT + STAGE_NAME
    const val VERIFICATION = "$STAGE/verification"
    const val SIGN_UP = "$STAGE/signup"
    const val LOGIN = "$STAGE/login"
    const val GET_SALT = "$STAGE/salt"
    const val DATA = "$STAGE/data"

    fun checkAppCompatibility(
        appVersion: Int,
        coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        onSuccess: () -> Unit = {},
        onFail: (APIResult<String>) -> Unit = {},
        onComplete: () -> Unit = {}
    ) = APIRequest.requestAsync(
        DATA, CompatibleVersionRequest(appVersion),
        APIRequest.jsonHeaders(),
        coroutineScope,
        dispatcher
    ) {
        if (it.failed()) onFail(it)
        else {
            val versionValid: BooleanResult = ExviSerializer.fromJson(it.body)
            if (versionValid.result) onSuccess()
            else onFail(it)
        }
        onComplete()
    }
}