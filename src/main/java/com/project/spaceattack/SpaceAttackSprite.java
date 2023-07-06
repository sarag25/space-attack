package com.project.spaceattack;

import javafx.scene.image.ImageView;

/**
 * Basic sprite for objects of the game, it extends Sprite.
 */
public class SpaceAttackSprite extends Sprite{
    String description;
    boolean isAlive = true;

    /**
     * Initialize a new space attack sprite.
     * @param view the image of the sprite
     * @param location the location of the sprite
     */
    public SpaceAttackSprite(ImageView view, PVector location) {
        super(view, location);
    }

    /**
     * Initialize a new space attack sprite.
     * @param view the image of the sprite
     * @param location the location of the sprite
     * @param velocity the velocity of the sprite
     * @param description the description of the sprite
     */
    public SpaceAttackSprite(ImageView view, PVector location, PVector velocity, String description) {
        super(view, location, velocity);
        this.description = description;
    }

    /**
     * Checks if a sprite is alive.
     * @return true if it is alive, false if it is not
     */
    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public String getDescription() {
        return description;
    }
}