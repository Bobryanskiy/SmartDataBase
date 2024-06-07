package com.github.bobryanskiy.smartdb;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.ArrayList;

public class ConfigureScene {
    @FXML
    private GridPane gridPane;
    private CheckBox checkBox = new CheckBox();
    private Button deleteButton = new Button("-");
    private Button addButton = new Button("+");
    private int settingAmount = 0;
    public static final ArrayList<String> available = new ArrayList<>();
    public static final ArrayList<ComboBox<String>> comboBoxes = new ArrayList<>();
    private ArrayList<Control> advancedSearchControls = new ArrayList<>();
//    @FXML
//    private Button addNewSetting;

    @FXML
    private void initialize() {
        checkBox.setScaleX(1.4);
        checkBox.setScaleY(1.4);
        checkBox.setTranslateX(7);
        checkBox.autosize();
        gridPane.add(checkBox, 1, 0);

        deleteButton.setDisable(true);
        deleteButton.prefWidth(25);
        deleteButton.prefHeight(22);
        deleteButton.setMaxSize(25, 22);
        deleteButton.setMinSize(25, 22);
        deleteButton.setTranslateX(165);
        deleteButton.setTranslateY(-12);
        gridPane.add(deleteButton, 1, 0);

        addButton.prefWidth(25);
        addButton.prefHeight(22);
        addButton.setMaxSize(25, 22);
        addButton.setMinSize(25, 22);
        addButton.setTranslateX(165);
        addButton.setTranslateY(12 - 50);
        addButton.setDisable(false);
        addButton.setOnAction(actionEvent -> addNewSetting());
        gridPane.add(addButton, 1, 0);

        DatePicker datePicker = new DatePicker();
        datePicker.setTranslateX(29);
        datePicker.setPrefWidth(133);
        datePicker.setPrefHeight(46);
        datePicker.setEditable(false);
        gridPane.add(datePicker, 1, 0);

        if (!comboBoxes.isEmpty()) {
            for (ComboBox<String> comboBox : comboBoxes) {
                addOldRow(comboBox);
            }
        } else {
            available.addAll(Controller.columnsNames);
            addNewRow();
        }
    }

    @FXML
    protected void addNewSetting() {

        if (comboBoxes.get(settingAmount - 1).getValue() != null) {
//            if () {
//
//            }
            comboBoxes.get(settingAmount - 1).setDisable(true);
            available.remove(comboBoxes.get(settingAmount - 1).getValue());
            addNewRow();
        }
    }

    private void addOldRow(ComboBox<String> box) {
        gridPane.add(box, 0, settingAmount++);
//        gridPane.addRow(settingAmount++, box);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setMinHeight(50);
        rowConstraints.setPrefHeight(50);
        gridPane.getRowConstraints().add(rowConstraints);
    }

    private void addNewRow() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(FXCollections.observableArrayList(available));
        comboBox.setPrefWidth(193);
        comboBox.setPrefHeight(46);
        comboBox.translateXProperty().set(2);
        comboBoxes.add(comboBox);

        addButton.setTranslateY(addButton.getTranslateY() + 50);

//        gridPane.addRow(settingAmount++, comboBox);
        gridPane.add(comboBox, 0, settingAmount++);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setMinHeight(50);
        rowConstraints.setPrefHeight(50);
        gridPane.getRowConstraints().add(rowConstraints);
    }
}
