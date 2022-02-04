/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.javafx.elements;

import javafx.scene.control.PasswordField;

/**
 *
 * @author callum
 */
public class PasswordInput extends PasswordField {

    public PasswordInput() {
        this.lengthProperty()
                .addListener(new TextFieldLengthListener(this, 30));
        this.textProperty()
                .addListener(new TextFieldContentListener(this,
                        "([0-9a-zA-Z]|[*.!@#$%^&(){}\\[\\]:;<>,.?/~_+-=|])*"));
    }

}
