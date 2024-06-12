package com.github.bobryanskiy.smartdb;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("screen-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
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
        stage.showAndWait();;
        if (!ConfigureScene.comboBoxes.isEmpty()) {
            Node node = ConfigureScene.comboBoxes.get(ConfigureScene.comboBoxes.size() - 1)[0];
            if (node instanceof ComboBox<?> && !node.isDisabled()) {
                ((ComboBox<?>) ConfigureScene.comboBoxes.get(ConfigureScene.comboBoxes.size() - 1)[0]).setValue(null);
            }
            else if (node instanceof TextField) ((TextField)node).setText("");
            ConfigureScene.comboBoxes.forEach(n -> {
                if (n[0] instanceof TextField && ((TextField) n[0]).getText() != null)
                    ConfigureScene.finalString.append(((TextField)n[0]).getText());
                else if (n[0] instanceof ComboBox<?> && ((ComboBox<?>) n[0]).getValue() != null) {
                    ConfigureScene.finalString.append("[").append(((ComboBox<?>)n[0]).getValue()).append("]");
                    if (n.length == 2) {
                        ConfigureScene.finalString.append("=").append(((ComboBox<?>) n[1]).getValue());
                    }
                }
                ConfigureScene.finalString.append(" ");
            });
            System.out.println(ConfigureScene.finalString.toString().strip());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}