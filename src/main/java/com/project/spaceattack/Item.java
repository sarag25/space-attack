package com.project.spaceattack;

import javafx.scene.image.ImageView;

/**
 * Basic sprite for items of the game, it extends SpriteAttackSprite.
 */
public class Item extends SpaceAttackSprite{

    /**
     * Damage on collision.
     */
    int damage;

    /**
     * Initialize a new item.
     * @param view the image of the item
     * @param location the location of the item
     * @param velocity the velocity of the item
     * @param description the description of the item
     * @param damage the damage on collision of the item
     */
    public Item(ImageView view, PVector location, PVector velocity, String description, int damage) {
        super(view, location, velocity, description);
        this.damage = damage;
    }
    public int getDamage() {
        return damage;
    }
    public void setDamage(int damage) {
        this.damage = damage;
    }
}
