/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.javafx;

import com.camackenzie.exvi.client.model.UserAccount;
import com.camackenzie.exvi.client.gui.desktop.javafx.elements.PasswordInput;
import com.camackenzie.exvi.client.gui.desktop.javafx.elements.UsernameInput;
import com.camackenzie.exvi.core.api.APIResult;
import com.camackenzie.exvi.core.api.AccountAccessKeyResult;
import com.camackenzie.exvi.core.async.RunnableFuture;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author callum
 */
public class LoginViewController implements Initializable {

    @FXML
    Button loginButton;
    @FXML
    Button signupButton;
    @FXML
    UsernameInput usernameField;
    @FXML
    PasswordInput passwordField;
    @FXML
    Label errorText;

    RunnableFuture loginFuture;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loginButton.setOnAction(new LoginAction());
        signupButton.setOnAction(new SignupAction());
    }

    private class LoginAction implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            if (usernameField.getText().isBlank()) {
                errorText.setText("Please enter a username");
                errorText.setVisible(true);
            } else if (passwordField.getText().isBlank()) {
                errorText.setText("Please enter a password");
                errorText.setVisible(true);
            } else if (passwordField.getText().length() < 8) {
                errorText.setText("Your password must be longer than 8 characters");
                errorText.setVisible(true);
            } else {
                loginButton.setDisable(true);
                usernameField.setDisable(true);
                passwordField.setDisable(true);
                errorText.setVisible(false);

                loginFuture = new RunnableFuture(() -> {
                    try {
                        APIResult<AccountAccessKeyResult> result
                                = UserAccount.requestLogin(usernameField.getText(),
                                        passwordField.getText()).get();
                        if (result.failed()
                                || result.getBody().errorOccured()) {
                            Platform.runLater(() -> {
                                errorText.setText(result.getBody().getMessage());
                                errorText.setVisible(true);
                            });
                        } else {
                            Platform.runLater(() -> {
                                try {
                                    Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                                    UserAccount user = UserAccount.fromAccessKey(usernameField.getText(),
                                            result.getBody().getAccessKey());
                                    ((BackendModel) stage.getUserData()).getUserManager()
                                            .setActiveUser(user);
                                    Parent homepage = FXMLLoader.load(getClass().getResource("/fxml/HomepageView.fxml"));
                                    stage.getScene().setRoot(homepage);
                                } catch (IOException ex) {
                                    System.err.println(ex);
                                }
                            });
                        }
                    } catch (InterruptedException ex) {
                        System.out.println("Login action interrupted: " + ex.getMessage());
                    } catch (ExecutionException ex) {
                        System.err.println("Login action failed: " + ex);
                    } finally {
                        Platform.runLater(() -> {
                            loginButton.setDisable(false);
                            usernameField.setDisable(false);
                            passwordField.setDisable(false);
                        });
                    }
                });
                loginFuture.start();
            }
        }

    }

    private class SignupAction implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            try {
                if (loginFuture != null) {
                    loginFuture.cancel(true);
                }
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/SignupView.fxml"));
                stage.getScene().setRoot(root);
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }

    }

}