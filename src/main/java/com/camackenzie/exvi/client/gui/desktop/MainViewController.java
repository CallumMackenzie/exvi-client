/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

/**
 *
 * @author callum
 */
public class MainViewController {

    private final BackendModel model;
    private final MainView mainView;

    public MainViewController(BackendModel model, MainView mainView) {
        this.model = model;
        this.mainView = mainView;
        this.setupControllers();
    }

    public MainView getMainView() {
        return this.mainView;
    }

    public BackendModel getModel() {
        return this.model;
    }

    private void setupControllers() {
    }

}
