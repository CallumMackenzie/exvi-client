/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gluon;

import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
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
        this.setupModel();
        this.setupStage(stage);
        this.setInitialView(stage);

        stage.show();
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
        viewManager = new ViewManager();
        Parent root;
        if (model.getUserManager().hasActiveUser()) {
            root = viewManager.getFXML(Views.HOME);
        } else {
            root = viewManager.getFXML(Views.LOGIN);
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

}
