package com.erloo.pixelgame.units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.erloo.pixelgame.Player;

public class Alice extends NPC {
    private Vector2 position;
    private TextureAtlas atlas;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;
    private TextureRegion currentFrame;
    private Animation<TextureRegion> frontAnimation;
    private Player player;
    private final float NEAR_PLAYER_THRESHOLD = 18; // Set the threshold value as per your requirements

    public Alice(TextureAtlas atlas, Vector2 position, Player player) {
        super();
        this.position = position;
        this.atlas = atlas;
        this.player = player;
        createAnimations();
        currentAnimation = frontAnimation;
    }

    public void update(float delta) {
        // Add Alice's update logic.
    }

    public void render(SpriteBatch aliceBatch) {
        stateTime += Gdx.graphics.getDeltaTime();
        currentFrame = currentAnimation.getKeyFrame(stateTime, true);

        int aliceWidth = currentFrame.getRegionWidth();
        int aliceHeight = currentFrame.getRegionHeight();

        aliceBatch.draw(currentFrame, position.x - aliceWidth / 2, position.y - aliceHeight / 2);
    }

    public void interact() {
        //
    }
    public boolean isNearPlayer() {
        return position.dst(player.getPosition()) <= NEAR_PLAYER_THRESHOLD;
    }
    private void createAnimations() {
        Array<TextureAtlas.AtlasRegion> frontFrames = new Array<>();
        frontFrames.add(atlas.findRegion("front"));
        frontAnimation = new Animation<>(0.3f, frontFrames, Animation.PlayMode.LOOP);
        currentFrame = frontAnimation.getKeyFrame(0);
    }
}
