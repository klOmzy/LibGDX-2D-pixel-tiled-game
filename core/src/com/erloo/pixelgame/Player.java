package com.erloo.pixelgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
    private TiledMapTileLayer collisionLayer;
    private OrthographicCamera camera;

    public Player(TextureAtlas atlas, TiledMapTileLayer collisionLayer, OrthographicCamera camera) {
        this.atlas = atlas;
        this.collisionLayer = collisionLayer;
        this.camera = camera;
        position = new Vector2(0, 0);
        stateTime = 0;

        createAnimations();
        currentAnimation = leftAnimation;

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
        int cellX = (int) (x / 16);
        int cellY = (int) (y / 16);
        TiledMapTileLayer.Cell cell = collisionLayer.getCell(cellX, cellY);
        return cell != null && cell.getTile() != null;
    }


    public void update(float delta, float mapWidth, float mapHeight) {

        boolean isMoving = false;


        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            if (!isCellOccupied(position.x, position.y + 100 * delta)) {
                currentAnimation = backAnimation;
                position.y += 100 * delta;
                isMoving = true;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            if (!isCellOccupied(position.x, position.y - 100 * delta)) {
                currentAnimation = frontAnimation;
                position.y -= 100 * delta;
                isMoving = true;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            if (!isCellOccupied(position.x - 100 * delta, position.y)) {
                currentAnimation = leftAnimation;
                position.x -= 100 * delta;
                isMoving = true;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            if (!isCellOccupied(position.x + 100 * delta, position.y)) {
                currentAnimation = rightAnimation;
                position.x += 100 * delta;
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