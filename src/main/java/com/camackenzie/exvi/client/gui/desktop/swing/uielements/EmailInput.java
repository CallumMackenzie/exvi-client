/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.swing.uielements;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 *
 * @author callum
 */
public class EmailInput extends PromptedTextField {

    private String emailError = "Please enter an email address";

    public EmailInput() {
        super(new JTextField(), "Email");
        this.getTextField().addKeyListener(new EmailKeyListener());
        this.getTextField().addCaretListener(new EmailCaretListener());
    }

    public String getEmailError() {
        return this.emailError;
    }

    public boolean isEmailValid() {
        if (this.emailError == null) {
            return false;
        }
        return this.emailError.equals("");
    }

    public String getEmail() {
        return this.getText();
    }

    public void clear() {
        this.getTextField().setText("");
    }

    private class EmailKeyListener extends KeyAdapter {

        @Override
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            String sc = Character.toString(c);
            if (!sc.matches("[0-9a-zA-Z@_.]")
                    || getEmail().length() >= 40) {
                e.consume();
            }
        }

    }

    private class EmailCaretListener implements CaretListener {

        @Override
        public void caretUpdate(CaretEvent e) {
            if (!getEmail().matches("[0-9a-zA-Z_.]+@[0-9a-zA-Z_.]+")) {
                emailError = "Email is not valid";
            } else {
                emailError = "";
            }
        }

    }

}
