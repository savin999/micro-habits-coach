package com.savin.microhabits;

import com.savin.microhabits.service.HabitService;
import com.savin.microhabits.storage.FileStorage;
import com.savin.microhabits.ui.HomeView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Path;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        HabitService habitService = new HabitService();

        Path savePath = Path.of(System.getProperty("user.home"),
                ".microhabits-coach", "habits.txt");
        FileStorage storage = new FileStorage(savePath);

        HomeView homeView = new HomeView(habitService, storage);

        Scene scene = new Scene(homeView.create(), 820, 560);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        stage.setTitle("Micro-Habits Coach");
        stage.setScene(scene);

        // Auto-load when the UI is ready
        homeView.loadOnStartup();

        // Auto-save when user closes the app
        stage.setOnCloseRequest(e -> homeView.saveOnExit());

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
