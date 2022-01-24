/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.uielements;

import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author callum
 */
public class PromptedTextField<T extends JTextField> {

    private final T textField;
    private final TextPrompt textPrompt;

    public PromptedTextField(T textField, String prompt) {
        this.textField = textField;
        this.textPrompt = new TextPrompt(prompt, textField);
        this.textPrompt.changeAlpha(0.7f);
    }

    public TextPrompt getPrompt() {
        return this.textPrompt;
    }

    public T getTextField() {
        return this.textField;
    }

    public static PromptedTextField<JTextField> textField(String prompt) {
        return new PromptedTextField<>(new JTextField(), prompt);
    }

    public static PromptedTextField<JPasswordField> passwordField(String prompt) {
        return new PromptedTextField<>(new JPasswordField(), prompt);
    }

}
