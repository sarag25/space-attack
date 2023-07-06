package com.project.spaceattack;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Scanner;
import static com.project.spaceattack.Constants.DELIMITER;

/**
 * Controller for the high scores view.
 */
public class HighScoresController {
    @FXML private Label labelName1 = new Label();
    @FXML private Label labelName2 = new Label();
    @FXML private Label labelName3 = new Label();
    @FXML private Label labelName4 = new Label();
    @FXML private Label labelName5 = new Label();
    @FXML private Label labelScore1 = new Label();
    @FXML private Label labelScore2 = new Label();
    @FXML private Label labelScore3 = new Label();
    @FXML private Label labelScore4 = new Label();
    @FXML private Label labelScore5 = new Label();
    @FXML private ImageView homeIcon;

    /**
     * Array that stores the top 5 high scores.
     */
    HighScore[] highScores = new HighScore[5];

    /**
     * Fills the array with the top 5 high score.
     * @return the filled array
     */
    HighScore[] fillHighScores() {
        HighScore[] res = new HighScore[highScores.length];
        for(int k = 0; k < res.length; k++) {
            res[k] = new HighScore("-", -1);
        }
        int index = 0;
        try {
            Scanner scanner = new Scanner(new File("src/main/resources/com/project/spaceattack/data/scores.txt"));
            scanner.useDelimiter(DELIMITER);
            while(scanner.hasNext()) {
                String name = scanner.next();
                String points = scanner.next();
                if(index >= 5) {
                    Arrays.sort(res, Comparator.comparingInt(v -> v.score));
                    if(Integer.parseInt(points) > res[0].score) {
                        res[0].name = name;
                        res[0].score = Integer.parseInt(points);
                    }
                }
                else {
                    res[index].name = name;
                    res[index].score = Integer.parseInt(points);
                    index++;
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Arrays.sort(res, Comparator.comparingInt(v -> v.score));
        for(HighScore h : res){
            if(h.score == -1){
                h.score = 0;
            }
        }
        return res;
    }

    /**
     * Fills the label with the top 5 high scores.
     */
    void fillLabels() {
        labelName5.setText(highScores[0].name);
        labelName4.setText(highScores[1].name);
        labelName3.setText(highScores[2].name);
        labelName2.setText(highScores[3].name);
        labelName1.setText(highScores[4].name);
        labelScore5.setText(String.valueOf(highScores[0].score));
        labelScore4.setText(String.valueOf(highScores[1].score));
        labelScore3.setText(String.valueOf(highScores[2].score));
        labelScore2.setText(String.valueOf(highScores[3].score));
        labelScore1.setText(String.valueOf(highScores[4].score));
    }
    public void playScores(Stage stage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(MenuApplication.class.getResource("high-scores-table.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("High scores");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons" + "/game" +
                "-development" + ".png"))));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        initialize();
    }

    /**
     * Initializes the view.
     */
    @FXML public void initialize() {
        highScores = fillHighScores();
        fillLabels();
    }

    /**
     * Home icon handler.
     */
    @FXML void onHomeIconClick() {
        Stage stage = (Stage) homeIcon.getScene().getWindow();
        stage.close();
    }

    /**
     * Clear button handler,
     * erases all the previous high scores.
     */
    @FXML void onClearDataButtonClick() {
        try {
            FileOutputStream writer = new FileOutputStream("src/main/resources/com/project/spaceattack/data/scores" +
                    ".txt");
            writer.write(("").getBytes());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        initialize();
    }
}