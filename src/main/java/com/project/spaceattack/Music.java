package com.project.spaceattack;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.nio.file.Paths;

/**
 * The music theme of the game.
 */
public class Music {
    MediaPlayer mediaPlayer = new MediaPlayer(new Media(Paths.get("src/main/resources/com/project/spaceattack" +
            "/audio/menu-music.mp3").toUri().toString()));

    /**
     * Starts the music theme of the game.
     */
    public void musicMenu() {
        mediaPlayer.play();
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.seek(Duration.ZERO);
            }
        });
    }
}
