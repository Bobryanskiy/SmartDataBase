package com.github.bobryanskiy.smartdb;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class Controller extends Control {

    ObservableList<String> placeSearchChoices = FXCollections.observableArrayList("В начале", "В конце", "Где-то в тексте");

    @FXML
    private ImageView helpImage;
    @FXML
    private Label welcomeText;
    @FXML
    private TextField loginText;
    @FXML
    private TextField passwordText;
    @FXML
    private TextField maskTextField;
    @FXML
    private Button loginButton;
    @FXML
    private Button logoutButton;
    @FXML
    private ComboBox<String> placeToSearch;
    @FXML
    private Button chooseFolder;
    @FXML
    private Tooltip newFileMaskToolTip;
    @FXML
    private Button startSearchButton;
    @FXML
    private Label startErrorLabel;
    @FXML
    private Label chooseFolderLabel;
    @FXML
    private TableView<String[]> tableView;
    public static ArrayList<String> tables;
    public static ArrayList<String> columnsNames;
    public static String[] columnsTypes;

    private File folderPath;

    @FXML
    public void initialize() {
        placeToSearch.setItems(placeSearchChoices);
        setToolTips();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("src"));
        chooseFolder.setOnAction(e -> {
            folderPath = directoryChooser.showDialog(((Node)e.getSource()).getScene().getWindow());
            if (folderPath != null) chooseFolderLabel.setText(folderPath.toString());
        });
        startSearchButton.setOnAction(a -> {
            if (placeToSearch.getValue() == null) startErrorLabel.setText("Выберите где искать в файле");
            else if (folderPath == null) startErrorLabel.setText("Выберите папку с файлами");
            else if (ConfigureScene.finalString == null || ConfigureScene.finalString.isEmpty()) startErrorLabel.setText("Настройте конфигурацию поиска");
            else if (maskTextField.getText().isEmpty()) startErrorLabel.setText("Настройте маску файла");
            else {
                startErrorLabel.setText("Успешно");
                search();
            }
        });
    }

    private void search() {}

    private void setToolTips() {
        newFileMaskToolTip.setText("""
                [filename] - текущее найденное имя
                [tablename], где tablename - имя столбца из таблицы справа.
                Вставляет значение из той же строки.
                Например, имя файла было something.txt
                Написав [id]_[filename].txt мы к каждому найденному файлу добавим значение id
                из подходящей строчки, описанной в конфигурации выше.
                """);
        newFileMaskToolTip.setShowDelay(newFileMaskToolTip.getShowDelay().divide(10));
        newFileMaskToolTip.setFont(Font.font(12));
        Tooltip tooltip = new Tooltip("""
                У вас есть 3 варианта:
                    добавить для поиска любое слово
                    добавить имя столбца
                    добавить фиксированный параметр столбца
                Поиск будет осуществляться в порядке добавления, между словами всегда ставится пробел.
                Сохраняются только те, который больше нельзя редактировать.
                То есть для сохранения последнего надо добавить любое пустое поле.
                """);
        tooltip.setFont(Font.font(12));
        tooltip.setShowDelay(tooltip.getShowDelay().divide(10));
        Tooltip.install(helpImage, tooltip);
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
//                tc.setPrefWidth(100);
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
        if (DataBase.connection != null) Application.showConfigureStage();
    }
}

