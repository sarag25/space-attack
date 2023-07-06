package com.project.spaceattack;

import javafx.scene.image.ImageView;

/**
 * Basic sprite for ships of the game, it extends SpaceAttackSprite.
 */
public class ShipSprite extends SpaceAttackSprite{
    int health;

    /**
     * It represents the row to which the ship belongs to.
     */
    int row;

    /**
     * Initialize a new ship sprite.
     * @param view the image of the sprite
     * @param location the location of the sprite
     * @param velocity the velocity of the sprite
     * @param description the description of the sprite
     * @param health the health of the sprite
     * @param row the row of the sprite
     */
    public ShipSprite(ImageView view, PVector location, PVector velocity, String description, int health, int row) {
        super(view, location, velocity, description);
        this.health = health;
        this.row = row;
    }
    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getRow() {
        return row;
    }
}