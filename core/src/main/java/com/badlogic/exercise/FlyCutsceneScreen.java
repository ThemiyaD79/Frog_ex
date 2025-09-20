package com.badlogic.exercise;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

public class FlyCutsceneScreen extends ScreenAdapter {
    private final GameLevel game;
    private Fly flyingWitch;
    private float cutsceneTime = 0f;
    private float cutsceneDuration = 2f; // seconds
    private boolean finished = false;

    public FlyCutsceneScreen(GameLevel game) {
        this.game = game;
        // Initialize here so it's always ready
        flyingWitch = new Fly("Wh.png", 150f, Gdx.graphics.getHeight() / 2f);
    }

    @Override
    public void show() {
        // No need to initialize flyingWitch here anymore
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        cutsceneTime += delta;

        if (flyingWitch != null) {
            flyingWitch.update(delta);
            flyingWitch.draw();
        }

        // End cutscene after duration or on user input
        if (!finished && (cutsceneTime > cutsceneDuration ||
            Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER) ||
            Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE))) {
            finished = true;
        }
    }

    public boolean isFinished() {
        return finished;
    }

    @Override
    public void resize(int width, int height) {
        if (flyingWitch != null) {
            flyingWitch.resize(width, height);
        }
    }

    @Override
    public void dispose() {
        if (flyingWitch != null) {
            flyingWitch.dispose();
        }
    }
}
