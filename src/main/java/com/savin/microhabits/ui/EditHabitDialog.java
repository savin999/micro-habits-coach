package com.savin.microhabits.ui;

import com.savin.microhabits.model.Habit;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Dialog for editing an existing habit.
 */
public class EditHabitDialog extends Dialog<EditHabitDialog.Result> {

    public record Result(String name, String description) {}

    public EditHabitDialog(Habit habit) {
        setTitle("Edit Habit");
        setHeaderText("Update your habit details");

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        TextField nameField = new TextField(habit.getName());
        TextArea descField = new TextArea(habit.getDescription());
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

        // Disable Save if the name is empty
        Button okButton = (Button) getDialogPane().lookupButton(saveBtn);
        okButton.setDisable(nameField.getText().trim().isEmpty());

        nameField.textProperty().addListener((obs, oldV, newV) ->
                okButton.setDisable(newV == null || newV.trim().isEmpty())
        );

        setResultConverter(btn -> {
            if (btn == saveBtn) {
                return new Result(
                        nameField.getText().trim(),
                        descField.getText().trim()
                );
            }
            return null;
        });
    }
}
