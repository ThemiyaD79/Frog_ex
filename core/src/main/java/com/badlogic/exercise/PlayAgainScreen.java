package com.badlogic.exercise;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class PlayAgainScreen implements Disposable {
    private final SpriteBatch spriteBatch;
    private final FitViewport viewport;
    private final BitmapFont font; // Keep font for potential future use, though not drawing text now
    private final Texture playAgainButtonTexture;
    private Texture greenTileTexture; // Background texture
    private final Texture exitButtonTexture; // NEW: Exit button texture
    private boolean playAgainClicked = false;
    private boolean exitGameTriggered = false;

    /**
     * Constructor for PlayAgainScreen.
     *
     * @param spriteBatch The SpriteBatch instance for drawing.
     * @param viewport The FitViewport for consistent scaling.
     * @param font The BitmapFont for drawing text.
     * @param playAgainButtonTexture The texture for the "Play Again" button.
     * @param exitButtonTexture The texture for the "Exit" button. // NEW: Added exitButtonTexture parameter
     */
    public PlayAgainScreen(SpriteBatch spriteBatch, FitViewport viewport, BitmapFont font, Texture playAgainButtonTexture, Texture exitButtonTexture) {
        this.spriteBatch = spriteBatch;
        this.viewport = viewport;
        this.font = font;
        this.playAgainButtonTexture = playAgainButtonTexture;
        this.exitButtonTexture = exitButtonTexture; // NEW: Initialize exitButtonTexture

        // Load the GreenTile background texture
        try {
            greenTileTexture = new Texture("GreenTile.png");
        } catch (Exception e) {
            Gdx.app.error("PlayAgainScreen", "Error loading GreenTile.png", e);
            // Fallback to a plain color if texture fails to load
            greenTileTexture = null; // Set to null to indicate failure
        }
    }

    /**
     * Renders the game over screen, including the "Game Over!" text and "Play Again" button.
     */
    public void render() {
        // Clear to black, then draw the background texture
        ScreenUtils.clear(Color.BLACK);

        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        float ww = viewport.getWorldWidth();
        float wh = viewport.getWorldHeight();

        // Draw GreenTile.png to cover the entire screen
        if (greenTileTexture != null) {
            spriteBatch.draw(greenTileTexture, 0, 0, ww, wh);
        } else {
            // If GreenTile.png failed to load, draw a fallback color
            ScreenUtils.clear(Color.GREEN); // Fallback to green color
        }

        // Draw Play Again button in the middle
        float playAgainButtonWidth = 2.0f; // Adjust size as needed
        float playAgainButtonHeight = 1.0f; // Adjust size as needed
        float playAgainButtonX = (ww - playAgainButtonWidth) / 2f;
        float playAgainButtonY = (wh - playAgainButtonHeight) / 2f + 0.3f; // Slightly above center
        spriteBatch.draw(playAgainButtonTexture, playAgainButtonX, playAgainButtonY, playAgainButtonWidth, playAgainButtonHeight);

        // Draw Exit button below the Play Again button
        if (exitButtonTexture != null) {
            float exitButtonWidth = 1.5f; // Adjust size as needed
            float exitButtonHeight = 0.75f; // Adjust size as needed
            float exitButtonX = (ww - exitButtonWidth) / 2f;
            float exitButtonY = playAgainButtonY - exitButtonHeight - 0.2f; // Below Play Again button
            spriteBatch.draw(exitButtonTexture, exitButtonX, exitButtonY, exitButtonWidth, exitButtonHeight);
        }

        spriteBatch.end();
    }

    /**
     * Handles touch up events on the Play Again screen.
     *
     * @param screenX The x-coordinate of the touch in screen coordinates.
     * @param screenY The y-coordinate of the touch in screen coordinates.
     * @return True if a button was clicked, false otherwise.
     */
    public boolean handleTouchUp(int screenX, int screenY) {
        Vector2 pos = new Vector2(screenX, screenY);
        viewport.unproject(pos); // Convert screen coordinates to world coordinates

        float ww = viewport.getWorldWidth();
        float wh = viewport.getWorldHeight();

        // Play Again button bounds
        float playAgainButtonWidth = 2.0f;
        float playAgainButtonHeight = 1.0f;
        float playAgainButtonX = (ww - playAgainButtonWidth) / 2f;
        float playAgainButtonY = (wh - playAgainButtonHeight) / 2f + 0.3f;
        Rectangle playAgainBounds = new Rectangle(playAgainButtonX, playAgainButtonY, playAgainButtonWidth, playAgainButtonHeight);

        // Exit button bounds
        float exitButtonWidth = 1.5f;
        float exitButtonHeight = 0.75f;
        float exitButtonX = (ww - exitButtonWidth) / 2f;
        float exitButtonY = playAgainButtonY - exitButtonHeight - 0.2f;
        Rectangle exitBounds = new Rectangle(exitButtonX, exitButtonY, exitButtonWidth, exitButtonHeight);


        if (playAgainBounds.contains(pos)) {
            playAgainClicked = true; // Set flag to indicate button was clicked
            Gdx.app.log("PlayAgainScreen", "Play Again button clicked.");
            return true;
        } else if (exitBounds.contains(pos)) { // Check for Exit button click
            exitGameTriggered = true; // Set flag to indicate exit was triggered
            Gdx.app.log("PlayAgainScreen", "Exit button clicked.");
            return true;
        }
        return false;
    }

    /**
     * Handles key down events on the Play Again screen.
     *
     * @param keycode The keycode of the pressed key.
     * @return True if the Enter key or Escape key was pressed, false otherwise.
     */
    public boolean handleKeyDown(int keycode) {
        if (keycode == Input.Keys.ENTER) {
            playAgainClicked = true; // Set flag to indicate button was clicked
            Gdx.app.log("PlayAgainScreen", "Enter key pressed. Play Again triggered.");
            return true;
        } else if (keycode == Input.Keys.ESCAPE) { // Handle Escape key for exit
            exitGameTriggered = true;
            Gdx.app.log("PlayAgainScreen", "Escape key pressed. Exit game triggered.");
            return true;
        }
        return false;
    }

    /**
     * Checks if the "Play Again" button has been clicked or Enter key pressed.
     *
     * @return True if "Play Again" was triggered, false otherwise.
     */
    public boolean isPlayAgainTriggered() {
        return playAgainClicked;
    }

    /**
     * Checks if the "Exit" button has been clicked or Escape key pressed.
     *
     * @return True if "Exit" was triggered, false otherwise.
     */
    public boolean isExitGameTriggered() {
        return exitGameTriggered;
    }

    /**
     * Resets the play again and exit trigger flags. Should be called after the game is reset.
     */
    public void resetTrigger() {
        playAgainClicked = false;
        exitGameTriggered = false; // Reset exit trigger
    }

    @Override
    public void dispose() {
        // Textures and fonts are managed and disposed by Main class,
        // so no need to dispose them here.
        // Dispose the background texture
        if (greenTileTexture != null) {
            greenTileTexture.dispose();
        }
        // Dispose the exit button texture
        if (exitButtonTexture != null) {
            exitButtonTexture.dispose();
        }
    }
}
