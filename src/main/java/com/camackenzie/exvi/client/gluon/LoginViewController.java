/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gluon;

import com.camackenzie.exvi.client.model.UserAccount;
import com.camackenzie.exvi.client.gluon.elements.PasswordInput;
import com.camackenzie.exvi.client.gluon.elements.UsernameInput;
import com.camackenzie.exvi.core.api.APIResult;
import com.camackenzie.exvi.core.api.AccountAccessKeyResult;
import com.camackenzie.exvi.core.async.RunnableFuture;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author callum
 */
public class LoginViewController extends Controller {

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
        // Cache FXML results for faster load times
        this.cacheFXML(Views.SIGNUP, Views.HOME);

        // Setup controllers
        loginButton.setOnAction(new LoginAction());
        signupButton.setOnAction(new SignupAction());
    }

    private class LoginAction implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            // Ensure input fields have valid input
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
                // Disable controls
                loginButton.setDisable(true);
                usernameField.setDisable(true);
                passwordField.setDisable(true);
                errorText.setVisible(false);

                // Send login request async
                loginFuture = new RunnableFuture(() -> {
                    try {
                        // Send request
                        APIResult<AccountAccessKeyResult> result
                                = UserAccount.requestLogin(usernameField.getText(),
                                        passwordField.getText()).get();
                        // Check request status
                        if (result.failed()
                                || result.getBody().errorOccured()
                                || result.getBody().getAccessKey().isBlank()) {
                            // Update UI to show error
                            Platform.runLater(() -> {
                                errorText.setText(result.getBody().getMessage());
                                errorText.setVisible(true);
                            });
                        } else {
                            Platform.runLater(() -> {
                                // Update model with user credentials
                                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                                UserAccount user = UserAccount.fromAccessKey(usernameField.getText(),
                                        result.getBody().getAccessKey());
                                BackendModel model = (BackendModel) stage.getUserData();
                                model.getUserManager().setActiveUser(user);
                                // Switch views
                                try {
                                    setView(Views.HOME, stage);
                                } catch (Exception exc) {
                                    exc.printStackTrace(System.err);
                                    model.getUserManager().signOutActiveUser();
                                }
                            });
                        }
                    } catch (InterruptedException ex) {
                        System.out.println("Login action interrupted: " + ex);
                    } catch (ExecutionException ex) {
                        System.err.println("Login action failed: " + ex);
                    } finally {
                        // Reenable controls
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
            // Cancel login if it is being requested
            if (loginFuture != null) {
                loginFuture.cancel(true);
            }
            // Switch views
            setView(Views.SIGNUP, (Node) e.getSource());
        }

    }

}
