/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gluon;

import java.net.URL;

/**
 *
 * @author callum
 */
public enum Views {
    LOGIN("/fxml/LoginView.fxml"),
    SIGNUP("/fxml/SignupView.fxml"),
    HOME("/fxml/HomepageView.fxml"),
    CREATE_WORKOUT("/fxml/WorkoutCreationView.fxml");

    private final String path;

    private Views(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public String getID() {
        return super.toString();
    }

    public URL getURL() {
        return getClass().getResource(this.path);
    }

}
