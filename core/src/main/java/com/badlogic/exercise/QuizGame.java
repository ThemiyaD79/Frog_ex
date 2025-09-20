package com.badlogic.exercise;

import com.badlogic.gdx.Game;

public class QuizGame extends Game {
    @Override
    public void create() {
        setScreen(new QuizScreen1());
    }
}
