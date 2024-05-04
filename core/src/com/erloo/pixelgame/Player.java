package com.erloo.pixelgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Player {
    private Vector2 position;
    private Vector2 velocity;
    private float speed;
    private Sprite sprite;

    public Player(Texture texture) {
        position = new Vector2();
        velocity = new Vector2();
        speed = 100f;
        sprite = new Sprite(texture);
        sprite.setSize(16, 16); // set the size of the sprite to 16x16 pixels
        sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2); // set the origin to the center of the sprite
    }

    public void update(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            velocity.y = speed;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            velocity.y = -speed;
        } else {
            velocity.y = 0;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velocity.x = -speed;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocity.x = speed;
        } else {
            velocity.x = 0;
        }

        position.add(velocity.x * delta, velocity.y * delta);
        sprite.setPosition(position.x, position.y);
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public Vector2 getPosition() {
        return position;
    }
}

