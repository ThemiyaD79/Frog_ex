package com.badlogic.exercise;

// This interface allows Character and FlyCutsceneScreen to work with any level class.
public interface GameLevel {
    // These fields are needed by the Character class for scrolling
    float getTileScrollX();
    void setTileScrollX(float value);

    float getBackgroundScrollX();
    void setBackgroundScrollX(float value);
}
