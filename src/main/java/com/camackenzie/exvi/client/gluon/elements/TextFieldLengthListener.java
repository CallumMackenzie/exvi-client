/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */package com.camackenzie.exvi.client.gluon.elements;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

/**
 *
 * @author callum
 */
public class TextFieldLengthListener implements ChangeListener<Number> {

    private final int maxLength;
    private final TextField field;

    public TextFieldLengthListener(TextField field, int maxLen) {
        this.maxLength = maxLen;
        this.field = field;
    }

    @Override
    public void changed(ObservableValue<? extends Number> observable,
            Number oldValue, Number newValue) {
        if (newValue.intValue() > oldValue.intValue()) {
            if (field.getText().length() >= this.maxLength) {
                this.field.setText(
                        this.field.getText().substring(0, this.maxLength));
            }
        }
    }

}
