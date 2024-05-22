package com.erloo.pixelgame.units.hostile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.erloo.pixelgame.pathfinding.Node;
import com.erloo.pixelgame.Player;
import com.erloo.pixelgame.damage.Damageable;
import com.badlogic.gdx.math.Rectangle;
import com.erloo.pixelgame.pathfinding.Pathfinder;
import com.erloo.pixelgame.pathfinding.Grid;

import java.util.List;

public class Boss extends Enemy implements Damageable {
    private Vector2 position;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;
    private TextureRegion currentFrame;
    private TextureAtlas atlas;
    private float viewRadius;
    private boolean isChasing;
    private Vector2 target;
    private Vector2 spawnPosition;
    private Array<TiledMapTileLayer> collisionLayers;
    private boolean isCollidingWithPlayer;
    private Pathfinder pathfinder;
    private Grid grid;
    private Player player;
    private TextureRegion frontFrame;
    private TextureRegion backFrame;
    private TextureRegion leftFrame;
    private TextureRegion rightFrame;
    private TextureRegion frontAttackFrame;
    private TextureRegion backAttackFrame;
    private TextureRegion leftAttackFrame;
    private TextureRegion rightAttackFrame;
    private float attackTime;
    private float attackInterval; // Интервал между атаками (в секундах)
    private boolean firstCollision;
    public Boss(TextureAtlas atlas, int damage, Vector2 position, Array<TiledMapTileLayer> collisionLayers, Grid grid, Player player) {
        super(damage, player);
        this.position = position;
        this.spawnPosition = position.cpy();
        this.atlas = atlas;
        this.collisionLayers = collisionLayers;
        this.grid = grid;
        this.player = player;
        viewRadius = 100f;
        isChasing = false;
        createAnimations();
        currentAnimation = frontAnimation;
        health = 1000; // устанавливаем максимальное здоровье
        pathfinder = new Pathfinder();
        attackInterval = 1f;
        firstCollision = true;
    }

    public void update(float delta) {
        boolean wasMoving = isMoving();
        isMoving = false;

        setInvulnerable(delta);
        if (!isCollidingWithPlayer) {
            if (isChasing) {
                // Обновляем позицию и анимацию слайма, когда он преследует игрока
                List<Node> path = findPathToPlayer(player);
                if (path != null && !path.isEmpty()) {
                    Node nextNode = path.get(0);
                    Vector2 nextPosition = new Vector2(nextNode.x * 16, nextNode.y * 16);
                    Vector2 direction = nextPosition.cpy().sub(position).nor();
                    float speed = 30f;

                    float newX = position.x;
                    float newY = position.y;

                    newX += direction.x * speed * delta;
                    position.x = newX;

                    newY += direction.y * speed * delta;
                    position.y = newY;

                    if (position.epsilonEquals(nextPosition, 1f)) {
                        path.remove(0);
                    }

                    // Обновляем анимацию в соответствии с направлением, когда слайм преследует игрока
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

                    isMoving = true;
                }
            } else {
                // Обновляем позицию и анимацию слайма, когда он не преследует игрока и не соприкасается с ним
                if (!position.epsilonEquals(spawnPosition, 1f)) {
                    Vector2 direction = spawnPosition.cpy().sub(position).nor();
                    float speed = 30f;

                    float newX = position.x;
                    float newY = position.y;

                    newX += direction.x * speed * delta;
                    position.x = newX;

                    newY += direction.y * speed * delta;
                    position.y = newY;

                    // Обновляем анимацию в соответствии с направлением, когда слайм возвращается на свою исходную позицию
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

                    isMoving = true;
                } else {
                    // Устанавливаем frontAnimation, когда слайм не двигается и не соприкасается с игроком
                    currentAnimation = frontAnimation;
                }
            }
        } else {
            List<Node> path = findPathToPlayer(player);
            if (path != null && !path.isEmpty()) {
                Node nextNode = path.get(0);
                Vector2 nextPosition = new Vector2(nextNode.x * 16, nextNode.y * 16);
                Vector2 direction = nextPosition.cpy().sub(position).nor();

                // Выбираем анимацию атаки в зависимости от направления движения
                if (Math.abs(direction.x) > Math.abs(direction.y)) {
                    if (direction.x > 0) {
                        currentAnimation = rightAttackAnimation;
                    } else if (direction.x < 0) {
                        currentAnimation = leftAttackAnimation;
                    }
                } else {
                    if (direction.y > 0) {
                        currentAnimation = backAttackAnimation;
                    } else if (direction.y < 0) {
                        currentAnimation = frontAttackAnimation;
                    }
                }

                // Обновляем время атаки
                attackTime += delta;

                // Вызываем метод атаки
                attack(player);
            }
        }

        if (wasMoving != isMoving()) {
            stateTime = 0;
        }
    }

    public void render(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();

        if (isCollidingWithPlayer) {
            TextureRegion firstFrame = currentAnimation.getKeyFrame(0);
            if (currentFrame.getRegionX() == firstFrame.getRegionX() &&
                    currentFrame.getRegionY() == firstFrame.getRegionY()) {
                currentFrame = currentAnimation.getKeyFrame(stateTime);
                stateTime += Gdx.graphics.getDeltaTime(); // Добавьте эту строку
            }
        } else {
            // Обновляем анимацию, когда слайм не соприкасается с игроком
            if (isMoving()) {
                currentFrame = currentAnimation.getKeyFrame(stateTime, true);
            } else {
                // Устанавливаем первый кадр frontAnimation, когда слайм не двигается и не соприкасается с игроком
                currentFrame = frontAnimation.getKeyFrame(0);
            }
        }

        int width = currentFrame.getRegionWidth();
        int height = currentFrame.getRegionHeight();

        blinking(batch);
        batch.draw(currentFrame, position.x - width / 2, position.y - height / 2);
    }
    public void attack(Player player) {
        if (isCollidingWithPlayer) {
            if (firstCollision) {
                player.takeDamage(damage);
                firstCollision = false;
            } else if (attackTime >= attackInterval) {
                player.takeDamage(damage);
                attackTime = 0;
            }
        }
    }
    public float getPositionX() {
        return position.x;
    }

    public float getPositionY() {
        return position.y;
    }
    public int getWidth() {
        return currentFrame.getRegionWidth();
    }
    public int getHeight() {
        return currentFrame.getRegionHeight();
    }
    public void checkCollisionWithPlayer(Player player) {
        Rectangle bossRect = getBoundingRectangle();
        Rectangle playerRect = player.getBoundingRectangle();
        if (Intersector.overlaps(bossRect, playerRect)) {
            isCollidingWithPlayer = true;
            isChasing = false;
        } else {
            isCollidingWithPlayer = false;
        }
    }
    public Vector2 getPosition() {
        return position;
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
    @Override
    public void reward(){
        int COIN_REWARD = 100;
        player.getCoins().addCoins(COIN_REWARD);
    }
    public void checkTargetInView(Vector2 target) {
        if (position.dst(target) <= viewRadius) {
            isChasing = true;
            this.target = target;
        } else {
            isChasing = false;
        }
    }
    public int getHealth() {
        return health;
    }

    public Rectangle getBoundingRectangle() {
        return new Rectangle(position.x, position.y, currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
    }
//    public Rectangle getBoundingRectangle() {
//        return new Rectangle(position.x - 8, position.y - 16, currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
//    }

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
        // Инициализируем переменные для хранения первых кадров каждой анимации
        frontFrame = frontFrames.get(0);
        backFrame = backFrames.get(0);
        leftFrame = leftFrames.get(0);
        rightFrame = rightFrames.get(0);

        // Инициализируем переменные для хранения первых кадров каждой анимации атаки
        frontAttackFrame = frontAttackFrames.get(0);
        backAttackFrame = backAttackFrames.get(0);
        leftAttackFrame = leftAttackFrames.get(0);
        rightAttackFrame = rightAttackFrames.get(0);

        currentFrame = frontAnimation.getKeyFrame(0);
    }

    public Vector2 getSpawnPosition() {
        return spawnPosition;
    }

    public void stopMoving() {
        isChasing = false;
    }
    @Override
    public void deathmessage(){
        String message = "Boss is defeated";
        System.out.println(message);
    }
    public List<Node> findPathToPlayer(Player player) {
        int playerGridX = (int) (player.getPosition().x / 16);
        int playerGridY = (int) (player.getPosition().y / 16);
        Node startNode = grid.getNode((int) (position.x / 16), (int) (position.y / 16));
        Node endNode = grid.getNode(playerGridX, playerGridY);
        return pathfinder.findPath(startNode, endNode, grid);
    }
}

