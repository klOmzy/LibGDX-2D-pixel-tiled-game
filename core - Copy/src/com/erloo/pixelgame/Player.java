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
    private TextureRegion currentFrame; // Добавляем переменную currentFrame

    public Player(TextureAtlas atlas, float mapWidth, float mapHeight) {
        this.atlas = atlas;
        position = new Vector2(mapWidth / 2, mapHeight / 2); // Устанавливаем начальную позицию игрока в центре карты
        stateTime = 0;

        createAnimations();
        currentAnimation = leftAnimation;
    }

    public void update(float delta, float mapWidth, float mapHeight) {
        boolean isMoving = false;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            currentAnimation = backAnimation;
            position.y += 100 * delta;
            isMoving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            currentAnimation = frontAnimation;
            position.y -= 100 * delta;
            isMoving = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            currentAnimation = leftAnimation;
            position.x -= 100 * delta;
            isMoving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            currentAnimation = rightAnimation;
            position.x += 100 * delta;
            isMoving = true;
        }
        // Ограничиваем движение игрока внутри границ карты
        if (position.x < 0) {
            position.x = 0;
        }
        if (position.x > mapWidth - 16) {
            position.x = mapWidth - 16;
        }
        if (position.y < 0) {
            position.y = 0;
        }
        if (position.y > mapHeight - 16) {
            position.y = mapHeight - 16;
        }

        if (isMoving) {
            // Если игрок движется, то обновляем позицию анимации
            stateTime += delta;
            currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        } else {
            // Если игрок не движется, то отображаем первый кадр анимации
            stateTime = 0;
            currentFrame = currentAnimation.getKeyFrame(0, false);
        }
    }

    public void render(SpriteBatch batch) {
        if (currentFrame != null) {
            batch.draw(currentFrame, position.x, position.y);
        }
    }

    public Vector2 getPosition() {
        return position;
    }

    private Animation<TextureRegion> leftAnimation;
    private Animation<TextureRegion> rightAnimation;
    private Animation<TextureRegion> backAnimation;
    private Animation<TextureRegion> frontAnimation;

    private void createAnimations() {
        Array<TextureAtlas.AtlasRegion> leftFrames = atlas.findRegions("left");        leftFrames.add(atlas.findRegion("left"));
        leftFrames.add(atlas.findRegion("left1"));
        leftFrames.add(atlas.findRegion("left2"));
        leftFrames.add(atlas.findRegion("left3"));
        leftAnimation = new Animation<>(0.3f, leftFrames, Animation.PlayMode.LOOP);

        Array<TextureAtlas.AtlasRegion> rightFrames = atlas.findRegions("right");        rightFrames.add(atlas.findRegion("right"));
        rightFrames.add(atlas.findRegion("right1"));
        rightFrames.add(atlas.findRegion("right2"));
        rightFrames.add(atlas.findRegion("right3"));
        rightAnimation = new Animation<>(0.3f, rightFrames, Animation.PlayMode.LOOP);

        Array<TextureAtlas.AtlasRegion> backFrames = atlas.findRegions("back");        backFrames.add(atlas.findRegion("back"));
        backFrames.add(atlas.findRegion("back1"));
        backFrames.add(atlas.findRegion("back2"));
        backFrames.add(atlas.findRegion("back3"));
        backAnimation = new Animation<>(0.3f, backFrames, Animation.PlayMode.LOOP);

        Array<TextureAtlas.AtlasRegion> frontFrames = atlas.findRegions("front");        frontFrames.add(atlas.findRegion("front"));
        frontFrames.add(atlas.findRegion("front1"));
        frontFrames.add(atlas.findRegion("front2"));
        frontFrames.add(atlas.findRegion("front3"));
        frontAnimation = new Animation<>(0.3f, frontFrames, Animation.PlayMode.LOOP);

    }
}


