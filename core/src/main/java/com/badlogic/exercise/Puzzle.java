package com.badlogic.exercise;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Puzzle {

    private final float WORLD_WIDTH = 600;
    private final float WORLD_HEIGHT = 800;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Texture backgroundFrameTexture;
    private Texture draggableItemTexture;

    private Array<DraggableItem> draggableItems;
    private Array<DropZone> dropZones;
    private boolean isPuzzleSolved = false;


    private DraggableItem draggedItem = null;
    private float dragOffsetX, dragOffsetY;

    public Puzzle(float worldWidth, float worldHeight, Texture draggableItemTexture, Texture backgroundFrameTexture) {
        this.draggableItemTexture = draggableItemTexture;
        this.backgroundFrameTexture = backgroundFrameTexture;

        draggableItems = new Array<>();
        dropZones = new Array<>();


        float itemWidth = WORLD_WIDTH * 0.52f;
        float itemHeight = WORLD_HEIGHT * 0.58f;
        draggableItems.add(new DraggableItem(WORLD_WIDTH / 2 - itemWidth / 2, WORLD_HEIGHT * 0.001f, itemWidth, itemHeight, new TextureRegion(draggableItemTexture)));

        float zoneWidth = WORLD_WIDTH * 0.55f;
        float zoneHeight = WORLD_HEIGHT * 0.08f;
        dropZones.add(new DropZone(WORLD_WIDTH / 2 - zoneWidth / 2, WORLD_HEIGHT * 0.42f, zoneWidth, zoneHeight));
    }

    public void initializeRenderers(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        this.batch = batch;
        this.shapeRenderer = shapeRenderer;
    }

    public void update(float deltaTime) {
        // No poison image updates needed anymore
    }

    public void render() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (DropZone zone : dropZones) {
            if (zone.isFilled()) {
                shapeRenderer.setColor(zone.getFilledColor());
            } else if (zone.isHovered()) {
                shapeRenderer.setColor(zone.getHoveredColor());
            } else {
                shapeRenderer.setColor(zone.getFillColor());
            }
            shapeRenderer.rect(zone.bounds.x, zone.bounds.y, zone.bounds.width, zone.bounds.height);
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GREEN);
        for (DropZone zone : dropZones) {
            shapeRenderer.rect(zone.bounds.x, zone.bounds.y, zone.bounds.width, zone.bounds.height);
        }
        shapeRenderer.end();

        batch.begin();
        for (DraggableItem item : draggableItems) {
            batch.draw(item.textureRegion, item.bounds.x, item.bounds.y, item.bounds.width, item.bounds.height);
        }
        batch.end();
    }

    public boolean handleTouchDown(float worldX, float worldY) {
        Gdx.app.log("DragDropGameLogic", "Touch Down at World: (" + String.format("%.2f", worldX) + ", " + String.format("%.2f", worldY) + ")");

        for (DraggableItem item : draggableItems) {
            boolean isItemInAnyFilledZone = false;
            for (DropZone zone : dropZones) {
                if (zone.isFilled() && zone.getContainedItem() == item) {
                    isItemInAnyFilledZone = true;
                    break;
                }
            }

            if (item.bounds.contains(worldX, worldY) && !isItemInAnyFilledZone) {
                draggedItem = item;
                dragOffsetX = worldX - item.bounds.x;
                dragOffsetY = worldY - item.bounds.y;

                for (DropZone zone : dropZones) {
                    if (zone.getContainedItem() == item) {
                        zone.setFilled(false);
                        zone.setContainedItem(null);
                        break;
                    }
                }

                Gdx.app.log("DragDropGameLogic", "Started dragging item.");
                return true;
            }
        }
        return false;
    }

    public boolean handleTouchDragged(float worldX, float worldY) {
        if (draggedItem != null) {
            draggedItem.bounds.x = worldX - dragOffsetX;
            draggedItem.bounds.y = worldY - dragOffsetY;

            for (DropZone zone : dropZones) {
                if (zone.bounds.overlaps(draggedItem.bounds) && !zone.isFilled()) {
                    zone.setHovered(true);
                } else {
                    zone.setHovered(false);
                }
            }

            return true;
        }
        return false;
    }

    public boolean handleTouchUp(float worldX, float worldY) {
        if (draggedItem != null) {
            boolean droppedInZone = false;
            for (DropZone zone : dropZones) {
                zone.setHovered(false);
                if (zone.bounds.overlaps(draggedItem.bounds) && !zone.isFilled()) {
                    draggedItem.bounds.x = zone.bounds.x + (zone.bounds.width - draggedItem.bounds.width) / 2;
                    draggedItem.bounds.y = zone.bounds.y + (zone.bounds.height - draggedItem.bounds.height) / 2;
                    zone.setFilled(true);
                    zone.setContainedItem(draggedItem);
                    droppedInZone = true;
                    showMessage("Item dropped!");
                    isPuzzleSolved = true;
                    break;
                }
            }

            if (!droppedInZone) {
                draggedItem.resetPosition();
                Gdx.app.log("DragDropGameLogic", "Item dropped outside valid zone, reverting position.");
            }

            draggedItem = null;
            return true;
        }
        return false;
    }

    public boolean handleKeyPress(int keycode) {
        // No poison logic here anymore
        return false;
    }

    public boolean isCompleted() {
        return isPuzzleSolved; // or whatever your puzzle completion flag is
    }

    public void resetGame() {
        for (DraggableItem item : draggableItems) {
            for (DropZone zone : dropZones) {
                if (zone.getContainedItem() == item) {
                    zone.setFilled(false);
                    zone.setContainedItem(null);
                    break;
                }
            }
            item.resetPosition();
        }
        showMessage("Game has been reset!");
    }

    private void showMessage(String msg) {
        Gdx.app.log("DragDropGameLogic", msg);
    }

    private static class DraggableItem {
        Rectangle bounds;
        TextureRegion textureRegion;
        float initialX, initialY;

        public DraggableItem(float x, float y, float width, float height, TextureRegion textureRegion) {
            this.bounds = new Rectangle(x, y, width, height);
            this.initialX = x;
            this.initialY = y;
            this.textureRegion = textureRegion;
        }

        public void resetPosition() {
            bounds.x = initialX;
            bounds.y = initialY;
        }
    }

    private static class DropZone {
        Rectangle bounds;
        private boolean filled = false;
        private boolean hovered = false;
        private DraggableItem containedItem = null;

        private final Color fillColor = Color.BLACK;
        private final Color hoveredColor = new Color(0.76f, 0.88f, 0.79f, 1);
        private final Color filledColor = new Color(0.65f, 0.85f, 0.71f, 1);

        public DropZone(float x, float y, float width, float height) {
            this.bounds = new Rectangle(x, y, width, height);
        }

        public boolean isFilled() { return filled; }
        public void setFilled(boolean filled) { this.filled = filled; }
        public boolean isHovered() { return hovered; }
        public void setHovered(boolean hovered) { this.hovered = hovered; }
        public DraggableItem getContainedItem() { return containedItem; }
        public void setContainedItem(DraggableItem item) { this.containedItem = item; }

        public Color getFillColor() { return fillColor; }
        public Color getHoveredColor() { return hoveredColor; }
        public Color getFilledColor() { return filledColor; }
    }
}
