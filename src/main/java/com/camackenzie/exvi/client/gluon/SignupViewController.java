/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gluon;

import com.camackenzie.exvi.client.gluon.elements.PasswordInput;
import com.camackenzie.exvi.client.gluon.elements.TextFieldContentListener;
import com.camackenzie.exvi.client.gluon.elements.TextFieldLengthListener;
import com.camackenzie.exvi.client.gluon.elements.UsernameInput;
import com.camackenzie.exvi.client.model.UserAccount;
import com.camackenzie.exvi.core.api.APIResult;
import com.camackenzie.exvi.core.api.AccountAccessKeyResult;
import com.camackenzie.exvi.core.api.VerificationResult;
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
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author callum
 */
public class SignupViewController extends Controller {

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
        this.cacheFXML(Views.LOGIN, Views.HOME);

        toLoginPageButton.setOnAction(new ToLoginPageAction());
        sendCodeButton.setOnAction(new SendVerificationCodeAction());
        signupButton.setOnAction(new CreateAccountAction());

        codeInput.lengthProperty()
                .addListener(new TextFieldLengthListener(codeInput, 6));
        codeInput.textProperty()
                .addListener(new TextFieldContentListener(codeInput, "([0-9])*"));

        phoneInput.lengthProperty()
                .addListener(new TextFieldLengthListener(phoneInput, 20));
        phoneInput.textProperty()
                .addListener(new TextFieldContentListener(phoneInput, "\\+?([0-9])*"));

        emailInput.lengthProperty()
                .addListener(new TextFieldLengthListener(emailInput, 40));
        emailInput.textProperty()
                .addListener(new TextFieldContentListener(emailInput, "([0-9a-zA-Z]|[.\\-_])*@?([0-9a-zA-Z]|[.\\-_])*"));
    }

    private class ToLoginPageAction implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            if (signupFuture != null) {
                signupFuture.cancel(true);
            }
            setView(Views.LOGIN, (Node) e.getSource());
        }

    }

    private class SendVerificationCodeAction implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            if (usernameInput.getText().length() == 0) {
                errorText.setText("Please enter a username");
                errorText.setVisible(true);
            } else if (emailInput.getText().length() == 0) {
                errorText.setText("Please enter an email");
                errorText.setVisible(true);
            } else if (phoneInput.getText().length() == 0) {
                errorText.setText("Please enter a phone number");
                errorText.setVisible(true);
            } else {
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

    }

    private class CreateAccountAction implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            if (codeInput.getText().length() != 6) {
                errorText.setText("Verification code must be 6 numbers");
                errorText.setVisible(true);
            } else if (passwordInput.getText().length() < 8) {
                errorText.setText("Password must be at least 8 characters");
                errorText.setVisible(true);
            } else if (usernameInput.getText().length() == 0) {
                errorText.setText("Please enter a username");
                errorText.setVisible(true);
            } else {
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
                                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                                UserAccount user = UserAccount.fromAccessKey(usernameInput.getText(),
                                        result.getBody().getAccessKey());
                                ((BackendModel) stage.getUserData())
                                        .getUserManager()
                                        .setActiveUser(user);

                                setView(Views.HOME, stage);
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

}
