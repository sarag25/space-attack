package com.project.spaceattack;

/**
 * Declaration and initialization of constants.
 */
public class Constants {

    /**
     * General constants.
     */
    public static String DELIMITER = ";";
    public static int ZERO = 0, INVERT = -1, DEFAULT_COLLISION_DAMAGE = 1, DEFAULT_BOOST_MULTIPLIER = 1;
    public static int BORDER_LEFT = 5, BORDER_DOWN = 760, BORDER_RIGHT = 1465, BORDER_UP = 0, BORDER_WIDTH = 1540, BORDER_HEIGHT = 880;
    public static int DEFAULT_POINT_GAIN = 50, DEFAULT_POINT_REDUCTION = 25, DEFAULT_ITEM_COST = 500;

    /**
     * Player constants.
     */
    public static int STARTING_X = 740, STARTING_Y = 650, DEFAULT_MISSILE_SPEED = 10, DEFAULT_MISSILE_DAMAGE = 1;
    public static int DEFAULT_PLAYER_WIDTH = 70, DEFAULT_PLAYER_HEIGHT = 100, DEFAULT_PLAYER_SPEED = 7, DEFAULT_PLAYER_HEALTH = 3;

    /**
     * Item constants.
     */
    public static int DEFAULT_ITEM_WIDTH = 50, DEFAULT_ITEM_HEIGHT = 50;
    public static int DEFAULT_MISSILE_WIDTH = 20, DEFAULT_MISSILE_HEIGHT = 30, DEFAULT_BOOST_LENGTH = 5;
    public static int DEFAULT_MISSILE1_DAMAGE = 1, DEFAULT_MISSILE2_DAMAGE = DEFAULT_MISSILE1_DAMAGE * 2, DEFAULT_HEALING = -1;

    /**
     * Enemies constants.
     */
    public static int STARTING_ENEMY_Y = -50, DEFAULT_ARRAY_CAPACITY = 5;
    public static int DEFAULT_ENEMY1_SIZE = 60, DEFAULT_ENEMY2_SIZE = 50;
    public static int DEFAULT_ENEMY1_HEALTH = 1, DEFAULT_ENEMY2_HEALTH = 2;
    public static int[] ENEMY_ROWS = {50, 150, 250, 350, 450};

    /**
     * Probabilities constants.
     */
    public static double DEFAULT_PROBABILITY = 0.006;
}
