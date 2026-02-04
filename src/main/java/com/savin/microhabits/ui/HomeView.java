package com.savin.microhabits.ui;

import com.savin.microhabits.model.Habit;
import com.savin.microhabits.service.HabitService;
import com.savin.microhabits.storage.FileStorage;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Text;

import java.util.List;

/**
 * Main UI view for managing habits.
 */
public class HomeView {

    private final HabitService habitService;
    private final FileStorage storage;

    private final VBox cardsBox = new VBox(12);
    private final Label emptyLabel = new Label("No habits yet. Click Add Habit to start");

    private final StackPane rootStack = new StackPane();
    private final VBox overlay = new VBox(10);
    private final ProgressIndicator spinner = new ProgressIndicator();
    private final Label overlayText = new Label("Working...");

    public HomeView(HabitService habitService, FileStorage storage) {
        this.habitService = habitService;
        this.storage = storage;
        buildOverlay();
    }

    public Parent create() {
        Label title = new Label("Micro-Habits Coach");
        title.getStyleClass().add("title");

        Button addBtn = new Button("+ Add Habit");
        addBtn.getStyleClass().add("primary-btn");

        Button saveBtn = new Button("Save");
        saveBtn.getStyleClass().add("ghost-btn");

        Button loadBtn = new Button("Load");
        loadBtn.getStyleClass().add("ghost-btn");

        HBox header = new HBox(10, title, new Region(), loadBtn, saveBtn, addBtn);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("header");

        cardsBox.getStyleClass().add("cards");
        emptyLabel.getStyleClass().add("muted");

        ScrollPane scroll = new ScrollPane(cardsBox);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll");

        VBox content = new VBox(14, header, scroll);
        content.setPadding(new Insets(18));
        content.getStyleClass().add("root");
        content.setBackground(createGradientBackground());

        rootStack.getChildren().setAll(content, overlay);
        StackPane.setAlignment(overlay, Pos.CENTER);

        addBtn.setOnAction(e -> {
            AddHabitDialog dialog = new AddHabitDialog();
            dialog.showAndWait().ifPresent(result -> {
                habitService.addHabit(result.name(), result.description());
                refreshCards();
            });
        });

        saveBtn.setOnAction(e -> runInBackground(
                "Saving...",
                () -> {
                    storage.save(habitService.getHabitsReadOnly());
                    return null;
                },
                () -> showInfo("Saved", "Your habits were saved successfully.")
        ));

        loadBtn.setOnAction(e -> runInBackground(
                "Loading...",
                () -> {
                    List<Habit> loaded = storage.load();
                    habitService.replaceAll(loaded);
                    return null;
                },
                this::refreshCards
        ));

        refreshCards();
        return rootStack;
    }

    private Background createGradientBackground() {
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#667eea")),
                new Stop(0.5, Color.web("#764ba2")),
                new Stop(1, Color.web("#f093fb"))
        );
        return new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY));
    }

    private void refreshCards() {
        cardsBox.getChildren().clear();

        if (habitService.getHabitsReadOnly().isEmpty()) {
            cardsBox.getChildren().add(emptyLabel);
            return;
        }

        for (Habit habit : habitService.getHabitsReadOnly()) {
            cardsBox.getChildren().add(createHabitCard(habit));
        }
    }

    private Parent createHabitCard(Habit habit) {
        Label name = new Label(habit.getName());
        name.getStyleClass().add("card-title");

        Text desc = new Text(habit.getDescription().isBlank() ? "No description" : habit.getDescription());
        desc.getStyleClass().add("card-desc");
        desc.wrappingWidthProperty().set(420);

        Label streak = new Label("Streak: " + habit.getStreakToday());
        streak.getStyleClass().add("streak");

        Button doneBtn = new Button("Mark Done Today");
        doneBtn.getStyleClass().add("secondary-btn");

        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("ghost-btn");

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("danger-btn");

        doneBtn.setOnAction(e -> {
            habitService.markToday(habit.getId(), true);
            refreshCards();
        });

        editBtn.setOnAction(e -> {
            EditHabitDialog dialog = new EditHabitDialog(habit);
            dialog.showAndWait().ifPresent(result -> {
                try {
                    habitService.updateHabit(habit.getId(), result.name(), result.description());
                    refreshCards();
                } catch (IllegalArgumentException ex) {
                    showError("Invalid input", ex.getMessage());
                }
            });
        });

        deleteBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Delete Habit");
            confirm.setHeaderText("Delete this habit?");
            confirm.setContentText(habit.getName());

            confirm.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.OK) {
                    habitService.deleteHabit(habit.getId());
                    refreshCards();
                }
            });
        });

        HBox actions = new HBox(8, editBtn, deleteBtn, doneBtn);
        actions.setAlignment(Pos.CENTER_RIGHT);

        HBox bottomRow = new HBox(12, streak, new Region(), actions);
        HBox.setHgrow(bottomRow.getChildren().get(1), Priority.ALWAYS);
        bottomRow.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(8, name, desc, bottomRow);
        card.getStyleClass().add("card");

        return card;
    }

    private void runInBackground(String message, BackgroundWork work, Runnable onSuccessUi) {
        overlayText.setText(message);
        setOverlayVisible(true);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(600); // makes the overlay visible for demos/screenshots
                work.run();
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            setOverlayVisible(false);
            onSuccessUi.run();
        });

        task.setOnFailed(e -> {
            setOverlayVisible(false);
            Throwable ex = task.getException();
            showError("Operation failed", ex == null ? "Unknown error." : ex.getMessage());
        });

        Thread t = new Thread(task, "microhabits-bg-task");
        t.setDaemon(true);
        t.start();
    }

    @FunctionalInterface
    private interface BackgroundWork {
        Void run() throws Exception;
    }

    private void buildOverlay() {
        overlay.getStyleClass().add("overlay");
        overlay.setAlignment(Pos.CENTER);
        overlay.setPadding(new Insets(18));
        overlay.getChildren().addAll(spinner, overlayText);
        overlay.setVisible(false);
        overlay.setManaged(false);
    }

    private void setOverlayVisible(boolean visible) {
        overlay.setVisible(visible);
        overlay.setManaged(visible);
    }

    private void showError(String title, String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(title);
        a.setContentText(message);
        a.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(title);
        a.setContentText(message);
        a.showAndWait();
    }

    public void loadOnStartup() {
        runInBackground(
                "Loading...",
                () -> {
                    List<Habit> loaded = storage.load();
                    habitService.replaceAll(loaded);
                    return null;
                },
                this::refreshCards
        );
    }

    public void saveOnExit() {
        try {
            storage.save(habitService.getHabitsReadOnly());
        } catch (Exception ex) {
            System.err.println("Auto-save failed: " + ex.getMessage());
        }
    }
}
