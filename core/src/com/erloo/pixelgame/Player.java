package com.erloo.pixelgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Player {
    private Vector2 position;
    private TextureAtlas atlas;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;
    private TextureRegion currentFrame; // Добавляем переменную currentFrame
    private OrthographicCamera camera;
    private Array<TiledMapTileLayer> collisionLayers;

    public Player(TextureAtlas atlas, Array<TiledMapTileLayer> collisionLayers, OrthographicCamera camera, float spawnX, float spawnY) {
        this.atlas = atlas;
        this.collisionLayers = collisionLayers;
        this.camera = camera;
        position = new Vector2(0, 0);
        this.position.set(spawnX, spawnY);

        stateTime = 0;

        createAnimations();
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

    public void centerCamera() {
        float cameraX = position.x + currentFrame.getRegionWidth() / 2f;
        float cameraY = position.y + currentFrame.getRegionHeight() / 2f;
        camera.position.set(cameraX, cameraY, 0);
        camera.update();
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

    public void update(float delta, float mapWidth, float mapHeight) {

        boolean isMoving = false;
        float speed = 100 * delta;

        float moveX = 0;
        float moveY = 0;

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

        // Normalize diagonal movement
        if (moveX != 0 && moveY != 0) {
            float norm = (float) Math.sqrt(2) / 2;
            moveX *= norm;
            moveY *= norm;
        }

        position.x += moveX;
        position.y += moveY;

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
        float width = currentFrame.getRegionWidth();
        float height = currentFrame.getRegionHeight();
        float halfWidth = width / 2f;
        float halfHeight = height / 2f;
        batch.draw(currentFrame, position.x - halfWidth, position.y - halfHeight, halfWidth, halfHeight, width, height, 1, 1, 0);
    }

    public Vector2 getPosition() {
        return position;
    }

    private Animation<TextureRegion> leftAnimation;
    private Animation<TextureRegion> rightAnimation;
    private Animation<TextureRegion> backAnimation;
    private Animation<TextureRegion> frontAnimation;

    private void createAnimations() {
        Array<TextureAtlas.AtlasRegion> leftFrames = new Array<>();
        leftFrames.add(atlas.findRegion("left"));
        leftFrames.add(atlas.findRegion("left1"));
        leftFrames.add(atlas.findRegion("left2"));
        leftFrames.add(atlas.findRegion("left3"));
        leftAnimation = new Animation<>(0.3f, leftFrames, Animation.PlayMode.LOOP);

        Array<TextureAtlas.AtlasRegion> rightFrames = new Array<>();
        rightFrames.add(atlas.findRegion("right"));
        rightFrames.add(atlas.findRegion("right1"));
        rightFrames.add(atlas.findRegion("right2"));
        rightFrames.add(atlas.findRegion("right3"));
        rightAnimation = new Animation<>(0.3f, rightFrames, Animation.PlayMode.LOOP);

        Array<TextureAtlas.AtlasRegion> backFrames = new Array<>();
        backFrames.add(atlas.findRegion("back"));
        backFrames.add(atlas.findRegion("back1"));
        backFrames.add(atlas.findRegion("back2"));
        backFrames.add(atlas.findRegion("back3"));
        backAnimation = new Animation<>(0.3f, backFrames, Animation.PlayMode.LOOP);

        Array<TextureAtlas.AtlasRegion> frontFrames = new Array<>();
        frontFrames.add(atlas.findRegion("front"));
        frontFrames.add(atlas.findRegion("front1"));
        frontFrames.add(atlas.findRegion("front2"));
        frontFrames.add(atlas.findRegion("front3"));
        frontAnimation = new Animation<>(0.3f, frontFrames, Animation.PlayMode.LOOP);
    }
}