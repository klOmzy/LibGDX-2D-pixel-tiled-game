package com.erloo.pixelgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Player {
    private Vector2 position;
    private TextureAtlas atlas;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;

    public Player(TextureAtlas atlas) {
        this.atlas = atlas;
        position = new Vector2(0, 0);
        stateTime = 0;

        createAnimations();
        currentAnimation = leftAnimation;
    }

    public void update(float delta) {
        stateTime += delta;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            currentAnimation = backAnimation;
            position.y += 100 * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            currentAnimation = frontAnimation;
            position.y -= 100 * delta;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            currentAnimation = leftAnimation;
            position.x -= 100 * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            currentAnimation = rightAnimation;
            position.x += 100 * delta;
        }
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, position.x, position.y);
    }

    public Vector2 getPosition() {
        return position;
    }

    private Animation<TextureRegion> leftAnimation;
    private Animation<TextureRegion> rightAnimation;
    private Animation<TextureRegion> backAnimation;
    private Animation<TextureRegion> frontAnimation;

    private void createAnimations() {
        Array<TextureAtlas.AtlasRegion> leftFrames = atlas.findRegions("left");
        leftAnimation = new Animation<>(0.1f, leftFrames, Animation.PlayMode.LOOP);

        Array<TextureAtlas.AtlasRegion> rightFrames = atlas.findRegions("right");
        rightAnimation = new Animation<>(0.1f, rightFrames, Animation.PlayMode.LOOP);

        Array<TextureAtlas.AtlasRegion> backFrames = atlas.findRegions("back");
        backAnimation = new Animation<>(0.1f, backFrames, Animation.PlayMode.LOOP);

        Array<TextureAtlas.AtlasRegion> frontFrames = atlas.findRegions("front");
        frontAnimation = new Animation<>(0.1f, frontFrames, Animation.PlayMode.LOOP);
    }
}


