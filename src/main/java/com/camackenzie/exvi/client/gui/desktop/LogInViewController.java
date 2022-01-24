/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import com.camackenzie.exvi.client.gui.desktop.BackendModel;

/**
 *
 * @author callum
 */
public class LogInViewController extends ViewController<LogInView, BackendModel> {

    public LogInViewController(LogInView lv, BackendModel m) {
        super(lv, m);
        this.setupControllers();
    }

    private void setupControllers() {
    }

}
