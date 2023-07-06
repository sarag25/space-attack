package com.project.spaceattack;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

/**
 * Basic sprite for objects of the game.
 */
public class Sprite extends Region {
    static final int MAX_SPEED = 30;
    PVector location;
    PVector velocity;
    PVector acceleration;
    ImageView view;

    /**
     * Initialize a new sprite.
     * @param view the image of the sprite
     * @param location the location of the sprite
     */
    public Sprite(ImageView view, PVector location) {
        this.view = view;
        this.location = location;
        this.velocity = new PVector(0, 0);
        this.acceleration = new PVector(0, 0);
        getChildren().add(view);
    }

    /**
     * Initialize a new sprite.
     * @param view the image of the sprite
     * @param location the location of the sprite
     * @param velocity the velocity of the sprite
     */
    public Sprite(ImageView view, PVector location, PVector velocity) {
        this.view = view;
        this.location = location;
        this.velocity = velocity;
        this.acceleration = new PVector(0, 0);
        getChildren().add(view);
    }
    public PVector getLocation() {
        return location;
    }

    public PVector getVelocity() {
        return velocity;
    }

    public void setVelocity(PVector velocity) {
        this.velocity = velocity;
    }

    /**
     * Updates a sprite attributes.
     */
    public void update() {
        velocity = velocity.add(acceleration);
        velocity = velocity.limit(MAX_SPEED);
        location = location.add(velocity);
    }

    /**
     * Checks a sprite intersection with another.
     * @param other the other sprite
     * @return true if it intersects, false if it does not
     */
    public boolean intersects(Sprite other) {
        return getBoundsInParent().intersects(other.getBoundsInParent());
    }

    /**
     * Displays a sprite.
     */
    public void display() {
        setTranslateX(location.x);
        setTranslateY(location.y);
    }
}
