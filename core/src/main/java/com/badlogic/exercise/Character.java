package com.badlogic.exercise;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound; // Import for sound
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Character {
    // Animation fields
    private Animation<TextureRegion> walkAnimation;
    private TextureRegion[] walkFrames;
    private TextureRegion idleFrame; // A dedicated frame for standing still
    private float stateTime;

    // Sound fields
    private Sound jumpSound;
    private Sound hitSound;

    // State and Physics fields
    public Vector2 position;
    public Vector2 velocity;
    public Rectangle bounds;
    private boolean facingRight = true; // To track the character's direction
    public boolean isJumping = false;

    // Constants
    private final float frogWidth = 0.8f;
    private final float frogHeight = 0.8f;
    private final float gravity = -20f;
    private final float jumpSpeed = 8f;
    public final float groundLevel = 0.8f;
    private final float acceleration = 15f;
    private final float deceleration = 20f;
    private final float maxSpeed = 3f;
    public final float SCROLL_TRIGGER_X = 4.0f;

    public Character() {
        // Load dedicated idle frame from hurt1.png
        Texture idleTexture = new Texture("hurt1.png");
        idleFrame = new TextureRegion(idleTexture);

        // Load walk frames
        Texture walk1 = new Texture("walk1.png");
        Texture walk2 = new Texture("walk2.png");
        Texture walk3 = new Texture("walk3.png");
        Texture walk4 = new Texture("walk4.png");

        walkFrames = new TextureRegion[4];
        walkFrames[0] = new TextureRegion(walk1);
        walkFrames[1] = new TextureRegion(walk2);
        walkFrames[2] = new TextureRegion(walk3);
        walkFrames[3] = new TextureRegion(walk4);

        walkAnimation = new Animation<TextureRegion>(0.1f, walkFrames);
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);

        stateTime = 0f;

        position = new Vector2(3.5f, groundLevel);
        velocity = new Vector2();
        bounds = new Rectangle(position.x, position.y, frogWidth, frogHeight);

        // Load sound effects
        // The volume (1.0f) is set to be "a little higher than theme song"
        // assuming the theme song is played at a lower volume.
        try {
            jumpSound = Gdx.audio.newSound(Gdx.files.internal("jump.mp3"));
        } catch (Exception e) {
            Gdx.app.error("Character", "Failed to load jump.mp3: " + e.getMessage());
            // Handle error, e.g., by setting jumpSound to null or using a default sound
        }
        try {
            hitSound = Gdx.audio.newSound(Gdx.files.internal("hit.mp3"));
        } catch (Exception e) {
            Gdx.app.error("Character", "Failed to load hit.mp3: " + e.getMessage());
            // Handle error
        }
    }

    public void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            velocity.x += acceleration * Gdx.graphics.getDeltaTime();
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            velocity.x -= acceleration * Gdx.graphics.getDeltaTime();
        } else {
            // Decelerate if no horizontal key is pressed
            if (Math.abs(velocity.x) > 0.1f) { // Only decelerate if there's significant velocity
                velocity.x += (velocity.x > 0 ? -1 : 1) * deceleration * Gdx.graphics.getDeltaTime();
            } else {
                velocity.x = 0; // Stop completely if velocity is very low
            }
        }

        velocity.x = MathUtils.clamp(velocity.x, -maxSpeed, maxSpeed);

        // Jump logic
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && position.y <= groundLevel + 0.01f) {
            velocity.y = jumpSpeed;
            isJumping = true;
            if (jumpSound != null) {
                jumpSound.play(1.0f); // Play jump sound at full volume
            }
        }
    }

    public void update(float delta, GameLevel game) {
        // Only update the animation timer if the character is moving horizontally.
        if (Math.abs(velocity.x) > 0.1f) {
            stateTime += delta;
        }

        // Update facing direction based on velocity
        if (velocity.x > 0.1f) {
            facingRight = true;
        } else if (velocity.x < -0.1f) {
            facingRight = false;
        }

        // Apply gravity if above ground
        if (position.y > groundLevel) {
            velocity.y += gravity * delta;
        } else {
            isJumping = false;
        }

        // Handle horizontal scrolling based on character position
        if (velocity.x > 0 && position.x >= SCROLL_TRIGGER_X) {
            game.setTileScrollX(game.getTileScrollX() + velocity.x * delta);
            game.setBackgroundScrollX(game.getBackgroundScrollX() + 0.5f * velocity.x * delta);
        } else if (velocity.x < 0 && game.getTileScrollX() > 0 && position.x <= SCROLL_TRIGGER_X) {
            game.setTileScrollX(game.getTileScrollX() + velocity.x * delta);
            game.setBackgroundScrollX(game.getBackgroundScrollX() + 0.5f * velocity.x * delta);
        } else {
            position.x += velocity.x * delta;
        }

        // Apply vertical velocity
        position.y += velocity.y * delta;

        // Prevent falling below ground level
        if (position.y < groundLevel) {
            position.y = groundLevel;
            velocity.y = 0;
        }

        // Update bounds for collision detection
        bounds.set(position.x, position.y, frogWidth, frogHeight);
    }

    public void draw(SpriteBatch spriteBatch, float invincibilityTime) {
        // Handle blinking (for invincibility)
        if (invincibilityTime > 0 && ((int) (invincibilityTime * 10) % 2 != 0)) {
            return; // skip rendering for blink effect
        }

        TextureRegion currentFrame;

        // If moving, get a frame from the walk animation.
        // If not moving, use the single, dedicated idle frame.
        if (Math.abs(velocity.x) > 0.1f) {
            currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        } else {
            currentFrame = idleFrame;
        }

        // Flip the frame based on the last direction the character was moving.
        if (facingRight && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (!facingRight && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }

        spriteBatch.draw(currentFrame, position.x, position.y, frogWidth, frogHeight);
    }

    /**
     * Call this method when the character is hit by an obstacle or enemy.
     */
    public void onHit() {
        if (hitSound != null) {
            hitSound.play(1.0f); // Play hit sound at full volume
        }
        // Add any other logic for when the character is hit (e.g., reduce health, start invincibility)
        // For example:
        // game.characterHit(); // Assuming GameLevel has a method to handle character hits
    }

    public void reset() {
        position.set(3.5f, groundLevel);
        velocity.set(0, 0);
        isJumping = false;
        facingRight = true;
    }

    public void dispose() {
        // Dispose all loaded textures
        if (idleFrame != null && idleFrame.getTexture() != null) {
            idleFrame.getTexture().dispose();
        }
        if (walkFrames != null) {
            for (TextureRegion frame : walkFrames) {
                if (frame != null && frame.getTexture() != null) {
                    frame.getTexture().dispose();
                }
            }
        }
        // Dispose sound effects
        if (jumpSound != null) {
            jumpSound.dispose();
        }
        if (hitSound != null) {
            hitSound.dispose();
        }
    }
}
