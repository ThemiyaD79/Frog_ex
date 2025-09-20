package com.badlogic.exercise;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;

public class Display {
    private final Texture backgroundTexture;
    private final Texture stoneTexture;
    private final Texture grass1;
    private final Texture[] treeTextures;
    private final ArrayList<Tree> trees;

    private final Texture coinTexture;
    private final ArrayList<Coin> coins;
    private final Texture marbleTopTexture;
    private final Texture[] tileTextures;
    private final Texture planetTexture;

    private float lastTreeX = 0;
    private static final float WORLD_VIEW_WIDTH = 12;

    public static class Tree {
        public final Texture texture;
        public final Vector2 position;
        public final Rectangle bounds;

        public Tree(Texture texture, Vector2 position) {
            this.texture = texture;
            this.position = position;
            this.bounds = new Rectangle(position.x, position.y, 0.5f, 0.5f);
        }
    }

    public static class Coin {
        public final Vector2 position;
        public final Rectangle bounds;
        private boolean collected;

        public Coin(Vector2 position) {
            this.position = position;
            this.bounds = new Rectangle(position.x, position.y, 0.5f, 0.5f);
            this.collected = false;
        }

        public void draw(SpriteBatch batch, Texture texture, float tileScrollX) {
            if (!collected) {
                batch.draw(texture, position.x - tileScrollX, position.y, 0.5f, 0.5f);
            }
        }

        public Rectangle getBounds() {
            return bounds;
        }

        public boolean isCollected() {
            return collected;
        }

        public void collect() {
            collected = true;
        }
    }

    public Display() {
        backgroundTexture = new Texture("background.png");
        stoneTexture = new Texture("stone.png");
        grass1 = new Texture("tile_0018.png");
        Texture tree1 = new Texture("Tree (1).png");
        Texture tree2 = new Texture("Tree (2).png");
        treeTextures = new Texture[]{tree1, tree2};

        marbleTopTexture = new Texture("Cloud.png");
        Texture tile2 = new Texture("Cloud.png");
        tileTextures = new Texture[]{tile2};

        coinTexture = new Texture("coin.png");
        planetTexture = new Texture("planet.png");

        trees = new ArrayList<>();
        coins = new ArrayList<>();

        generateInitialTrees();
    }

    private void generateInitialTrees() {
        lastTreeX = 6f;
        while (lastTreeX < WORLD_VIEW_WIDTH) {
            generateTreeCluster();
        }
    }

    private void generateTreeCluster() {
        boolean placeDouble = MathUtils.randomBoolean(0.3f);
        int count = placeDouble ? 2 : 1;

        for (int i = 0; i < count; i++) {
            Texture texture = treeTextures[MathUtils.random(treeTextures.length - 1)];
            Vector2 treePos = new Vector2(lastTreeX, 0.9f);
            trees.add(new Tree(texture, treePos));

            if (MathUtils.randomBoolean(0.33f)) {
                int coinSetSize = MathUtils.random(1, 3);
                for (int c = 0; c < coinSetSize; c++) {
                    float offsetX = c * 0.6f;
                    coins.add(new Coin(new Vector2(treePos.x + offsetX, treePos.y + 1.5f)));
                }
            }

            lastTreeX += (i == 0 && count == 2) ? 0.3f : 0.5f;
        }
        lastTreeX += MathUtils.random(3f, 4f);
    }

    public void update(float tileScrollX) {
        if (lastTreeX < tileScrollX + WORLD_VIEW_WIDTH) {
            generateTreeCluster();
        }

        trees.removeIf(tree -> tree.position.x < tileScrollX - 5);
        coins.removeIf(coin -> coin.position.x < tileScrollX - 5);
    }

    public void draw(SpriteBatch spriteBatch, FitViewport viewport, float backgroundScrollX, float tileScrollX, CloudManager cloudManager) {
        float ww = viewport.getWorldWidth();
        float wh = viewport.getWorldHeight();

        // Background
        for (float x = -backgroundScrollX % ww - ww; x < ww * 2; x += ww) {
            spriteBatch.draw(backgroundTexture, x, 0, ww, wh);
        }

        // Top cloud layer (marble texture)
        float marbleTileWidth = 1.5f;
        float marbleTileHeight = 1.5f;
        for (float x = -backgroundScrollX % marbleTileWidth - marbleTileWidth; x < ww + marbleTileWidth; x += marbleTileWidth) {
            spriteBatch.draw(marbleTopTexture, x, wh - marbleTileHeight, marbleTileWidth, marbleTileHeight);
        }

        // Planet on the left side of the cloud
        spriteBatch.draw(planetTexture, 0.3f, wh - marbleTileHeight + 0.1f, 1f, 1f);

        // Lower decorative tile2 bar (below cloud layer, tiled like clouds)
        float barY = wh - marbleTileHeight - 0.1f;
        float tileWidth = 1f;
        float tileHeight = 1f;
        Texture tile2 = tileTextures[0];

        for (float x = -backgroundScrollX % tileWidth - tileWidth; x < ww + tileWidth; x += tileWidth) {
            spriteBatch.draw(tile2, x, barY, tileWidth, tileHeight);
        }

        // Clouds
        if (cloudManager != null) {
            cloudManager.draw(spriteBatch);
        }

        // Ground
        for (float x = -tileScrollX % 1; x < ww + 1; x += 0.5f) {
            spriteBatch.draw(stoneTexture, x, 0, 0.7f, 0.7f);
            spriteBatch.draw(grass1, x, 0.5f, 0.5f, 0.5f);
        }

        // Trees
        for (Tree t : trees) {
            spriteBatch.draw(t.texture, t.position.x - tileScrollX, t.position.y, 0.5f, 0.5f);
        }

        // Coins
        for (Coin c : coins) {
            c.draw(spriteBatch, coinTexture, tileScrollX);
        }
    }

    public ArrayList<Tree> getTrees() {
        return trees;
    }

    public ArrayList<Coin> getCoins() {
        return coins;
    }

    public void reset() {
        trees.clear();
        coins.clear();
        generateInitialTrees();
    }

    public void dispose() {
        backgroundTexture.dispose();
        stoneTexture.dispose();
        grass1.dispose();
        coinTexture.dispose();
        marbleTopTexture.dispose();
        planetTexture.dispose();
        for (Texture texture : treeTextures) {
            texture.dispose();
        }
        for (Texture tile : tileTextures) {
            tile.dispose();
        }
    }
}
