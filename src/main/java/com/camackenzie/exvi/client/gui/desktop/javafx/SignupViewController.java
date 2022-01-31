/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.javafx;

import com.camackenzie.exvi.client.gui.desktop.javafx.elements.PasswordInput;
import com.camackenzie.exvi.client.gui.desktop.javafx.elements.UsernameInput;
import com.camackenzie.exvi.client.model.UserAccount;
import com.camackenzie.exvi.core.api.APIResult;
import com.camackenzie.exvi.core.api.AccountAccessKeyResult;
import com.camackenzie.exvi.core.api.VerificationResult;
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
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author callum
 */
public class SignupViewController implements Initializable {

    @FXML
    Button toLoginPageButton;
    @FXML
    Button sendCodeButton;
    @FXML
    Button signupButton;
    @FXML
    PasswordInput passwordInput;
    @FXML
    UsernameInput usernameInput;
    @FXML
    TextField emailInput;
    @FXML
    TextField phoneInput;
    @FXML
    TextField codeInput;
    @FXML
    Text errorText;

    RunnableFuture signupFuture;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        toLoginPageButton.setOnAction(new ToLoginPageAction());
        sendCodeButton.setOnAction(new SendVerificationCodeAction());
        signupButton.setOnAction(new CreateAccountAction());
    }

    private class ToLoginPageAction implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            if (signupFuture != null) {
                signupFuture.cancel(true);
            }
            try {
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/LoginView.fxml"));
                stage.getScene().setRoot(root);
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }

    }

    private class SendVerificationCodeAction implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            sendCodeButton.setDisable(true);
            signupButton.setDisable(true);
            emailInput.setDisable(true);
            usernameInput.setDisable(true);
            phoneInput.setDisable(true);

            signupFuture = new RunnableFuture(() -> {
                try {
                    APIResult<VerificationResult> result
                            = UserAccount.requestVerification(usernameInput.getText(),
                                    emailInput.getText(),
                                    phoneInput.getText()).get();

                    if (result.failed()
                            || result.getBody().errorOccured()) {
                        Platform.runLater(() -> {
                            errorText.setText(result.getBody() == null
                                    ? ("Status code " + result.getStatusCode())
                                    : result.getBody().getMessage());
                            errorText.setVisible(true);
                        });
                    } else {
                        Platform.runLater(() -> {
                            errorText.setVisible(false);
                            sendCodeButton.setText("Resend Verification Code");
                        });
                    }
                } catch (InterruptedException ex) {
                    System.out.println("Verification interrupted: " + ex);
                } catch (ExecutionException ex) {
                    System.err.println(ex);
                } finally {
                    Platform.runLater(() -> {
                        sendCodeButton.setDisable(false);
                        signupButton.setDisable(false);
                        emailInput.setDisable(false);
                        usernameInput.setDisable(false);
                        phoneInput.setDisable(false);
                    });
                }
            });
            signupFuture.start();
        }

    }

    private class CreateAccountAction implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            sendCodeButton.setDisable(true);
            signupButton.setDisable(true);
            emailInput.setDisable(true);
            phoneInput.setDisable(true);
            usernameInput.setDisable(true);
            codeInput.setDisable(true);
            passwordInput.setDisable(true);

            signupFuture = new RunnableFuture(() -> {
                try {
                    APIResult<AccountAccessKeyResult> result
                            = UserAccount.requestSignUp(usernameInput.getText(),
                                    codeInput.getText(),
                                    passwordInput.getText()).get();
                    if (result.failed()
                            || result.getBody().errorOccured()) {
                        Platform.runLater(() -> {
                            errorText.setText(result.getBody() == null
                                    ? ("Status code " + result.getStatusCode())
                                    : result.getBody().getMessage());
                            errorText.setVisible(true);
                        });
                    } else {
                        Platform.runLater(() -> {
                            // Switch to homepage view
                        });
                    }
                } catch (InterruptedException ex) {
                    System.out.println("Create account action interrupted: " + ex);
                } catch (ExecutionException ex) {
                    System.err.println(ex);
                } finally {
                    Platform.runLater(() -> {
                        sendCodeButton.setDisable(false);
                        signupButton.setDisable(false);
                        emailInput.setDisable(false);
                        phoneInput.setDisable(false);
                        usernameInput.setDisable(false);
                        codeInput.setDisable(false);
                        passwordInput.setDisable(false);
                    });
                }
            });
            signupFuture.start();
        }

    }

}
