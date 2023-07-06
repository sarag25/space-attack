package com.project.spaceattack;

/**
 * Object to store high scores.
 */
public class HighScore {
    public String name;
    public int score;

    /**
     * Initialize a new high score.
     * @param name the name of the player
     * @param score the score of the player
     */
    public HighScore(String name, int score) {
        this.name = name;
        this.score = score;
    }
}
