package com.badlogic.exercise;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

class Cloud {
    Texture texture;
    float x, y, speed;
    float width, height;

    public Cloud(Texture texture) {
        this.texture = texture;
        width = 4;
        height = 2;
        reset();
    }

    public void update(float delta) {
        x -= speed * delta;
        if (x + width < -16) {
            reset();
            x = 16;
        }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }

    public void reset() {
        x = MathUtils.random(0, 32);
        y = MathUtils.random(5, 8);
        speed = MathUtils.random(0.5f, 1.5f);
    }
}

public class CloudManager implements Disposable {
    private final Array<Cloud> clouds;
    private final Texture cloudTexture;

    public CloudManager() {
        cloudTexture = new Texture("cloud.png");
        clouds = new Array<>();
        for (int i = 0; i < 5; i++) {
            clouds.add(new Cloud(cloudTexture));
        }
    }

    public void update(float delta) {
        for (Cloud cloud : clouds) {
            cloud.update(delta);
        }
    }

    public void draw(SpriteBatch batch) {
        for (Cloud cloud : clouds) {
            cloud.draw(batch);
        }
    }

    @Override
    public void dispose() {
        cloudTexture.dispose();
    }
}
