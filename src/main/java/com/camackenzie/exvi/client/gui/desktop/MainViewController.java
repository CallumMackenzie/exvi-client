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
public class MainViewController extends ViewController<MainView, BackendModel> {

    public MainViewController(BackendModel model, MainView mainView) {
        super(mainView, model);
        this.setupControllers();
    }

    private void setupControllers() {
    }

}
