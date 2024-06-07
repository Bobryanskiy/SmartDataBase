package com.github.bobryanskiy.smartdb;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class Controller {

    ObservableList<String> placeSearchChoices = FXCollections.observableArrayList("В начале", "В конце", "Где-то в тексте");

    @FXML
    private Label welcomeText;
    @FXML
    private TextField loginText;
    @FXML
    private TextField passwordText;
    @FXML
    private Button loginButton;
    @FXML
    private Button logoutButton;
    @FXML
    private ComboBox<String> placeToSearch;
    @FXML
    private Button configure;
    @FXML
    private TableView<String[]> tableView;
//    private final ArrayList<TableColumn<String[], String>> columnsTable = new ArrayList<>();
    private ArrayList<String> tables;
    public static ArrayList<String> columnsNames;
    public static String[] columnsTypes;

    @FXML
    public void initialize() {
        placeToSearch.setItems(placeSearchChoices);
    }

    @FXML
    protected void onLogInButtonClick() {
        try {
            DataBase.connect(loginText.getText(), passwordText.getText());
            loginText.clear();
            loginText.setDisable(true);
            passwordText.clear();
            passwordText.setDisable(true);
            loginButton.setDisable(true);
            logoutButton.setDisable(false);
            welcomeText.setText("Успешно!");

            tables = DataBase.getTables();
            Object[] temp = DataBase.getColumns(tables.get(0));
            columnsNames = (ArrayList<String>) temp[0];
            columnsTypes = (String[]) temp[1];
            ObservableList<String[]> data = FXCollections.observableArrayList();
            data.addAll(DataBase.getSomeData(tables.get(0), columnsNames.size()));
            for (int i = 0; i < columnsNames.size(); i++) {
                TableColumn<String[], String> tc = new TableColumn<>(columnsNames.get(i));
                final int colNo = i;
                tc.setCellValueFactory(p -> new SimpleStringProperty((p.getValue()[colNo])));
//                tc.setPrefWidth(90);
                tableView.getColumns().add(tc);
            }
            tableView.setItems(data);
            tableView.getColumns().forEach(k -> System.out.println(k.getText()));
        } catch (SQLException e) {
            welcomeText.setText(e.getMessage());
        }
    }

    @FXML
    protected void onLogOutButtonClick() throws SQLException {
        loginText.setDisable(false);
        passwordText.setDisable(false);
        loginButton.setDisable(false);
        logoutButton.setDisable(true);
        tableView.getItems().clear();
        tableView.getColumns().clear();
        DataBase.close();
        ConfigureScene.available.clear();
        ConfigureScene.comboBoxes.clear();
    }

    @FXML
    protected void onConfigureButtonClick() throws IOException {
        Application.showConfigureStage();
    }
}