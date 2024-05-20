package com.erloo.pixelgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.erloo.pixelgame.damage.Damageable;
import com.erloo.pixelgame.damage.Healable;
import com.erloo.pixelgame.damage.Health;
import com.erloo.pixelgame.units.Merchant;

public class Player implements Damageable, Healable {
    private Vector2 position;
    private TextureAtlas atlas;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;
    private TextureRegion currentFrame; // Добавляем переменную currentFrame
    private OrthographicCamera camera;
    private Array<TiledMapTileLayer> collisionLayers;
    private boolean isAttacking;
    private Animation<TextureRegion> previousAnimation;
    private Array<Animation<TextureRegion>> attackAnimations;
    private int attackAnimationIndex;
    private float attackCooldown; // New variable to keep track of the attack cooldown
    private Health health;
    private boolean isDead;
    private float deathTimer;
    private float spawnX;
    private float spawnY;
    private boolean isBlinking;
    private float blinkTimer;
    private float blinkDuration; // Длительность мигания (например, 0.1 секунды)
    private float blinkInterval; // Интервал между миганиями (например, 0.1 секунды)
    private boolean isInvulnerable;
    private float invulnerabilityTimer;
    private float invulnerabilityDuration;
    private int damage; // добавляем переменную damage
    private float moveX;
    private float moveY;
    private Vector2 previousPosition = new Vector2();
    boolean isPlayerActive = true;
    private Coin coins;
    private int numHealthPotions;
    public Player(TextureAtlas atlas, Array<TiledMapTileLayer> collisionLayers, OrthographicCamera camera, float spawnX, float spawnY) {
        this.atlas = atlas;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.collisionLayers = collisionLayers;
        this.camera = camera;
        this.damage = 5; // инициализируем переменную damage значением 10 (например)
        this.coins = new Coin();
        position = new Vector2(0, 0);
        this.position.set(spawnX, spawnY);
        health = new Health(100); // устанавливаем максимальное здоровье

        numHealthPotions = 0;

        attackCooldown = 0; // Initialize the attack cooldown to 0
        stateTime = 0;

        attackAnimations = new Array<>();
        attackAnimationIndex = 0;

        createAnimations();
        previousAnimation = frontAnimation;
        currentAnimation = frontAnimation;

        // Вычисляем ширину и высоту текстуры персонажа
        TextureRegion currentFrame = frontAnimation.getKeyFrame(0); // Выбираем любой регион из анимации, чтобы получить размер текстуры
        float textureWidth = currentFrame.getRegionWidth();
        float textureHeight = currentFrame.getRegionHeight();

        // Вычисляем смещение позиции персонажа относительно центра его текстуры
        float offsetX = (textureWidth - 16) / 2f; // 16 - это ширина тайла, которую мы использовали при создании карты
        float offsetY = (textureHeight - 16) / 2f;

        // Применяем смещение при установке позиции персонажа
        position.x -= offsetX;
        position.y -= offsetY;
    }
    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }
    public void update(float delta, float mapWidth, float mapHeight) {
        previousPosition.set(position);
        boolean isMoving = false;

        float speed = 80 * delta;
        invulnerabilityDuration = 1f;

        moveX = 0;
        moveY = 0;

        if (isInvulnerable) {
            invulnerabilityTimer += delta;
            if (invulnerabilityTimer >= invulnerabilityDuration) {
                isInvulnerable = false;
            }
        }

        if (!isDead) {
            if (isPlayerActive) {
                if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                    if (!isCellOccupied(position.x, position.y + speed)) {
                        currentAnimation = backAnimation;
                        moveY += speed;
                        isMoving = true;
                    }
                }
                if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                    if (!isCellOccupied(position.x, position.y - speed)) {
                        currentAnimation = frontAnimation;
                        moveY -= speed;
                        isMoving = true;
                    }
                }
                if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                    if (!isCellOccupied(position.x - speed, position.y)) {
                        currentAnimation = leftAnimation;
                        moveX -= speed;
                        isMoving = true;
                    }
                }
                if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                    if (!isCellOccupied(position.x + speed, position.y)) {
                        currentAnimation = rightAnimation;
                        moveX += speed;
                        isMoving = true;
                    }
                }

                if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && attackCooldown <= 0) {
                    isAttacking = true;
                    previousAnimation = currentAnimation;
                    attackAnimationIndex = 0;

                    // Clear the attackAnimations array and add the appropriate attack animations based on the current direction
                    attackAnimations.clear();
                    if (previousAnimation == leftAnimation) {
                        attackAnimations.add(leftAttackAnimation);
                    } else if (previousAnimation == rightAnimation) {
                        attackAnimations.add(rightAttackAnimation);
                    } else if (previousAnimation == backAnimation) {
                        attackAnimations.add(backAttackAnimation);
                    } else if (previousAnimation == frontAnimation) {
                        attackAnimations.add(frontAttackAnimation);
                    }

                    attackCooldown = 0.30f; // Set the attack cooldown to 0.3 seconds

                    // Reset the walking animation
                    stateTime = 0;
                    isMoving = false;
                }

                // Update the attack cooldown
                if (attackCooldown > 0) {
                    attackCooldown -= delta;
                    moveX = 0;
                    moveY = 0;
                }

                health.regenerate(delta);

                if (isAttacking) {
                    currentAnimation = attackAnimations.get(attackAnimationIndex);

                    stateTime += delta;
                    currentFrame = currentAnimation.getKeyFrame(stateTime, false);

                    if (currentAnimation.isAnimationFinished(stateTime)) {
                        attackAnimationIndex++;

                        if (attackAnimationIndex >= attackAnimations.size) {
                            isAttacking = false;
                            currentAnimation = previousAnimation;
                            stateTime = 0;
                        }
                    }
                } else {
                    // The player is not attacking, so update the animation based on movement
                    if (isMoving) {
                        stateTime += delta;
                        currentFrame = currentAnimation.getKeyFrame(stateTime, true);
                    } else {
                        stateTime = 0;
                        currentFrame = currentAnimation.getKeyFrame(0, false);
                    }
                }

                position.x += moveX;
                position.y += moveY;

                // Normalize diagonal movement
                if (moveX != 0 && moveY != 0) {
                    float norm = (float) Math.sqrt(2) / 2;
                    moveX *= norm;
                    moveY *= norm;
                }
            }
        } else {
            deathTimer -= delta;
            if (deathTimer <= 0) {
                isDead = false;
                health.heal(100); // Восстанавливаем здоровье до 100 HP
            }
        }
    }

    public void render(SpriteBatch batch) {
        float width = currentFrame.getRegionWidth();
        float height = currentFrame.getRegionHeight();
        float halfWidth = width / 2f;
        float halfHeight = height / 2f;
        blinkDuration = 0.02f;
        blinkInterval = 0.1f;

        if (isBlinking) {
            blinkTimer += Gdx.graphics.getDeltaTime();
            if (blinkTimer > blinkDuration * 8) { // Умножим blinkDuration на количество миганий (в этом случае 5)
                isBlinking = false;
                // Сбросить цвет batch в исходное состояние
                batch.setColor(Color.WHITE);
            } else if (Math.floor(blinkTimer / (blinkDuration + blinkInterval)) % 2 == 0) { // Проверим, следует ли отображать красный цвет или белый
                // Установить красный цвет для мигания
                batch.setColor(Color.RED);
            } else {
                // Сбросить цвет batch в исходное состояние
                batch.setColor(Color.WHITE);
            }
        } else {
            // Убедитесь, что цвет batch сброшен в исходное состояние, когда нет мигания
            batch.setColor(Color.WHITE);
        }
        batch.draw(currentFrame, position.x - halfWidth, position.y - halfHeight, halfWidth, halfHeight, width, height, 1, 1, 0);
    }
    public void setPlayerActive(boolean isPlayerActive) {
        this.isPlayerActive = isPlayerActive;
    }
    public void addHealthPotion() {
        numHealthPotions++;
    }
    public int getNumHealthPotions() {
        return numHealthPotions;
    }
    public void upgradeDamage() {
        this.damage += 5;
    }

    public void upgradeMaxHealth() {
        health.setMaxHealth(health.getMaxHealth() + 25);
        health.heal(25);
    }
    public void useHealthPotion() {
        if (numHealthPotions > 0) {
            numHealthPotions--;
            new HealthPotion().use(this);
        }
    }
    public Coin getCoins() {
        return coins;
    }
    public boolean isPlayerActive() {
        return isPlayerActive;
    }

    public void centerCamera() {
        float cameraX = position.x;
        float cameraY = position.y;
        camera.position.set(cameraX, cameraY, 0);
        camera.update();
    }
    @Override
    public void takeDamage(int damage) {
        if (!isInvulnerable) {
            health.takeDamage(damage);
            isBlinking = true;
            blinkTimer = 0;
            isInvulnerable = true;
            invulnerabilityTimer = 0;
            if (health.getCurrentHealth() == 0) {
                position.set(spawnX, spawnY);
                currentAnimation = frontAnimation;
                stateTime = 0; // Сбросить stateTime, чтобы начать анимацию с начала
                currentFrame = currentAnimation.getKeyFrame(stateTime, true); // Обновить currentFrame для новой анимации
                isDead = true;
                deathTimer = 3; // Показывать сообщение о смерти в течение 3 секунд
            }
        }
    }

    public int getDamage() {
        return damage; // предполагая, что damage - это переменная, содержащая количество урона, которое игрок наносит
    }

    public boolean isDead() {
        return isDead;
    }

    @Override
    public void heal(int amount) {
        health.heal(amount);
    }

    public int getHealth() {
        return health.getCurrentHealth();
    }

    public int getMaxHealth() {
        return health.getMaxHealth();
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

    public Rectangle getBoundingRectangle() {
        return new Rectangle(position.x, position.y, currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
    }
    public void stopMoving() {
        moveX = 0;
        moveY = 0;
    }
    public Vector2 getPosition() {
        return position;
    }

    private Animation<TextureRegion> leftAnimation;
    private Animation<TextureRegion> rightAnimation;
    private Animation<TextureRegion> backAnimation;
    private Animation<TextureRegion> frontAnimation;
    private Animation<TextureRegion> leftAttackAnimation;
    private Animation<TextureRegion> rightAttackAnimation;
    private Animation<TextureRegion> backAttackAnimation;
    private Animation<TextureRegion> frontAttackAnimation;


    private void createAnimations() {
        Array<TextureAtlas.AtlasRegion> leftFrames = new Array<>();
        leftFrames.add(atlas.findRegion("left1"));
        leftFrames.add(atlas.findRegion("left2"));
        leftFrames.add(atlas.findRegion("left3"));
        leftFrames.add(atlas.findRegion("left4"));
        leftAnimation = new Animation<>(0.3f, leftFrames, Animation.PlayMode.LOOP);

        Array<TextureAtlas.AtlasRegion> rightFrames = new Array<>();
        rightFrames.add(atlas.findRegion("right1"));
        rightFrames.add(atlas.findRegion("right2"));
        rightFrames.add(atlas.findRegion("right3"));
        rightFrames.add(atlas.findRegion("right4"));
        rightAnimation = new Animation<>(0.3f, rightFrames, Animation.PlayMode.LOOP);

        Array<TextureAtlas.AtlasRegion> backFrames = new Array<>();
        backFrames.add(atlas.findRegion("back1"));
        backFrames.add(atlas.findRegion("back2"));
        backFrames.add(atlas.findRegion("back3"));
        backFrames.add(atlas.findRegion("back4"));
        backAnimation = new Animation<>(0.3f, backFrames, Animation.PlayMode.LOOP);

        Array<TextureAtlas.AtlasRegion> frontFrames = new Array<>();
        frontFrames.add(atlas.findRegion("front1"));
        frontFrames.add(atlas.findRegion("front2"));
        frontFrames.add(atlas.findRegion("front3"));
        frontFrames.add(atlas.findRegion("front4"));
        frontAnimation = new Animation<>(0.3f, frontFrames, Animation.PlayMode.LOOP);

        Array<TextureAtlas.AtlasRegion> leftAttackFrames = new Array<>();
        leftAttackFrames.add(atlas.findRegion("leftattack2"));
        leftAttackFrames.add(atlas.findRegion("leftattack3"));
        leftAttackFrames.add(atlas.findRegion("leftattack4"));
        leftAttackAnimation = new Animation<>(0.1f, leftAttackFrames, Animation.PlayMode.NORMAL);

        Array<TextureAtlas.AtlasRegion> rightAttackFrames = new Array<>();
        rightAttackFrames.add(atlas.findRegion("rightattack2"));
        rightAttackFrames.add(atlas.findRegion("rightattack3"));
        rightAttackFrames.add(atlas.findRegion("rightattack4"));
        rightAttackAnimation = new Animation<>(0.1f, rightAttackFrames, Animation.PlayMode.NORMAL);

        Array<TextureAtlas.AtlasRegion> backAttackFrames = new Array<>();
        backAttackFrames.add(atlas.findRegion("backattack2"));
        backAttackFrames.add(atlas.findRegion("backattack3"));
        backAttackFrames.add(atlas.findRegion("backattack4"));
        backAttackAnimation = new Animation<>(0.1f, backAttackFrames, Animation.PlayMode.NORMAL);

        Array<TextureAtlas.AtlasRegion> frontAttackFrames = new Array<>();
        frontAttackFrames.add(atlas.findRegion("frontattack2"));
        frontAttackFrames.add(atlas.findRegion("frontattack3"));
        frontAttackFrames.add(atlas.findRegion("frontattack4"));
        frontAttackAnimation = new Animation<>(0.1f, frontAttackFrames, Animation.PlayMode.NORMAL);

    }
}