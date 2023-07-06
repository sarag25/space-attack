package com.project.spaceattack;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * * Main class for the application,
 * * main method of this class is called by launcher class when execution.
 * @author Federico (Federico3737)
 * @author Sara (sarag25)
 * @version 2023.07.05
 */
public class MenuApplication extends Application {
    public static Music music = new Music();
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(MenuApplication.class.getResource("menu-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Space Attack");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons" + "/arcade-game.png"))));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        music.musicMenu();
    }
    public static void main(String[] args) {
        launch(args);
    }
}