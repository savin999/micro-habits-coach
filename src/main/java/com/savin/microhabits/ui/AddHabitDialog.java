package com.savin.microhabits.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class AddHabitDialog extends Dialog<AddHabitDialog.Result> {

    public record Result(String name, String description) {}

    public AddHabitDialog() {
        setTitle("Add Habit");
        setHeaderText("Create a new habit");

        ButtonType createBtn = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(createBtn, ButtonType.CANCEL);

        TextField nameField = new TextField();
        nameField.setPromptText("e.g., Drink water");

        TextArea descField = new TextArea();
        descField.setPromptText("Optional description...");
        descField.setPrefRowCount(3);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(12));

        grid.add(new Label("Name"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description"), 0, 1);
        grid.add(descField, 1, 1);

        getDialogPane().setContent(grid);

        // Disable Create button until name is valid
        var okButton = getDialogPane().lookupButton(createBtn);
        okButton.setDisable(true);

        nameField.textProperty().addListener((obs, oldV, newV) -> {
            okButton.setDisable(newV == null || newV.trim().isEmpty());
        });

        setResultConverter(btn -> {
            if (btn == createBtn) {
                return new Result(nameField.getText().trim(), descField.getText().trim());
            }
            return null;
        });
    }

    public Optional<Result> showAndWaitResult() {
        return showAndWait();
    }
}
