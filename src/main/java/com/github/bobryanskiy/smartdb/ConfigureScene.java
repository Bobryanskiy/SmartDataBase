package com.github.bobryanskiy.smartdb;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.sql.SQLException;
import java.util.ArrayList;

public class ConfigureScene {
    @FXML
    private GridPane gridPane;
    private final Button deleteButton = new Button("delete");
    private final Button addButton = new Button("add/save");
    private int settingAmount = 0;
    public static final ArrayList<String> available = new ArrayList<>();
    public static final ArrayList<Node[]> comboBoxes = new ArrayList<>();
    private final ContextMenu menuAdd = new ContextMenu();
    public static StringBuilder finalString;

    @FXML
    private void initialize() {
        finalString = new StringBuilder();
        buildAddButtonFunctional();
        createDeleteButton();
        createAddButton();
        buildConfigurationScene();
    }

    private void buildAddButtonFunctional() {
        addFirstMenuItem();
        addSecondMenuItem();
        addThirdMenuItem();
        addButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            menuAdd.setX(mouseEvent.getScreenX());
            menuAdd.setY(mouseEvent.getScreenY());
            menuAdd.show(((Node)mouseEvent.getSource()).getScene().getWindow());
        });
    }

    private void buildConfigurationScene() {
        if (!comboBoxes.isEmpty()) {
            for (Node[] comboBox : comboBoxes) {
                addOldRow(comboBox);
            }
            addButton.setTranslateY(50 * comboBoxes.size() + 12);
            deleteButton.setTranslateY(50 * comboBoxes.size() - 12);
        } else if (available.isEmpty()){
            available.addAll(Controller.columns.keySet());
        }
        deleteButton.setDisable(settingAmount == 0);
    }

    private void createAddButton() {
        addButton.prefWidth(70);
        addButton.prefHeight(22);
        addButton.setMaxSize(70, 22);
        addButton.setMinSize(70, 22);
        addButton.setTranslateX(200);
        addButton.setTranslateY(12);
        addButton.setDisable(false);
        gridPane.add(addButton, 0, 0);
    }

    private void createDeleteButton() {
        deleteButton.prefWidth(70);
        deleteButton.prefHeight(22);
        deleteButton.setMaxSize(70, 22);
        deleteButton.setMinSize(70, 22);
        deleteButton.setTranslateX(200);
        deleteButton.setTranslateY(-12);
        deleteButton.setOnAction(actionEvent -> deleteSetting());
        gridPane.add(deleteButton, 0, 0);
    }

    private void addThirdMenuItem() {
        MenuItem m3 = new MenuItem("Add a static one from column");
        m3.setOnAction(m -> {
            ComboBox<String> comboBox = new ComboBox<>();
            comboBox.setItems(FXCollections.observableArrayList(available));
            comboBox.setPrefWidth(90);
            comboBox.setPrefHeight(46);
            comboBox.translateXProperty().set(2);

            ComboBox<String> comboBox2 = new ComboBox<>();
            comboBox2.setPrefWidth(92);
            comboBox2.setPrefHeight(46);
            comboBox2.translateXProperty().set(103);
            comboBox2.setDisable(true);
            comboBox.setOnAction(s -> {
                comboBox2.setDisable(false);
                comboBox2.setOnAction(k -> addButton.setDisable(false));
                try {
                    comboBox2.setItems(FXCollections.observableArrayList(DataBase.getAvailableFromColumn(Controller.tables.get(0), comboBox.getValue())));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            addNewSetting(new Node[]{comboBox, comboBox2});
        });
        menuAdd.getItems().add(m3);
    }

    private void addSecondMenuItem() {
        MenuItem m2 = new MenuItem("Add any from column");
        m2.setOnAction(m -> {
            ComboBox<String> comboBox = new ComboBox<>();
            comboBox.setItems(FXCollections.observableArrayList(available));
            comboBox.setPrefWidth(193);
            comboBox.setPrefHeight(46);
            comboBox.translateXProperty().set(2);
            addNewSetting(new Node[]{comboBox});
        });
        menuAdd.getItems().add(m2);
    }

    private void addFirstMenuItem() {
        MenuItem m1 = new MenuItem("Add custom word");
        m1.setOnAction(m -> {
            TextField textField = new TextField();
            textField.prefWidth(193);
            textField.prefHeight(46);
            textField.setMaxSize(193, 46);
            textField.setMinSize(193, 46);
            textField.translateXProperty().set(2);
            addNewSetting(new Node[]{textField});
        });
        menuAdd.getItems().add(m1);
    }

    @FXML
    protected void deleteSetting() {
        if (!comboBoxes.isEmpty()) {
            gridPane.getRowConstraints().remove(comboBoxes.size());
            gridPane.getChildren().remove(comboBoxes.get(comboBoxes.size() - 1)[0]);
            if (comboBoxes.get(comboBoxes.size() - 1).length == 2) gridPane.getChildren().remove(comboBoxes.get(comboBoxes.size() - 1)[1]);
            comboBoxes.remove(comboBoxes.get(comboBoxes.size() - 1));
            addButton.setTranslateY(addButton.getTranslateY() - 50);
            deleteButton.setTranslateY(deleteButton.getTranslateY() - 50);
            --settingAmount;
            if (settingAmount == 0) {
                deleteButton.setDisable(true);
            } else {
                comboBoxes.get(comboBoxes.size() - 1)[0].setDisable(false);
                if (comboBoxes.get(comboBoxes.size() - 1).length == 2) comboBoxes.get(comboBoxes.size() - 1)[1].setDisable(false);
                if (comboBoxes.get(comboBoxes.size() - 1)[0] instanceof ComboBox<?>) available.add((String) ((ComboBox<?>)comboBoxes.get(comboBoxes.size() - 1)[0]).getValue());
            }
            addButton.setDisable(false);
        }
    }

    @FXML
    protected void addNewSetting(Node[] node) {
        if (settingAmount == 0) {
            deleteButton.setDisable(false);
            addNewRow(node);
        }
        else {
            Node[] n = comboBoxes.get(settingAmount - 1);
            if (n.length == 1) {
                if (n[0] instanceof ComboBox<?> && ((ComboBox<?>)n[0]).getValue() != null) {
                    func1(node, n);
                }
                else if (n[0] instanceof TextField && !((TextField)n[0]).getText().isEmpty()) {
                    comboBoxes.get(settingAmount - 1)[0].setDisable(true);
                    deleteButton.setDisable(false);
                    addNewRow(node);
                }
            } else if (n.length == 2 && ((ComboBox<?>)n[1]).getValue() != null){
                comboBoxes.get(settingAmount - 1)[1].setDisable(true);
                func1(node, n);
            }
        }
    }

    private void func1(Node[] node, Node[] n) {
        comboBoxes.get(settingAmount - 1)[0].setDisable(true);
        available.remove(((ComboBox<String>)n[0]).getValue());
        if (node[0] instanceof ComboBox<?>) ((ComboBox<?>) node[0]).getItems().remove(((ComboBox<?>) n[0]).getValue());
        deleteButton.setDisable(false);
        addNewRow(node);
    }

    private void addNewRow(Node[] nodes) {
        comboBoxes.add(nodes);

        addButton.setTranslateY(addButton.getTranslateY() + 50);
        deleteButton.setTranslateY(deleteButton.getTranslateY() + 50);

        addOldRow(nodes);
    }

    private void addOldRow(Node[] nodes) {
        for (Node value : nodes) gridPane.add(value, 0, settingAmount);
        ++settingAmount;
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setMinHeight(50);
        rowConstraints.setPrefHeight(50);
        gridPane.getRowConstraints().add(rowConstraints);
    }
}
