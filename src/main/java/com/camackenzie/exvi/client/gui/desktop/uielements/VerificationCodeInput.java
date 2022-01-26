/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.uielements;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPasswordField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 *
 * @author callum
 */
public class VerificationCodeInput extends PromptedTextField {

    private String codeError = "Please enter a verification code";

    public VerificationCodeInput() {
        super(new JPasswordField(), "Code");
        this.getTextField().addCaretListener(new VerificationCodeCaretListener());
        this.getTextField().addKeyListener(new VerificationCodeKeyListener());
    }

    public boolean isCodeValid() {
        return this.codeError.equals("");
    }

    public String getCodeError() {
        return this.codeError;
    }
    
    public String getCode() {
        return this.getText();
    }
    
    public void clear() {
        this.getTextField().setText("");
    }

    private class VerificationCodeKeyListener extends KeyAdapter {

        @Override
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            if (!Character.toString(c).matches("[0-9]")
                    || getText().length() >= 6) {
                e.consume();
            }
        }

    }

    private class VerificationCodeCaretListener implements CaretListener {

        @Override
        public void caretUpdate(CaretEvent e) {
            String code = getText();
            if (code.length() != 6 || !code.matches("[0-9]+")) {
                codeError = "Verification code must be 6 digits";
            } else {
                codeError = "";
            }
        }

    }

}
