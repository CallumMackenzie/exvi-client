/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

/**
 *
 * @author Alexx
 */
public abstract class ViewController<VIEW, MODEL> {

    private final VIEW view;
    private final MODEL model;

    public ViewController(VIEW view, MODEL model) {
        this.view = view;
        this.model = model;
    }

    public MODEL getModel() {
        return this.model;
    }

    public VIEW getView() {
        return this.view;
    }

}
