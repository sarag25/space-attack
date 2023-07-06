package com.project.spaceattack;

import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * The controller for the Menu.
 */
public class MenuController {

    /**
     * Sets the difficulty of the game.
     */
    double difficulty;

    /**
     * Exit button handler.
     */
    @FXML void onExitButtonClick() {
        System.exit(0);
    }

    /**
     * Play button handler: normal difficulty is set to easy.
     */
    @FXML void onPlayButtonClick() {
        difficulty = 1;
        try {
            GameController playController = new GameController();
            Stage primaryStage = new Stage();
            playController.start(primaryStage, difficulty);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Easy play button handler.
     */
    @FXML void onPlayEasyButtonClick() {
        difficulty = 1;
        try {
            GameController playController = new GameController();
            Stage primaryStage = new Stage();
            playController.start(primaryStage, difficulty);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Hard play button handler.
     */
    @FXML void onPlayHardButtonClick() {
        difficulty = 1.5;
        try {
            GameController playController = new GameController();
            Stage primaryStage = new Stage();
            playController.start(primaryStage, difficulty);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * How to play button handler.
     */
    @FXML void onHowToPlayButtonClick() {
        try {
            HowToPlayController howToPlayController = new HowToPlayController();
            Stage stage = new Stage();
            howToPlayController.playMenu(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * High scores button handler.
     */
    @FXML void onHighScoresButtonClick() {
        try {
            HighScoresController highScoresTable = new HighScoresController();
            Stage stage = new Stage();
            highScoresTable.playScores(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}