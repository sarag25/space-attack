package com.project.spaceattack;

/**
 * A basic vector to set the location of objects.
 */
public class PVector {
    double x;
    double y;

    /**
     * Constructor for a 2D vector.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public PVector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Add a vector to this vector.
     * @param v the vector to be added
     */
    public PVector add(PVector v) {
        return new PVector(x + v.x, y + v.y);
    }

    /**
     * Limit the magnitude of this vector.
     * @param max the maximum length to limit this vector
     */
    public PVector limit(double max) {
        if (magnitude() < max) {
            return copy();
        }
        return normalize().multiply(max);
    }

    /**
     * Calculate the magnitude (length) of the vector.
     * @return the length of the vector
     */
    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Get a copy of this vector.
     */
    public PVector copy() {
        return new PVector(x, y);
    }

    /**
     * Normalize the vector to length 1 (make it a unit vector).
     */
    public PVector normalize() {
        double magnitude = magnitude();
        if (magnitude == 0 || magnitude == 1) {
            return copy();
        }
        return multiply(1 / magnitude);
    }

    /**
     * Multiply this vector by a scalar.
     * @param n the value to multiply by
     */
    public PVector multiply(double n) {
        return new PVector(x * n, y * n);
    }
}


