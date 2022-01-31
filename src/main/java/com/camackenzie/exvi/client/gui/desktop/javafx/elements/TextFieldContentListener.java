/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.javafx.elements;

import java.util.regex.Pattern;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

/**
 *
 * @author callum
 */
public class TextFieldContentListener implements ChangeListener<String> {

    private final TextField field;
    private final Pattern regex;

    public TextFieldContentListener(TextField field, String regex) {
        this.field = field;
        this.regex = Pattern.compile(regex);
    }

    @Override
    public void changed(ObservableValue<? extends String> observable,
            String oldVal,
            String newVal) {
        if (!this.regex.matcher(newVal).matches()) {
            this.field.setText(oldVal);
        }
    }

}
