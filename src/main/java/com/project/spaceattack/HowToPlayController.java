package com.project.spaceattack;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * The controller for the How To Play view.
 */
public class HowToPlayController implements Initializable{
    @FXML private ImageView home;
    @FXML private ImageView menu;
    @FXML private AnchorPane pane1;
    @FXML private AnchorPane pane2;
    @FXML private ScrollPane paneGame;
    @FXML private ScrollPane paneCharacters;
    @FXML private ScrollPane paneOperate;
    @FXML private ScrollPane panePlay;
    @FXML private ScrollPane paneAbout;

    public void playMenu(Stage stage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(MenuApplication.class.getResource("how-to-play-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        stage.setTitle("Gameplay control");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons" + "/game" +
                "-development" + ".png"))));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Transition of the pane.
     */
    void resize(){
        pane1.setVisible(false);

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), pane1);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.play();

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), pane2);
        translateTransition.setByX(-600);
        translateTransition.play();
    }

    /**
     * Opens the right tab upon mouse click.
     * @param url the location used to resolve relative paths for the root object, or {@code null} if the location is
     * not known.
     * @param resourceBundle the resources used to localize the root object, or {@code null} if the root object was
     * not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        home.setOnMouseClicked(e -> {
            Stage stage = (Stage) home.getScene().getWindow();
            stage.close();
        });

        resize();

        menu.setOnMouseClicked(e -> {
            pane1.setVisible(true);

            FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(0.5), pane1);
            fadeTransition1.setFromValue(0);
            fadeTransition1.setToValue(0.15);
            fadeTransition1.play();

            TranslateTransition translateTransition1 = new TranslateTransition(Duration.seconds(0.5), pane2);
            translateTransition1.setByX(+600);
            translateTransition1.play();
        });

        pane1.setOnMouseClicked(e -> {
            FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(0.5), pane1);
            fadeTransition1.setFromValue(1);
            fadeTransition1.setToValue(0);
            fadeTransition1.play();

            fadeTransition1.setOnFinished(e1 -> {
                pane1.setVisible(false);
            });

            TranslateTransition translateTransition1 = new TranslateTransition(Duration.seconds(0.5), pane2);
            translateTransition1.setByX(-600);
            translateTransition1.play();
        });
    }

    /**
     * Game button handler.
     */
    @FXML void onGameButtonClick() {
        resize();
        paneGame.toFront();
    }

    /**
     * Characters button handler.
     */
    @FXML void onCharactersButtonClick() {
        resize();
        paneCharacters.toFront();
    }

    /**
     * Operate button handler.
     */
    @FXML void onOperateButtonClick() {
        resize();
        paneOperate.toFront();
    }

    /**
     * Play button handler.
     */
    @FXML void onPlayButtonClick() {
        resize();
        panePlay.toFront();
    }

    /**
     * About button handler.
     */
    @FXML void onAboutButtonClick() {
        resize();
        paneAbout.toFront();
    }
}