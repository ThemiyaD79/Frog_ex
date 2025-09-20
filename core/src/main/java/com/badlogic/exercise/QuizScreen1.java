// --- QuizScreen.java ---
package com.badlogic.exercise;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class QuizScreen1 implements Screen {
    private Stage stage;
    private boolean completed = false;
    private boolean restartClicked = false;
    private boolean exitClicked = false;

    public QuizScreen1() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label label = new Label("Quiz Completed!", skin);
        label.setColor(Color.YELLOW);

        TextButton restartBtn = new TextButton("Restart Quiz", skin);
        TextButton exitBtn = new TextButton("Exit to Menu", skin);

        restartBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                restartClicked = true;
                completed = true;
            }
        });

        exitBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                exitClicked = true;
                completed = true;
            }
        });

        table.add(label).padBottom(20).row();
        table.add(restartBtn).padBottom(10).row();
        table.add(exitBtn);
    }

    public void reset() {
        completed = false;
        restartClicked = false;
        exitClicked = false;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isRestartClicked() {
        return restartClicked;
    }

    public boolean isExitClicked() {
        return exitClicked;
    }

    public Stage getStage() {
        return stage;
    }

    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}
