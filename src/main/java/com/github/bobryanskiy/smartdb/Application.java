package com.github.bobryanskiy.smartdb;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("screen-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void showConfigureStage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("configure-view.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Configure");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage);
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        stage.setScene(scene);
        stage.showAndWait();
        if (!ConfigureScene.comboBoxes.get(ConfigureScene.comboBoxes.size() - 1).isDisabled()) ConfigureScene.comboBoxes.get(ConfigureScene.comboBoxes.size() - 1).setValue(null);
    }

    public static void main(String[] args) {
        launch();
    }
}