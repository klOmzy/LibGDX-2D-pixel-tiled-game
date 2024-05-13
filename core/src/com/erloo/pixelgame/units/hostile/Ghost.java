package com.erloo.pixelgame.units.hostile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.erloo.pixelgame.Player;
import com.erloo.pixelgame.damage.Damageable;

public class Ghost extends Enemy implements Damageable {
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
    private boolean isCollidingWithPlayer;
    public Ghost(TextureAtlas atlas, int damage, Vector2 position, Array<TiledMapTileLayer> collisionLayers) {
        super(damage);
        this.position = position;
        this.spawnPosition = position.cpy(); // сохраняем начальную позицию спавна
        this.atlas = atlas;
        this.collisionLayers = collisionLayers;
        viewRadius = 50f; // Задайте нужное значение радиуса обзора
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
        if (!isCollidingWithPlayer) {
            if (isChasing) {
                Vector2 direction = target.cpy().sub(position).nor();
                float speed = 40f; // Set the desired speed

                float newX = position.x + direction.x * speed * delta;
                float newY = position.y + direction.y * speed * delta;

                if (!isCellOccupied(newX, newY)) {
                    position.x = newX;
                    position.y = newY;
                } else {
                    System.out.println("Slime hit an obstacle!"); // Add this line
                }

                // Update the current animation based on the direction of movement
                if (Math.abs(direction.x) > Math.abs(direction.y)) {
                    if (direction.x > 0) {
                        currentAnimation = rightAnimation;
                    } else if (direction.x < 0) {
                        currentAnimation = leftAnimation;
                    }
                } else {
                    if (direction.y > 0) {
                        currentAnimation = backAnimation;
                    } else if (direction.y < 0) {
                        currentAnimation = frontAnimation;
                    }
                }
            } else {
                if (!position.epsilonEquals(spawnPosition, 1f)) { // Add this check
                    Vector2 direction = spawnPosition.cpy().sub(position).nor();
                    float speed = 40f; // Set the desired speed

                    float newX = position.x + direction.x * speed * delta;
                    float newY = position.y + direction.y * speed * delta;

                    if (!isCellOccupied(newX, newY)) {
                        position.x = newX;
                        position.y = newY;
                    }

                    // Update the current animation based on the direction of movement
                    if (Math.abs(direction.x) > Math.abs(direction.y)) {
                        if (direction.x > 0) {
                            currentAnimation = rightAnimation;
                        } else if (direction.x < 0) {
                            currentAnimation = leftAnimation;
                        }
                    } else {
                        if (direction.y > 0) {
                            currentAnimation = backAnimation;
                        } else if (direction.y < 0) {
                            currentAnimation = frontAnimation;
                        }
                    }
                } else {
                    // Slime has reached the spawn point, stop moving
                    currentAnimation = frontAnimation; // Set the default animation
                }
            }
        }
    }

    public void render(SpriteBatch ghostBatch) {
        stateTime += Gdx.graphics.getDeltaTime();
        currentFrame = currentAnimation.getKeyFrame(stateTime, true);

        int ghostWidth = currentFrame.getRegionWidth();
        int ghostHeight = currentFrame.getRegionHeight();
        blinkDuration = 0.02f;
        blinkInterval = 0.1f;

        if (isBlinking) {
            blinkTimer += Gdx.graphics.getDeltaTime();
            if (blinkTimer > blinkDuration * 8) { // Умножим blinkDuration на количество миганий (в этом случае 5)
                isBlinking = false;
                // Сбросить цвет batch в исходное состояние
                ghostBatch.setColor(Color.WHITE);
            } else if (Math.floor(blinkTimer / (blinkDuration + blinkInterval)) % 2 == 0) { // Проверим, следует ли отображать красный цвет или белый
                // Установить красный цвет для мигания
                ghostBatch.setColor(Color.RED);
            } else {
                // Сбросить цвет batch в исходное состояние
                ghostBatch.setColor(Color.WHITE);
            }
        } else {
            // Убедитесь, что цвет batch сброшен в исходное состояние, когда нет мигания
            ghostBatch.setColor(Color.WHITE);
        }
        ghostBatch.draw(currentFrame, position.x - ghostWidth / 2, position.y - ghostHeight / 2);
    }
    public void checkCollisionWithPlayer(Player player) {
        Rectangle slimeRect = getBoundingRectangle();
        Rectangle playerRect = player.getBoundingRectangle();
        if (Intersector.overlaps(slimeRect, playerRect)) {
            isCollidingWithPlayer = true;
        } else {
            isCollidingWithPlayer = false;
        }
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
        currentFrame = frontAnimation.getKeyFrame(0);
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
                System.out.println("Ghost is dead");
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
