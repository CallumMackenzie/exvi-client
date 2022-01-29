/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.swing;

import com.camackenzie.exvi.client.gui.desktop.BackendModel;

/**
 *
 * @author callum
 */
public class AppEntry implements Runnable {

    @Override
    public void run() {

        BackendModel model = new BackendModel();
        MainView mainView = new MainView(model);
        MainViewController mainViewController = new MainViewController(
                model, mainView
        );
        
        mainView.setVisible(true);

    }

}
