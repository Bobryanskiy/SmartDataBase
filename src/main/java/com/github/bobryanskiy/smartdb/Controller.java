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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

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
    public static LinkedHashMap<String, String> columns;

    private File folderPath;

    public static boolean containsDependenciesFromDb;

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
//            System.out.println(FileRenamer.getFilenameFromMask(maskTextField.getText(), columns.keySet()));
            if (placeToSearch.getValue() == null) startErrorLabel.setText("Выберите где искать в файле");
            else if (isMaskContainsBadSymbols()) startErrorLabel.setText("Маска не должна содержать\n запрещённых символов: \\/:*?\"<>|");
            else if (folderPath == null) startErrorLabel.setText("Выберите папку с файлами");
            else if (ConfigureScene.finalString == null || ConfigureScene.finalString.isEmpty()) startErrorLabel.setText("Настройте конфигурацию поиска");
            else if (maskTextField.getText().isEmpty()) startErrorLabel.setText("Настройте маску файла");
            else {
                try {
                    startErrorLabel.setText(FileRenamer.fileRenamer(folderPath.getPath(), requestParser(), maskTextField.getText()));
                } catch (FileNotFoundException | SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private boolean isMaskContainsBadSymbols() {
        return Pattern.compile("[\\\\/:*?\"<>|]").matcher(maskTextField.getText()).find();
    }

    enum RequestType{Column, ColumnWithSpecification, Word}

    public record Request(RequestType type, String column, String columnType, String word){}

    private Request[] requestParser() {
        containsDependenciesFromDb = false;
        Request[] requestPull = new Request[ConfigureScene.comboBoxes.size() - 1];
        for (int i = 0; i < ConfigureScene.comboBoxes.size() - 1; ++i) {
            if (ConfigureScene.comboBoxes.get(i)[0] instanceof TextField && ((TextField) ConfigureScene.comboBoxes.get(i)[0]).getText() != null) {
                requestPull[i] = new Request(RequestType.Word, null, null, ((TextField) ConfigureScene.comboBoxes.get(i)[0]).getText());
            }
            else if (ConfigureScene.comboBoxes.get(i)[0] instanceof ComboBox<?> && ((ComboBox<?>) ConfigureScene.comboBoxes.get(i)[0]).getValue() != null) {
                containsDependenciesFromDb = true;
                if (ConfigureScene.comboBoxes.get(i).length == 2) {
                    requestPull[i] = new Request(RequestType.ColumnWithSpecification, (String) ((ComboBox<?>) ConfigureScene.comboBoxes.get(i)[0]).getValue(), columns.get((String) ((ComboBox<?>) ConfigureScene.comboBoxes.get(i)[0]).getValue()), (String) ((ComboBox<?>)ConfigureScene.comboBoxes.get(i)[1]).getValue());
                } else {
                    requestPull[i] = new Request(RequestType.Column, (String) ((ComboBox<?>) ConfigureScene.comboBoxes.get(i)[0]).getValue(), columns.get((String) ((ComboBox<?>) ConfigureScene.comboBoxes.get(i)[0]).getValue()), null);
                }
            }
        }
        return requestPull;
    }

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
            columns = DataBase.getColumns(tables.get(0));
            ObservableList<String[]> columnsNamesInfo = FXCollections.observableArrayList();
            columnsNamesInfo.addAll(DataBase.getSomeData(tables.get(0), columns.size()));
            for (int i = 0; i < columns.size(); i++) {
                TableColumn<String[], String> tc = new TableColumn<>((String) columns.keySet().toArray()[i]);
                final int colNo = i;
                tc.setCellValueFactory(p -> new SimpleStringProperty((p.getValue()[colNo])));
//                tc.setPrefWidth(100);
                tableView.getColumns().add(tc);
            }
            tableView.setItems(columnsNamesInfo);
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

