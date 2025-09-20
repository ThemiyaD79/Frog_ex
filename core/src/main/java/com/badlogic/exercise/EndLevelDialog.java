package com.badlogic.exercise;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Interpolation;

public class EndLevelDialog {
    private SpriteBatch spriteBatch;
    private float worldWidth;
    private float worldHeight;

    private boolean isDialogSequenceActive = false;
    private int currentDialogIndex = 0;

    private Texture dialogBackTexture;
    private Texture dialog1Texture;
    private Texture dialog2Texture;
    private Texture emptyPotionIconTexture; // Changed: Texture for the small potion icon
    private Texture continueButtonTexture;
    private Texture exitButtonTexture;

    private Rectangle continueBounds;
    private Rectangle exitBounds;

    private float dialogTransitionTimer = 0f;
    private static final float DIALOG_TRANSITION_DURATION = 0.5f;
    private boolean dialogAnimationComplete = false;

    private static final int TOTAL_DIALOGS = 2; // Still 2 main dialogs (dialogue1, dialogue2)

    private boolean sequenceFinished = false;
    private boolean showButtons = false;
    private boolean showEmptyPotionIcon = false; // New: Flag to show the small empty potion icon
    private float emptyPotionSparkTimer = 0f; // New: Timer for the icon's spark animation
    private static final float SPARK_DURATION = 1.5f; // New: Duration for one cycle of spark animation

    private boolean continueClicked = false;
    private boolean exitClicked = false;

    public EndLevelDialog(SpriteBatch spriteBatch, float worldWidth, float worldHeight) {
        this.spriteBatch = spriteBatch;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;

        // --- Load Textures ---
        try {
            dialogBackTexture = new Texture("dialogBack.png");
        } catch (Exception e) {
            Gdx.app.error("EndLevelDialog", "Error loading DialogBack.png", e);
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.BLUE);
            pixmap.fill();
            dialogBackTexture = new Texture(pixmap);
            pixmap.dispose();
        }

        Pixmap placeholderPixmap = new Pixmap(400, 200, Pixmap.Format.RGBA8888);
        placeholderPixmap.setColor(Color.BLUE);
        placeholderPixmap.fill();
        Texture placeholderTexture = new Texture(placeholderPixmap);
        placeholderPixmap.dispose();

        try {
            dialog1Texture = new Texture("dialogue1.png");
        } catch (Exception e) {
            Gdx.app.error("EndLevelDialog", "Error loading dialogue1.png", e);
            dialog1Texture = placeholderTexture;
        }

        try {
            dialog2Texture = new Texture("newlines2.png");
        } catch (Exception e) {
            Gdx.app.error("EndLevelDialog", "Error loading newlines2.png", e);
            dialog2Texture = placeholderTexture;
        }

        try {
            emptyPotionIconTexture = new Texture("potionempty1.png"); // Load the small empty potion icon
        } catch (Exception e) {
            Gdx.app.error("EndLevelDialog", "Error loading emptypotion.png", e);
            emptyPotionIconTexture = placeholderTexture; // Use placeholder if not found
        }

        try {
            continueButtonTexture = new Texture("playButton.png");
        } catch (Exception e) {
            Gdx.app.error("EndLevelDialog", "Error loading Pause.png", e);
            continueButtonTexture = placeholderTexture;
        }

        try {
            exitButtonTexture = new Texture("Exit.png");
        } catch (Exception e) {
            Gdx.app.error("EndLevelDialog", "Error loading Exit.png", e);
            exitButtonTexture = placeholderTexture;
        }

        // --- Initialize Bounds ---
        continueBounds = new Rectangle();
        exitBounds = new Rectangle();
    }

    public void trigger() {
        if (!isDialogSequenceActive) {
            isDialogSequenceActive = true;
            currentDialogIndex = 0;
            dialogTransitionTimer = 0f;
            dialogAnimationComplete = false;
            sequenceFinished = false;
            continueClicked = false;
            exitClicked = false;
            showButtons = false;
            showEmptyPotionIcon = false; // Reset icon display state
            emptyPotionSparkTimer = 0f; // Reset spark timer
            Gdx.app.log("EndLevelDialog", "Dialog sequence started.");
        }
    }

    public void update(float delta) {
        if (isDialogSequenceActive) {
            dialogTransitionTimer += delta;
            if (dialogTransitionTimer >= DIALOG_TRANSITION_DURATION) {
                dialogTransitionTimer = DIALOG_TRANSITION_DURATION;
                dialogAnimationComplete = true;
            }

            // Update spark timer if the icon is shown
            if (showEmptyPotionIcon) {
                emptyPotionSparkTimer = (emptyPotionSparkTimer + delta) % SPARK_DURATION;
            }
        }
    }

    public void render() {
        if (!isDialogSequenceActive) return;

        ScreenUtils.clear(Color.BLACK);
        spriteBatch.begin();

        spriteBatch.draw(dialogBackTexture, 0, 0, worldWidth, worldHeight);

        Texture currentTexture = null;
        if (currentDialogIndex == 0) {
            currentTexture = dialog1Texture;
        } else if (currentDialogIndex == 1) {
            currentTexture = dialog2Texture; // This is the dialog where the icon appears
        }

        if (currentTexture == null) {
            Gdx.app.error("EndLevelDialog", "Missing dialog texture at index " + currentDialogIndex);
            isDialogSequenceActive = false;
            sequenceFinished = true;
            spriteBatch.end();
            return;
        }

        float progress = dialogTransitionTimer / DIALOG_TRANSITION_DURATION;
        float originalW = currentTexture.getWidth();
        float originalH = currentTexture.getHeight();

        float fitScaleX = worldWidth / originalW;
        float fitScaleY = worldHeight / originalH;
        float scale = Math.min(fitScaleX, fitScaleY) * 1.08f;

        float animScale = Interpolation.elasticOut.apply(0.01f, scale, progress);
        float w = originalW * animScale;
        float h = originalH * animScale;

        float x = (worldWidth - w) / 2f;
        float y = (worldHeight - h) / 2f + (worldHeight * 0.05f);

        spriteBatch.draw(currentTexture, x, y, w, h);

        // --- Render the small empty potion icon on dialog2Texture ---
        if (currentDialogIndex == 1 && showEmptyPotionIcon) {
            float iconSize = 64; // Adjust size as needed
            // Position the icon (e.g., top-right of the dialog image area)
            // These coordinates are relative to the world, adjust to fit your dialog2Texture
            float iconX = x + w - iconSize - 50; // 50 units from right edge of dialog texture
            float iconY = y + h - iconSize - 50; // 50 units from top edge of dialog texture

            // Calculate alpha for sparking effect (pulsating)
            float alpha = Interpolation.sine.apply(0.5f, 1.0f, emptyPotionSparkTimer / SPARK_DURATION);
            Color batchColor = spriteBatch.getColor();
            spriteBatch.setColor(batchColor.r, batchColor.g, batchColor.b, alpha); // Apply alpha

            spriteBatch.draw(emptyPotionIconTexture, iconX, iconY, iconSize, iconSize);

            spriteBatch.setColor(batchColor); // Reset batch color to original
        }

        // --- Render Buttons ---
        if (showButtons) {
            float btnW = 200, btnH = 80;
            float btnY = 60;
            float spacing = 40;
            float totalWidth = btnW * 2 + spacing;
            float startX = (worldWidth - totalWidth) / 2;

            continueBounds.set(startX, btnY, btnW, btnH);
            exitBounds.set(startX + btnW + spacing, btnY, btnW, btnH);

            spriteBatch.draw(continueButtonTexture, continueBounds.x, continueBounds.y, continueBounds.width, continueBounds.height);
            spriteBatch.draw(exitButtonTexture, exitBounds.x, exitBounds.y, exitBounds.width, exitBounds.height);
        }

        spriteBatch.end();
    }

    // Modified handleInput method accepts world coordinates touchPosition
    public boolean handleInput(boolean isClick, boolean isEnter, Vector2 touchPosition) {
        if (!isDialogSequenceActive) return false;

        // Handle clicks on buttons if they are shown
        if (showButtons) {
            if (touchPosition == null) return false; // Defensive check

            if (continueBounds.contains(touchPosition)) {
                continueClicked = true;
                Gdx.app.log("EndLevelDialog", "Continue clicked.");
                return true;
            } else if (exitBounds.contains(touchPosition)) {
                exitClicked = true;
                Gdx.app.log("EndLevelDialog", "Exit clicked.");
                return true;
            }
            return false;
        }

        // Only proceed if current dialog has finished animating AND input is received
        if (dialogAnimationComplete && (isClick || isEnter)) {
            dialogTransitionTimer = 0f;
            dialogAnimationComplete = false;

            if (currentDialogIndex == TOTAL_DIALOGS - 1) { // This means dialog2Texture was just shown
                // After dialog2Texture, hide the icon and show buttons
                showEmptyPotionIcon = false; // Make the icon disappear
                showButtons = true; // Show the continue/exit buttons
                Gdx.app.log("EndLevelDialog", "From last dialog, hiding icon and showing buttons.");
                // Do NOT increment currentDialogIndex here, as we are transitioning to buttons, not another dialog.
            } else {
                // Move to the next regular dialog
                currentDialogIndex++;
                Gdx.app.log("EndLevelDialog", "Next dialog: " + currentDialogIndex);

                // If we just moved to dialog2Texture, show the empty potion icon
                if (currentDialogIndex == 1) {
                    showEmptyPotionIcon = true;
                    emptyPotionSparkTimer = 0f; // Start spark animation
                    Gdx.app.log("EndLevelDialog", "Now showing dialog2 and empty potion icon.");
                }
            }
            return true;
        }
        return false;
    }

    public boolean isActive() {
        return isDialogSequenceActive;
    }

    public boolean isFinished() {
        // Dialog sequence finishes when continue or exit button is clicked
        return continueClicked || exitClicked;
    }

    public boolean isContinueClicked() {
        return continueClicked;
    }

    public boolean isExitClicked() {
        return exitClicked;
    }

    public void reset() {
        sequenceFinished = false;
        showButtons = false;
        showEmptyPotionIcon = false; // Reset icon display state
        emptyPotionSparkTimer = 0f; // Reset spark timer
        continueClicked = false;
        exitClicked = false;
    }

    public void dispose() {
        if (dialogBackTexture != null) dialogBackTexture.dispose();
        if (dialog1Texture != null) dialog1Texture.dispose();
        if (dialog2Texture != null) dialog2Texture.dispose();
        if (emptyPotionIconTexture != null) emptyPotionIconTexture.dispose(); // Dispose icon texture
        if (continueButtonTexture != null) continueButtonTexture.dispose();
        if (exitButtonTexture != null) exitButtonTexture.dispose();
    }
}
