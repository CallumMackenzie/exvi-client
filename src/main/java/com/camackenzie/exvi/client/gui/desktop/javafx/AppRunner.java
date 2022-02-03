/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.javafx;

import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author callum
 */
public class AppRunner extends Application {

    public static void main(String[] args) {
        Application.launch();
    }

    BackendModel model;
    ViewManager viewManager;

    @Override
    public void start(Stage stage) throws Exception {

        this.setupViewCaching();
        this.setupModel();
        this.setupStage(stage);
        this.setInitialView(stage);

        stage.show();
    }

    private void setupViewCaching() {
        this.viewManager = new ViewManager("login", "/fxml/LoginView.fxml",
                "home", "/fxml/HomepageView.fxml");
    }

    private void setupModel() {
        this.model = new BackendModel();
    }

    private void setupStage(Stage stage) {
        stage.setTitle("Exvi Fitness");
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/image/Logo.png")));
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        stage.setUserData(this.model);
    }

    private void setInitialView(Stage stage) throws IOException {
        Parent root;
        if (model.getUserManager().hasActiveUser()) {
            root = viewManager.getFXML("home");
        } else {
            root = viewManager.getFXML("login");
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

}
