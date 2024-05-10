package com.erloo.pixelgame.units.hostile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.erloo.pixelgame.damage.Damageable;
import com.badlogic.gdx.math.Rectangle;

public class Slime extends Enemy implements Damageable {
    private Vector2 position;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;
    private TextureRegion currentFrame;
    private TextureAtlas atlas;
    private float viewRadius;
    private boolean isChasing;
    private Vector2 target;
    private int health;
    private boolean isDead;
    private boolean isBlinking;
    private float blinkTimer;
    private float blinkDuration; // Длительность мигания (например, 0.1 секунды)
    private float blinkInterval; // Интервал между миганиями (например, 0.1 секунды)
    private boolean isInvulnerable;
    private float invulnerabilityTimer;
    private float invulnerabilityDuration;
    private Vector2 spawnPosition;
    private Array<TiledMapTileLayer> collisionLayers;

    public Slime(TextureAtlas atlas, int damage, Vector2 position, Array<TiledMapTileLayer> collisionLayers) {
        super(damage);
        this.position = position;
        this.spawnPosition = position.cpy(); // сохраняем начальную позицию спавна
        this.atlas = atlas;
        this.collisionLayers = collisionLayers;
        viewRadius = 100f; // Задайте нужное значение радиуса обзора
        isChasing = false;
        createAnimations();
        currentAnimation = frontAnimation;
        health = 30; // Задайте начальное значение здоровья

    }

    public void update(float delta) {
        invulnerabilityDuration = 0.5f;
        if (isInvulnerable) {
            invulnerabilityTimer += delta;
            if (invulnerabilityTimer >= invulnerabilityDuration) {
                isInvulnerable = false;
            }
        }

        if (isChasing) {
            Vector2 direction = target.cpy().sub(position).nor();
            float speed = 50f; // Set the desired speed

            float newX = position.x + direction.x * speed * delta;
            float newY = position.y + direction.y * speed * delta;

            if (!isCellOccupied(newX, newY)) {
                position.x = newX;
                position.y = newY;
            }

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

    public void render(SpriteBatch slimeBatch) {
        stateTime += Gdx.graphics.getDeltaTime();
        currentFrame = currentAnimation.getKeyFrame(stateTime, true);

        int slimeWidth = currentFrame.getRegionWidth();
        int slimeHeight = currentFrame.getRegionHeight();
        blinkDuration = 0.02f;
        blinkInterval = 0.1f;

        if (isBlinking) {
            blinkTimer += Gdx.graphics.getDeltaTime();
            if (blinkTimer > blinkDuration * 8) { // Умножим blinkDuration на количество миганий (в этом случае 5)
                isBlinking = false;
                // Сбросить цвет batch в исходное состояние
                slimeBatch.setColor(Color.WHITE);
            } else if (Math.floor(blinkTimer / (blinkDuration + blinkInterval)) % 2 == 0) { // Проверим, следует ли отображать красный цвет или белый
                // Установить красный цвет для мигания
                slimeBatch.setColor(Color.RED);
            } else {
                // Сбросить цвет batch в исходное состояние
                slimeBatch.setColor(Color.WHITE);
            }
        } else {
            // Убедитесь, что цвет batch сброшен в исходное состояние, когда нет мигания
            slimeBatch.setColor(Color.WHITE);
        }
        slimeBatch.draw(currentFrame, position.x - slimeWidth / 2, position.y - slimeHeight / 2);
    }
    public boolean isCellOccupied(float x, float y) {
        for (TiledMapTileLayer layer : collisionLayers) {
            int cellX = (int) (x / 16);
            int cellY = (int) (y / 16);
            if (cellX >= 0 && cellX < layer.getWidth() && cellY >= 0 && cellY < layer.getHeight()) {
                TiledMapTileLayer.Cell cell = layer.getCell(cellX, cellY);
                if (cell != null && cell.getTile() != null) {
                    return true;
                }
            }
        }
        return false;
    }
    public void checkTargetInView(Vector2 target) {
        if (position.dst(target) <= viewRadius) {
            isChasing = true;
            this.target = target;
        } else {
            isChasing = false;
        }
    }

    public Rectangle getBoundingRectangle() {
        return new Rectangle(position.x, position.y, currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
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
    public Vector2 getSpawnPosition() {
        return spawnPosition;
    }
    public boolean isMoving() {
        return isChasing;
    }
    public void stopMoving() {
        isChasing = false;
    }
    @Override
    public void takeDamage(int damage) {
        if (!isInvulnerable) {
            health -= damage; // Уменьшаем здоровье на полученный урон
            isBlinking = true;
            blinkTimer = 0;
            isInvulnerable = true;
            invulnerabilityTimer = 0;
            if (health <= 0) {
                System.out.println("Slime is dead");
                isDead = true;
            }
        }
    }
    public boolean isDead() {
        return isDead;
    }
    public int getHealth() {
        return health;
    }
}