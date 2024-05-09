package com.erloo.pixelgame.units.hostile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.erloo.pixelgame.damage.Damageable;

public class Slime extends Enemy implements Damageable {
    private Vector2 position;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;
    private TextureRegion currentFrame;
    private TextureAtlas atlas;
    private float viewRadius;
    private boolean isChasing;
    private Vector2 target;

    public Slime(TextureAtlas atlas, int damage, Vector2 position) {
        super(damage);
        this.position = position;
        this.atlas = atlas;
        viewRadius = 100f; // Задайте нужное значение радиуса обзора
        isChasing = false;
        createAnimations();
        currentAnimation = frontAnimation;
    }

    public void checkTargetInView(Vector2 target) {
        if (position.dst(target) <= viewRadius) {
            isChasing = true;
            this.target = target;
        } else {
            isChasing = false;
        }
    }

    public void update(float delta) {
        if (isChasing) {
            Vector2 direction = target.cpy().sub(position).nor();
            float speed = 50f; // Set the desired speed

            position.x += direction.x * speed * delta;
            position.y += direction.y * speed * delta;

            // Update the current animation based on the direction of movement
            if (direction.x > 0 && direction.y > 0) {
                currentAnimation = rightAnimation;
            } else if (direction.x < 0 && direction.y > 0) {
                currentAnimation = leftAnimation;
            } else if (direction.x > 0 && direction.y < 0) {
                currentAnimation = rightAnimation;
            } else if (direction.x < 0 && direction.y < 0) {
                currentAnimation = leftAnimation;
            } else if (direction.x > 0) {
                currentAnimation = rightAnimation;
            } else if (direction.x < 0) {
                currentAnimation = leftAnimation;
            } else if (direction.y > 0) {
                currentAnimation = backAnimation;
            } else if (direction.y < 0) {
                currentAnimation = frontAnimation;
            }
        }
    }

    private Animation<TextureRegion> leftAnimation;
    private Animation<TextureRegion> rightAnimation;
    private Animation<TextureRegion> backAnimation;
    private Animation<TextureRegion> frontAnimation;

    private void createAnimations() {
        Array<TextureAtlas.AtlasRegion> leftFrames = new Array<>();
        leftFrames.add(atlas.findRegion("left1"));
        leftFrames.add(atlas.findRegion("left2"));
        leftAnimation = new Animation<>(0.3f, leftFrames, Animation.PlayMode.LOOP);

        Array<TextureAtlas.AtlasRegion> rightFrames = new Array<>();
        rightFrames.add(atlas.findRegion("right1"));
        rightFrames.add(atlas.findRegion("right2"));
        rightAnimation = new Animation<>(0.3f, rightFrames, Animation.PlayMode.LOOP);

        Array<TextureAtlas.AtlasRegion> backFrames = new Array<>();
        backFrames.add(atlas.findRegion("back1"));
        backFrames.add(atlas.findRegion("back2"));
        backAnimation = new Animation<>(0.3f, backFrames, Animation.PlayMode.LOOP);

        Array<TextureAtlas.AtlasRegion> frontFrames = new Array<>();
        frontFrames.add(atlas.findRegion("front1"));
        frontFrames.add(atlas.findRegion("front2"));
        frontAnimation = new Animation<>(0.3f, frontFrames, Animation.PlayMode.LOOP);
    }

    public void render(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        currentFrame = currentAnimation.getKeyFrame(stateTime, true);

        int slimeWidth = currentFrame.getRegionWidth();
        int slimeHeight = currentFrame.getRegionHeight();

        batch.draw(currentFrame, position.x - slimeWidth / 2, position.y - slimeHeight / 2);
    }


    @Override
    public void takeDamage(int damage) {
        // Реализация получения урона
    }
}
