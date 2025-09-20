package com.badlogic.exercise;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.audio.Sound; // Import for sound

public class DialogShow {
    private SpriteBatch spriteBatch;
    private float worldWidth;
    private float worldHeight;

    private boolean isDialogSequenceActive = false;
    private int currentDialogIndex = 0;

    // Individual Texture fields for each dialog picture
    private Texture dialogue1Texture;
    private Texture dialogue2Texture;
    private Texture dialogue3Texture;
    private Texture dialogue4Texture;
    private Texture dialogue5Texture;
    private Texture dialogue6Texture;
    private Texture dialogue7Texture;
    private Texture dialogue8Texture;

    private Texture dialogBackTexture;

    private float dialogTransitionTimer = 0f;
    private static final float DIALOG_TRANSITION_DURATION = 0.5f;
    private boolean dialogAnimationComplete = false;

    private static final int TOTAL_NUMBER_OF_DIALOGS = 8;

    private boolean sequenceFinished = false;

    private Sound flipSound; // New: Field for the flip sound

    public DialogShow(SpriteBatch spriteBatch, float worldWidth, float worldHeight) {
        this.spriteBatch = spriteBatch;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;

        try {
            dialogBackTexture = new Texture("dialogBack.png");
        } catch (Exception e) {
            Gdx.app.error("DialogShow", "Error loading DialogBack.png", e);
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.BLUE);
            pixmap.fill();
            dialogBackTexture = new Texture(pixmap);
            pixmap.dispose();
        }

        Pixmap pixmap = new Pixmap(400, 200, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLUE);
        pixmap.fill();
        Texture placeholderTexture = new Texture(pixmap);
        pixmap.dispose();

        try { dialogue1Texture = new Texture("dialogue1.png"); } catch (Exception e) { Gdx.app.error("DialogShow", "Error loading dialogue1.png", e); dialogue1Texture = placeholderTexture; }
        try { dialogue2Texture = new Texture("dialogue2.png"); } catch (Exception e) { Gdx.app.error("DialogShow", "Error loading dialogue2.png", e); dialogue2Texture = placeholderTexture; }
        try { dialogue3Texture = new Texture("dialogue3.png"); } catch (Exception e) { Gdx.app.error("DialogShow", "Error loading dialogue3.png", e); dialogue3Texture = placeholderTexture; }
        try { dialogue4Texture = new Texture("dialogue4.png"); } catch (Exception e) { Gdx.app.error("DialogShow", "Error loading dialogue4.png", e); dialogue4Texture = placeholderTexture; }
        try { dialogue5Texture = new Texture("dialogue5.png"); } catch (Exception e) { Gdx.app.error("DialogShow", "Error loading dialogue5.png", e); dialogue5Texture = placeholderTexture; }
        try { dialogue6Texture = new Texture("dialogue6.png"); } catch (Exception e) { Gdx.app.error("DialogShow", "Error loading dialogue6.png", e); dialogue6Texture = placeholderTexture; }
        try { dialogue7Texture = new Texture("dialogue7.png"); } catch (Exception e) { Gdx.app.error("DialogShow", "Error loading dialogue7.png", e); dialogue7Texture = placeholderTexture; }
        try { dialogue8Texture = new Texture("dialogue8.png"); } catch (Exception e) { Gdx.app.error("DialogShow", "Error loading dialogue8.png", e); dialogue8Texture = placeholderTexture; }

        // New: Load the flip sound
        try {
            flipSound = Gdx.audio.newSound(Gdx.files.internal("flip.mp3"));
        } catch (Exception e) {
            Gdx.app.error("DialogShow", "Error loading flip.mp3", e);
            flipSound = null; // Set to null if loading fails
        }
    }

    public void updateDialogSequence(float delta) {
        if (isDialogSequenceActive) {
            dialogTransitionTimer += delta;
            if (dialogTransitionTimer >= DIALOG_TRANSITION_DURATION) {
                dialogTransitionTimer = DIALOG_TRANSITION_DURATION;
                dialogAnimationComplete = true;
            }
        }
    }

    public void drawDialogSequence() {
        if (!isDialogSequenceActive) return;

        ScreenUtils.clear(Color.BLACK); // Clear to black first

        spriteBatch.begin(); // Start batch for drawing background and dialog

        // Draw DialogBack.png to fill the entire world area
        spriteBatch.draw(dialogBackTexture, 0, 0, worldWidth, worldHeight);

        float progress = dialogTransitionTimer / DIALOG_TRANSITION_DURATION;

        Texture dialogTexture = null;
        switch (currentDialogIndex) {
            case 0:
                dialogTexture = dialogue1Texture;
                break;
            case 1:
                dialogTexture = dialogue2Texture;
                break;
            case 2:
                dialogTexture = dialogue3Texture;
                break;
            case 3:
                dialogTexture = dialogue4Texture;
                break;
            case 4:
                dialogTexture = dialogue5Texture;
                break;
            case 5:
                dialogTexture = dialogue6Texture;
                break;
            case 6:
                dialogTexture = dialogue7Texture;
                break;
            case 7:
                dialogTexture = dialogue8Texture;
                break;
            default:
                Gdx.app.error("DialogShow", "Attempted to draw dialog at out-of-bounds index: " + currentDialogIndex);
                isDialogSequenceActive = false;
                sequenceFinished = true;
                spriteBatch.end();
                return;
        }

        if (dialogTexture == null) {
            Gdx.app.error("DialogShow", "Dialog texture is null for index: " + currentDialogIndex);
            isDialogSequenceActive = false;
            sequenceFinished = true;
            spriteBatch.end();
            return;
        }

        float dialogOriginalWidth = dialogTexture.getWidth();
        float dialogOriginalHeight = dialogTexture.getHeight();

        float fitScaleX = worldWidth / dialogOriginalWidth;
        float fitScaleY = worldHeight / dialogOriginalHeight;
        float maxFitScale = Math.min(fitScaleX, fitScaleY);

        // --- MODIFIED: Made picture a little smaller (from 1.15f to 1.08f) ---
        float targetScale = maxFitScale * 1.08f; // Adjusted for slightly smaller

        float currentScale = Interpolation.elasticOut.apply(0.01f, targetScale, progress);

        float scaledWidth = dialogOriginalWidth * currentScale;
        float scaledHeight = dialogOriginalHeight * currentScale;

        // Calculate X for horizontal centering
        float x = (this.worldWidth / 2) - (scaledWidth / 2);

        // --- MODIFIED: Adjust Y position for the slightly smaller image ---
        // Adjusted from 0.07f to 0.05f (or keep 0.07f if that looked better with the new size)
        float y = (this.worldHeight / 2) - (scaledHeight / 2) + (this.worldHeight * 0.05f); // Adjusted Y offset

        // Draw dialogue picture on top of the background
        spriteBatch.draw(dialogTexture, x, y, scaledWidth, scaledHeight);
        spriteBatch.end();
    }

    public void triggerDialogSequence() {
        if (!isDialogSequenceActive) {
            isDialogSequenceActive = true;
            currentDialogIndex = 0;
            dialogTransitionTimer = 0f;
            dialogAnimationComplete = false;
            sequenceFinished = false;
            Gdx.app.log("DialogShow", "Dialog sequence activated.");
        }
    }

    public boolean handleDialogInput(boolean isClick, boolean isEnter) {
        if (isDialogSequenceActive && dialogAnimationComplete) {
            if (isClick || isEnter) {
                dialogTransitionTimer = 0f;
                dialogAnimationComplete = false;
                currentDialogIndex++;

                // New: Play flip sound when dialog advances
                if (flipSound != null) {
                    flipSound.play();
                }

                if (currentDialogIndex >= TOTAL_NUMBER_OF_DIALOGS) {
                    isDialogSequenceActive = false;
                    sequenceFinished = true;
                    Gdx.app.log("DialogShow", "Dialog sequence finished.");
                    return true;
                }
                Gdx.app.log("DialogShow", "Advancing to dialog " + (currentDialogIndex + 1));
                return true;
            }
        }
        return false;
    }

    public boolean isDialogActive() {
        return isDialogSequenceActive;
    }

    public boolean isSequenceFinished() {
        return sequenceFinished;
    }

    public void resetSequenceFinished() {
        this.sequenceFinished = false;
    }

    public void dispose() {
        if (dialogBackTexture != null) dialogBackTexture.dispose();
        if (dialogue1Texture != null) dialogue1Texture.dispose();
        if (dialogue2Texture != null) dialogue2Texture.dispose();
        if (dialogue3Texture != null) dialogue3Texture.dispose();
        if (dialogue4Texture != null) dialogue4Texture.dispose();
        if (dialogue5Texture != null) dialogue5Texture.dispose();
        if (dialogue6Texture != null) dialogue6Texture.dispose();
        if (dialogue7Texture != null) dialogue7Texture.dispose();
        if (dialogue8Texture != null) dialogue8Texture.dispose();
        if (flipSound != null) flipSound.dispose(); // New: Dispose the flip sound
    }
}
