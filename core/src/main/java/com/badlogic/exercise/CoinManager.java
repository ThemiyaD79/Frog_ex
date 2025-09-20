package com.badlogic.exercise;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class CoinManager {
    private final Texture coinTexture;
    private final ArrayList<Coin> coins;

    // Timer variables for automatic coin spawning
    private float spawnTimer = 0f;
    // Spawn more frequently (every 0.5 seconds instead of 1.0)
    private float spawnInterval = 0.5f;  // Changed from 1.0f to 0.5f

    public static class Coin {
        private Vector2 position;
        private Rectangle bounds;
        private boolean collected;
        private float collectedTime;
        private float floatOffset;

        public Coin(Vector2 position, Texture coinTexture) {
            this.position = position;
            this.bounds = new Rectangle(position.x, position.y, 0.5f, 0.5f);
            this.collected = false;
            this.collectedTime = 0f;
            this.floatOffset = 0f;
        }

        public void update(float delta, float tileScrollX) {
            if (collected) {
                collectedTime += delta;
                if (collectedTime < 0.5f) {
                    floatOffset = collectedTime * 1.5f;
                } else {
                    floatOffset = 1f;
                }
            }

            bounds.setPosition(position.x - tileScrollX, position.y + floatOffset);
        }

        public void draw(SpriteBatch batch, Texture texture, float tileScrollX) {
            if (!collected || collectedTime < 0.6f) {
                batch.draw(texture, position.x - tileScrollX, position.y + floatOffset, 0.5f, 0.5f);
            }
        }

        public void collect() {
            if (!collected) {
                collected = true;
                collectedTime = 0f;
            }
        }

        public boolean isCollected() {
            return collected;
        }

        public float getCollectedTime() {
            return collectedTime;
        }

        public Rectangle getBounds() {
            return bounds;
        }

        public Vector2 getPosition() {
            return position;
        }
    }

    public CoinManager(GoldBoxManager goldBoxManager) {
        coinTexture = new Texture("Coin.png");
        coins = new ArrayList<>();

        // Initialize coins on top of gold boxes
        for (GoldBoxManager.GoldBox goldBox : goldBoxManager.getGoldBoxes()) {
            Vector2 coinPos = new Vector2(goldBox.position.x, goldBox.position.y + 1.8f);
            coins.add(new Coin(coinPos, coinTexture));
        }
    }

    public void update(float tileScrollX, float delta) {
        for (Coin coin : coins) {
            coin.update(delta, tileScrollX);
        }
        removeCollectedCoins();

        // Handle timed automatic coin spawning
        spawnTimer += delta;
        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0f;

            // Spawn larger clusters: 5 to 8 coins (was 3 to 5)
            int clusterSize = MathUtils.random(5, 8);  // Changed from 3-5 to 5-8

            // Spawn coins further ahead to fill more horizontal space
            float startX = tileScrollX + MathUtils.random(10f, 15f); // Random start x between 10 and 15 instead of fixed 12

            // Spawn coins over a wider vertical range
            float startY = MathUtils.random(1.0f, 4.0f); // Changed range from 1.5-3.0 to 1.0-4.0

            for (int i = 0; i < clusterSize; i++) {
                // Slightly varied wave pattern for more natural coin layout
                float offsetX = i * 0.6f + MathUtils.random(-0.2f, 0.2f);
                float offsetY = (float)Math.sin(i * 0.7f) * 0.6f + MathUtils.random(-0.1f, 0.1f);
                Vector2 spawnPos = new Vector2(startX + offsetX, startY + offsetY);
                addCoin(spawnPos);
            }
        }
    }

    public void draw(SpriteBatch spriteBatch, float tileScrollX) {
        for (Coin coin : coins) {
            coin.draw(spriteBatch, coinTexture, tileScrollX);
        }
    }

    public int checkPlayerCollision(Rectangle playerBounds) {
        int collectedCount = 0;
        for (Coin coin : coins) {
            if (!coin.isCollected() && coin.getBounds().overlaps(playerBounds)) {
                coin.collect();
                collectedCount++;
            }
        }
        return collectedCount;
    }

    public void addCoin(Vector2 position) {
        coins.add(new Coin(position, coinTexture));
    }

    private void removeCollectedCoins() {
        coins.removeIf(coin -> coin.isCollected() && coin.getCollectedTime() >= 0.6f);
    }

    public ArrayList<Coin> getCoins() {
        return coins;
    }

    public void dispose() {
        coinTexture.dispose();
    }
}
