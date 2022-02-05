/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gluon.elements;

import javafx.scene.control.TextField;

/**
 *
 * @author callum
 */
public class UsernameInput extends TextField {

    public UsernameInput() {
        this.lengthProperty()
                .addListener(new TextFieldLengthListener(this, 24));
        this.textProperty()
                .addListener(new TextFieldContentListener(this,
                        "([0-9a-z]|[._-])*"));
    }

}
