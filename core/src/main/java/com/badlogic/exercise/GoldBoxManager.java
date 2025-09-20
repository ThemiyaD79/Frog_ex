package com.badlogic.exercise;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class GoldBoxManager {
    private final Texture goldBoxTexture;
    private final ArrayList<GoldBox> goldBoxes;

    private float nextSpawnX = 0f;
    private static final float SPAWN_INTERVAL_DISTANCE = 50f;

    public static class GoldBox {
        public Vector2 position;
        public Rectangle bounds;

        public GoldBox(Vector2 position) {
            this.position = position;
            // FIX: Made the box smaller
            this.bounds = new Rectangle(position.x, position.y, 0.6f, 0.6f);
        }
    }

    public GoldBoxManager() {
        goldBoxTexture = new Texture("GoldBox.png");
        goldBoxes = new ArrayList<>();
    }

    public void update(float tileScrollX) {
        if (tileScrollX >= nextSpawnX) {
            spawnGoldBox(tileScrollX);
            nextSpawnX += SPAWN_INTERVAL_DISTANCE;
        }

        for (GoldBox box : goldBoxes) {
            // FIX: Updated bounds to match new smaller size
            box.bounds.set(box.position.x - tileScrollX, box.position.y, 0.6f, 0.6f);
        }
    }

    private void spawnGoldBox(float tileScrollX) {
        float spawnX = tileScrollX + 15f;
        // FIX: Lowered the random height range for spawning
        float spawnY = MathUtils.random(1.8f, 2.8f);
        goldBoxes.add(new GoldBox(new Vector2(spawnX, spawnY)));
    }

    public void draw(SpriteBatch spriteBatch, float tileScrollX) {
        for (GoldBox g : goldBoxes) {
            // FIX: Draw the box smaller
            spriteBatch.draw(goldBoxTexture, g.position.x - tileScrollX, g.position.y, 0.6f, 0.6f);
        }
    }

    public ArrayList<GoldBox> getGoldBoxes() {
        return goldBoxes;
    }

    public void reset(ArrayList<Display.Tree> trees) {
        goldBoxes.clear();
        nextSpawnX = 0f;
    }

    public void dispose() {
        goldBoxTexture.dispose();
    }
}
