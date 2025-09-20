package com.badlogic.exercise;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.audio.Sound; // Import for sound

public class DialogShowBegining {
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

    private Sound dialogTriggerSound; // Field for the sound effect
    private Sound flipSound; // New: Field for the flip sound

    private float dialogTransitionTimer = 0f;
    private static final float DIALOG_TRANSITION_DURATION = 0.5f; // Duration for the animation of each image appearing

    private float dialogDisplayTimer = 0f; // Timer to control how long each dialog image is displayed

    // Array to hold custom display durations for each dialog image
    // You can customize these values. For example:
    // { Image1_Duration, Image2_Duration, Image3_Duration, ... }
    // Ensure the array size matches TOTAL_NUMBER_OF_DIALOGS.
    private static final float[] DIALOG_DISPLAY_DURATIONS = {
        3.0f, // Duration for 1.png
        2.5f, // Duration for 2.png
        5.9f, // Duration for 3.png
        5.2f, // Duration for 4.png
        6.2f, // Duration for 5.png
        5.4f, // Duration for 6.png
        5.2f  // Duration for 7.png
    };

    private boolean dialogAnimationComplete = false;

    private static final int TOTAL_NUMBER_OF_DIALOGS = 7;

    private boolean sequenceFinished = false;

    public DialogShowBegining(SpriteBatch spriteBatch, float worldWidth, float worldHeight) {
        this.spriteBatch = spriteBatch;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;

        // Placeholder texture for missing images
        Pixmap pixmap = new Pixmap(400, 200, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLUE);
        pixmap.fill();
        Texture placeholderTexture = new Texture(pixmap);
        pixmap.dispose();

        // Updated image filenames from dialogueX.png to X.png
        try { dialogue1Texture = new Texture("1.png"); } catch (Exception e) { Gdx.app.error("DialogShowBegining", "Error loading 1.png", e); dialogue1Texture = placeholderTexture; }
        try { dialogue2Texture = new Texture("2.png"); } catch (Exception e) { Gdx.app.error("DialogShowBegining", "Error loading 2.png", e); dialogue2Texture = placeholderTexture; }
        try { dialogue3Texture = new Texture("3.png"); } catch (Exception e) { Gdx.app.error("DialogShowBegining", "Error loading 3.png", e); dialogue3Texture = placeholderTexture; }
        try { dialogue4Texture = new Texture("4.png"); } catch (Exception e) { Gdx.app.error("DialogShowBegining", "Error loading 4.png", e); dialogue4Texture = placeholderTexture; }
        try { dialogue5Texture = new Texture("5.png"); } catch (Exception e) { Gdx.app.error("DialogShowBegining", "Error loading 5.png", e); dialogue5Texture = placeholderTexture; }
        try { dialogue6Texture = new Texture("6.png"); } catch (Exception e) { Gdx.app.error("DialogShowBegining", "Error loading 6.png", e); dialogue6Texture = placeholderTexture; }
        try { dialogue7Texture = new Texture("7.png"); } catch (Exception e) { Gdx.app.error("DialogShowBegining", "Error loading 7.png", e); dialogue7Texture = placeholderTexture; }

        // Load the sound effect for dialog triggering/advancing
        try {
            dialogTriggerSound = Gdx.audio.newSound(Gdx.files.internal("intro.mp3"));
        } catch (Exception e) {
            Gdx.app.error("DialogShowBegining", "Error loading intro.mp3", e);
            dialogTriggerSound = null; // Set to null if loading fails
        }

        // New: Load the flip sound
        try {
            flipSound = Gdx.audio.newSound(Gdx.files.internal("flip.mp3"));
        } catch (Exception e) {
            Gdx.app.error("DialogShowBegining", "Error loading flip.mp3", e);
            flipSound = null; // Set to null if loading fails
        }
    }

    public void updateDialogSequence(float delta) {
        if (isDialogSequenceActive) {
            dialogTransitionTimer += delta;
            if (dialogTransitionTimer >= DIALOG_TRANSITION_DURATION) {
                dialogTransitionTimer = DIALOG_TRANSITION_DURATION;
                dialogAnimationComplete = true;

                // If animation is complete, start/continue the display timer
                dialogDisplayTimer += delta;
                // Use the specific duration for the current dialog index
                if (currentDialogIndex < DIALOG_DISPLAY_DURATIONS.length && dialogDisplayTimer >= DIALOG_DISPLAY_DURATIONS[currentDialogIndex]) {
                    // Time for the current dialog to advance
                    dialogDisplayTimer = 0f; // Reset display timer
                    dialogTransitionTimer = 0f; // Reset transition timer for next image's animation
                    dialogAnimationComplete = false; // Reset animation complete for next image

                    currentDialogIndex++; // Advance to the next dialog

                    // New: Play flip sound when dialog advances
                    if (flipSound != null) {
                        flipSound.play();
                    }

                    if (currentDialogIndex >= TOTAL_NUMBER_OF_DIALOGS) {
                        isDialogSequenceActive = false;
                        sequenceFinished = true;
                        Gdx.app.log("DialogShowBegining", "Dialog sequence finished.");
                        if (dialogTriggerSound != null) {
                            dialogTriggerSound.stop(); // Stop sound when sequence finishes
                        }
                    } else {
                        Gdx.app.log("DialogShowBegining", "Advancing to dialog " + (currentDialogIndex + 1));
                    }
                }
            }
        }
    }

    public void drawDialogSequence() {
        if (!isDialogSequenceActive) return;

        ScreenUtils.clear(Color.BLACK); // Clear to black first, as requested

        spriteBatch.begin(); // Start batch for drawing dialog

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
            default:
                Gdx.app.error("DialogShowBegining", "Attempted to draw dialog at out-of-bounds index: " + currentDialogIndex);
                isDialogSequenceActive = false;
                sequenceFinished = true;
                spriteBatch.end();
                return;
        }

        if (dialogTexture == null) {
            Gdx.app.error("DialogShowBegining", "Dialog texture is null for index: " + currentDialogIndex);
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

        float targetScale = maxFitScale * 1.08f; // Adjusted for slightly smaller

        float currentScale = Interpolation.elasticOut.apply(0.01f, targetScale, progress);

        float scaledWidth = dialogOriginalWidth * currentScale;
        float scaledHeight = dialogOriginalHeight * currentScale;

        // Calculate X for horizontal centering
        float x = (this.worldWidth / 2) - (scaledWidth / 2);

        float y = (this.worldHeight / 2) - (scaledHeight / 2) + (this.worldHeight * 0.05f); // Adjusted Y offset

        // Draw dialogue picture
        spriteBatch.draw(dialogTexture, x, y, scaledWidth, scaledHeight);
        spriteBatch.end();
    }

    public void triggerDialogSequence() {
        if (!isDialogSequenceActive) {
            isDialogSequenceActive = true;
            currentDialogIndex = 0;
            dialogTransitionTimer = 0f;
            dialogDisplayTimer = 0f; // Reset display timer when triggering
            dialogAnimationComplete = false;
            sequenceFinished = false;
            Gdx.app.log("DialogShowBegining", "Dialog sequence activated.");
            if (dialogTriggerSound != null) {
                dialogTriggerSound.play(); // Play sound when dialog sequence is triggered
            }
        }
    }

    // This method is no longer needed for automatic progression
    public boolean handleDialogInput(boolean isClick, boolean isEnter) {
        // Input is no longer used for advancing dialog in automatic mode.
        // It will always return false, meaning no input is processed to advance the dialog.
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
        if (dialogue1Texture != null) dialogue1Texture.dispose();
        if (dialogue2Texture != null) dialogue2Texture.dispose();
        if (dialogue3Texture != null) dialogue3Texture.dispose();
        if (dialogue4Texture != null) dialogue4Texture.dispose();
        if (dialogue5Texture != null) dialogue5Texture.dispose();
        if (dialogue6Texture != null) dialogue6Texture.dispose();
        if (dialogue7Texture != null) dialogue7Texture.dispose();
        if (dialogTriggerSound != null) dialogTriggerSound.dispose(); // Dispose the sound
        if (flipSound != null) flipSound.dispose(); // New: Dispose the flip sound
    }
}
