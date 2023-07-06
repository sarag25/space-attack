package com.project.spaceattack;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.nio.file.Paths;

/**
 * The sound effects of the game.
 */
public class Sound {
    MediaPlayer mediaPlayer;

    /**
     * Player shooting sound effect.
     */
    void musicShoot(){
        mediaPlayer = new MediaPlayer(new Media(Paths.get("src/main/resources/com/project/spaceattack" +
                "/audio/laser-gun.mp3").toUri().toString()));
        mediaPlayer.play();
    }

    /**
     * Error sound effect.
     */
    void musicError(){
        mediaPlayer = new MediaPlayer(new Media(Paths.get("src/main/resources/com/project/spaceattack" +
                "/audio/error-sound.mp3").toUri().toString()));
        mediaPlayer.play();
    }

    /**
     * Game over sound effect.
     */
    void musicGameOver(){
        mediaPlayer = new MediaPlayer(new Media(Paths.get("src/main/resources/com/project/spaceattack" +
                "/audio/game-over.mp3").toUri().toString()));
        mediaPlayer.play();
    }

    /**
     * Power up sound effect.
     */
    void musicPowerUp(){
        mediaPlayer = new MediaPlayer(new Media(Paths.get("src/main/resources/com/project/spaceattack" +
                "/audio/power-up-sound.mp3").toUri().toString()));
        mediaPlayer.play();
    }
}