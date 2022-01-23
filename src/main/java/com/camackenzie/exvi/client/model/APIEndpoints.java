/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model;

/**
 *
 * @author callum
 */
public class APIEndpoints {

    public static final String STAGE_NAME = "test",
            ROOT = "https://s36irvth41.execute-api.us-east-2.amazonaws.com/",
            STAGE = ROOT + STAGE_NAME,
            VERIFICATION = STAGE + "/verification",
            SIGN_UP = STAGE + "/signup";

}
