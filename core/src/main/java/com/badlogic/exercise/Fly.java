package com.badlogic.exercise;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.audio.Sound; // Import for sound

/**
 * The Fly class manages a single horizontally moving texture (witch)
 * and a periodically appearing and disappearing firework effect at its trailing end.
 * It's designed for a specific character or object moving across the screen with an effect.
 */
public class Fly implements Disposable {
    private Texture texture; // The witch texture (image)
    private Texture fireworkTexture; // The first firework texture (FireWorks.png)
    private Texture fireworkTexture2; // The second firework texture (FireWorks2.png)
    // --- NEW: Background textures ---
    private Texture leftBackgroundTexture; // Background for left-to-right flight
    private Texture rightBackgroundTexture; // Background for right-to-left flight

    private float xPosition; // The current horizontal position of the single witch
    private float speed; // The speed at which the witch moves (pixels per second)
    private float yPosition; // The current Y-position of the bottom edge of the witch texture
    private float scaleFactor; // Scale factor to make the witch smaller

    // Scaled dimensions of the witch, calculated once in the constructor
    private float scaledWitchWidth;
    private float scaledWitchHeight;

    // Firework effect properties
    private float fireworkEffectX; // X position of the current firework burst
    private float fireworkEffectY; // Y position of the current firework burst
    private float fireworkEffectTimer; // Timer for the duration of the current firework burst
    private static final float FIREWORK_EFFECT_DURATION = 0.4f; // How long each firework burst lasts (in seconds)
    private static final float FIREWORK_SPAWN_INTERVAL = 0.2f; // How often a new firework burst is triggered (in seconds)
    private float fireworkSpawnTimer; // Timer to control when the next firework burst spawns

    private SpriteBatch flyBatch; // SpriteBatch for drawing the textures
    private OrthographicCamera flyCamera; // OrthographicCamera for consistent rendering

    // Flag to track current flight phase: true for bottom flight (Right to Left), false for top flight (Left to Right)
    private boolean isBottomFlight;
    // --- NEW: Flag to control if the flight cycle has completed ---
    private boolean cycleCompleted;

    // Sound field for fly sound
    private Sound flySound;

    public Fly(String texturePath, float initialSpeed, float yPosition) {
        try {
            this.texture = new Texture("witch.png"); // Load witch texture
        } catch (Exception e) {
            Gdx.app.error("Fly", "Error loading witch texture: wh.png. Ensure it exists.", e);
            this.texture = null;
        }

        try {
            this.fireworkTexture = new Texture("FireWorks.png"); // Load first firework texture
        } catch (Exception e) {
            Gdx.app.error("Fly", "Error loading firework texture: FireWorks.png. Ensure it exists.", e);
            this.fireworkTexture = null;
        }

        try {
            this.fireworkTexture2 = new Texture("FireWorks2.png"); // Load second firework texture
        } catch (Exception e) {
            Gdx.app.error("Fly", "Error loading firework texture: FireWorks2.png. Ensure it exists.", e);
            this.fireworkTexture2 = null;
        }

        // --- NEW: Load background textures ---
        try {
            this.leftBackgroundTexture = new Texture("dialogBack.png"); // Load left background texture
        } catch (Exception e) {
            Gdx.app.error("Fly", "Error loading leftFly.png: " + e.getMessage());
            this.leftBackgroundTexture = null;
        }

        try {
            this.rightBackgroundTexture = new Texture("dialogBack.png"); // Load right background texture
        } catch (Exception e) {
            Gdx.app.error("Fly", "Error loading rightFly.png: " + e.getMessage());
            this.rightBackgroundTexture = null;
        }
        // --- END NEW ---

        // Set speed magnitude (e.g., from 150f to 375f if original was 150f)
        // MODIFIED: Decreased speed multiplier from 7.5f to 6.0f to make it a little slower
        float baseSpeedMagnitude = Math.abs(initialSpeed * 5.5f);

        // Initial state: Start with Right Bottom to Left Bottom flight
        this.isBottomFlight = true;
        this.speed = -baseSpeedMagnitude; // Negative speed for right-to-left

        // Set initial Y position for the first flight phase (bottom)
        this.yPosition = 0; // Start at the very bottom of the screen

        // Set a scale factor for the witch's size (smaller)
        // MODIFIED: Increased scale factor from 0.6f to 0.8f to make the witch bigger
        this.scaleFactor = 0.9f;

        // Calculate scaled dimensions of the witch once in the constructor
        if (this.texture != null) {
            this.scaledWitchWidth = this.texture.getWidth() * this.scaleFactor;
            this.scaledWitchHeight = this.texture.getHeight() * this.scaleFactor;
        } else {
            this.scaledWitchWidth = 0;
            this.scaledWitchHeight = 0;
        }

        // Initial X position: Start off-screen to the right for the first flight
        this.xPosition = Gdx.graphics.getWidth();

        // Initialize firework timers
        this.fireworkEffectTimer = FIREWORK_EFFECT_DURATION; // Start with firework not visible
        this.fireworkSpawnTimer = 0;

        flyBatch = new SpriteBatch();

        flyCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        flyCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        flyCamera.update();

        // --- NEW: Initialize cycle completion flag ---
        this.cycleCompleted = false;

        // Load and play fly sound
        try {
            flySound = Gdx.audio.newSound(Gdx.files.internal("fly.mp3"));
            if (flySound != null) {
                flySound.loop(0.8f); // Play sound in a loop at 80% volume
            }
        } catch (Exception e) {
            Gdx.app.error("Fly", "Failed to load fly.mp3: " + e.getMessage());
        }
    }

    /**
     * Updates the position of the single flying witch and manages the firework effect.
     *
     * @param delta The time elapsed since the last frame, in seconds.
     */
    public void update(float delta) {
        if (texture == null) {
            return; // Do nothing if witch texture failed to load
        }

        // --- NEW: Stop movement if cycle is completed ---
        if (cycleCompleted) {
            speed = 0; // Ensure speed is zero
            if (flySound != null) {
                flySound.stop(); // Stop the sound when the cycle is completed
            }
            return; // Stop updating position and firework effects
        }

        // Update the witch's X position
        xPosition += speed * delta;

        // Logic to handle flight direction and reset based on current phase
        if (isBottomFlight) { // Currently flying Right Bottom to Left Bottom
            if (xPosition < -scaledWitchWidth) { // Witch went off left side
                xPosition = -scaledWitchWidth; // Reappear off-screen left
                speed = Math.abs(speed); // Change direction to Left to Right (positive speed)
                isBottomFlight = false; // Switch to top flight
                yPosition = Gdx.graphics.getHeight() - scaledWitchHeight; // Set Y to top
            }
        } else { // Currently flying Left Top to Right Top
            if (xPosition > Gdx.graphics.getWidth()) { // Witch went off right side
                xPosition = Gdx.graphics.getWidth(); // Reappear off-screen right
                yPosition = 0; // Set Y to bottom for the next Right to Left flight
                speed = -Math.abs(speed); // Change direction back to Right to Left (negative speed)
                isBottomFlight = true; // Switch to bottom flight
                // --- NEW: Mark cycle as completed after one full right-to-left and then left-to-right flight ---
                cycleCompleted = true;
            }
        }

        // Update firework spawn timer
        fireworkSpawnTimer += delta;
        if (fireworkSpawnTimer >= FIREWORK_SPAWN_INTERVAL) {
            // Trigger a new firework burst
            fireworkSpawnTimer = 0;
            fireworkEffectTimer = 0; // Start the firework effect timer

            // Get the scaled width of the *current* firework texture for accurate positioning.
            Texture tempFireworkTexture = (speed < 0) ? fireworkTexture : fireworkTexture2;
            float currentFireworkScaledWidth = (tempFireworkTexture != null ? tempFireworkTexture.getWidth() : 0) * scaleFactor * 0.7f;

            // Define how much the firework is behind the witch's tail
            // This offset is consistent for both directions, placing the firework's edge
            // slightly inside the visual tail of the witch.
            float offsetIntoTail = scaledWitchWidth * 0.1f; // 10% of witch width into the tail

            if (speed < 0) { // Flying right-to-left (witch is flipped, visual tail is on the right)
                // Firework's left edge should be at (witch's right edge - offsetIntoTail)
                fireworkEffectX = xPosition + scaledWitchWidth - offsetIntoTail;
            } else { // Flying left-to-right (witch is not flipped, visual tail is on the left)
                // Firework's right edge should be at (witch's left edge + offsetIntoTail)
                // So, firework's left edge is (witch's left edge + offsetIntoTail - firework's width)
                fireworkEffectX = xPosition + offsetIntoTail - currentFireworkScaledWidth;
            }

            // Adjust Y position to be at the witch's tail height
            float baseFireworkYOffset = scaledWitchHeight * 0.15f; // Base Y offset from bottom of witch
            fireworkEffectY = yPosition + baseFireworkYOffset;
        }

        // Update firework effect timer
        if (fireworkEffectTimer < FIREWORK_EFFECT_DURATION) {
            fireworkEffectTimer += delta;
        }
    }

    /**
     * Draws the single witch texture and the firework effect on the screen.
     */
    public void draw() {
        if (texture == null) {
            return; // Do nothing if witch texture failed to load
        }

        flyBatch.setProjectionMatrix(flyCamera.combined);
        flyBatch.begin();

        // --- NEW: Draw background based on flight direction ---
        // Backgrounds remain visible even after the witch stops
        if (rightBackgroundTexture != null) {
            // Draw right background to fill the screen
            flyBatch.draw(rightBackgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        if (leftBackgroundTexture != null) {
            // Draw left background to fill the screen
            flyBatch.draw(leftBackgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        // --- END NEW ---

        // Draw the witch using its pre-calculated scaled dimensions
        float drawU = 0;
        float drawU2 = 1;
        // --- MODIFIED: Flip logic for wh.png to *reverse* its facing direction relative to flight ---
        if (speed < 0) { // If speed is negative (flying right-to-left), do NOT flip (original image faces right)
            drawU = 0;
            drawU2 = 1;
        } else { // If speed is positive (flying left-to-right), flip horizontally (original image faces right)
            drawU = 1;
            drawU2 = 0;
        }
        // Swapped v and v2 (1, 0) to flip the texture vertically, as requested previously.
        flyBatch.draw(texture, xPosition, yPosition, scaledWitchWidth, scaledWitchHeight, drawU, 1, drawU2, 0);


        // Draw the firework effect if it's active
        if ((fireworkTexture != null || fireworkTexture2 != null) && fireworkEffectTimer < FIREWORK_EFFECT_DURATION) {
            // Calculate alpha for fading effect
            float alpha = 1.0f - (fireworkEffectTimer / FIREWORK_EFFECT_DURATION);

            flyBatch.setColor(1, 1, 1, alpha); // Apply fading alpha

            Texture currentFireworkTexture = null;
            // Select firework texture based on direction
            if (speed < 0) { // Flying right-to-left
                currentFireworkTexture = fireworkTexture; // Use FireWorks.png
            } else { // Flying left-to-right
                currentFireworkTexture = fireworkTexture2; // Use FireWorks2.png
            }

            // Ensure the selected firework texture is not null before proceeding
            if (currentFireworkTexture == null) {
                flyBatch.setColor(1, 1, 1, 1); // Reset color
                flyBatch.end();
                return;
            }

            // Scale firework relative to witch size
            float fireworkScaledWidth = currentFireworkTexture.getWidth() * scaleFactor * 0.7f; // Smaller than witch
            float fireworkScaledHeight = currentFireworkTexture.getHeight() * scaleFactor * 0.7f;

            // Reduced yOffsetForBunch to make fireworks closer vertically
            float yOffsetForBunch = fireworkScaledHeight * 0.2f;

            // xOffsetForBunch to place top/bottom fireworks behind the main one
            // This offset is relative to the main firework's position.
            float xOffsetForBunch = fireworkScaledWidth * 0.15f;

            // Draw the main (middle) firework
            flyBatch.draw(currentFireworkTexture, fireworkEffectX, fireworkEffectY, fireworkScaledWidth, fireworkScaledHeight);

            // Draw two additional fireworks, slightly offset to create a bunch effect,
            // always behind the main firework relative to the witch's movement.
            if (speed < 0) { // Flying right-to-left, so "behind" is to the right
                flyBatch.draw(currentFireworkTexture, fireworkEffectX + xOffsetForBunch, fireworkEffectY - yOffsetForBunch, fireworkScaledWidth, fireworkScaledHeight);
                flyBatch.draw(currentFireworkTexture, fireworkEffectX + xOffsetForBunch, fireworkEffectY + yOffsetForBunch, fireworkScaledWidth, fireworkScaledHeight);
            } else { // Flying left-to-right
                flyBatch.draw(currentFireworkTexture, fireworkEffectX - xOffsetForBunch, fireworkEffectY - yOffsetForBunch, fireworkScaledWidth, fireworkScaledHeight);
                flyBatch.draw(currentFireworkTexture, fireworkEffectX - xOffsetForBunch, fireworkEffectY + yOffsetForBunch, fireworkScaledWidth, fireworkScaledHeight);
            }

            flyBatch.setColor(1, 1, 1, 1); // Reset batch color to opaque white for other drawings
        }

        flyBatch.end();
    }

    /**
     * Resizes the camera's viewport when the screen size changes.
     *
     * @param width The new width of the screen.
     * @param height The new height of the screen.
     */
    public void resize(int width, int height) {
        flyCamera.viewportWidth = width;
        flyCamera.viewportHeight = height;
        flyCamera.setToOrtho(false, width, height);
        flyCamera.update();
    }

    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
        if (fireworkTexture != null) {
            fireworkTexture.dispose();
        }
        if (fireworkTexture2 != null) {
            fireworkTexture2.dispose();
        }
        // --- NEW: Dispose background textures ---
        if (leftBackgroundTexture != null) {
            leftBackgroundTexture.dispose();
        }
        if (rightBackgroundTexture != null) {
            rightBackgroundTexture.dispose();
        }
        // --- END NEW ---
        if (flyBatch != null) {
            flyBatch.dispose();
        }
        // Dispose sound effect
        if (flySound != null) {
            flySound.dispose();
        }
    }
}
