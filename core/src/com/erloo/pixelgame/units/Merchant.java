package com.erloo.pixelgame.units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.erloo.pixelgame.*;
import com.erloo.pixelgame.dialogues.*;

public class Merchant extends NPC {
    private Vector2 position;
    private TextureAtlas atlas;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;
    private TextureRegion currentFrame;
    private Animation<TextureRegion> frontAnimation;
    private Player player;
    private final float NEAR_PLAYER_THRESHOLD = 18; // Set the threshold value as per your requirements
    private Dialogue currentDialogue;
    private boolean isDialogueOpen;
    private DialogueBox dialogueBox;
    private DialogueManager dialogueManager;
    private Coin playerCoins;

    public Merchant(TextureAtlas atlas, Vector2 position, Player player, BitmapFont dialogFont, DialogueBox  dialogueBox, DialogueManager dialogueManager,  Coin playerCoins) {
        super();
        this.position = position;
        this.atlas = atlas;
        this.player = player;
        this.dialogueBox = dialogueBox;
        this.dialogueManager = dialogueManager;
        this.playerCoins = playerCoins;
        createAnimations();
        currentAnimation = frontAnimation;
        isDialogueOpen = false;

    }
    public void update(float delta) {
        // Add merchant's update logic.
    }

    public void render(SpriteBatch merchantBatch) {
        stateTime += Gdx.graphics.getDeltaTime();
        currentFrame = currentAnimation.getKeyFrame(stateTime, true);

        int merchantWidth = currentFrame.getRegionWidth();
        int merchantHeight = currentFrame.getRegionHeight();

        merchantBatch.draw(currentFrame, position.x - merchantWidth / 2, position.y - merchantHeight / 2);
    }
    public void interact() {
        if (!isActive()) { // Only start the dialogue if it is not already active
            startDialogue();
            setActive(true); // Set the isActive flag to true
        }
    }
    public boolean isActive() {
        return dialogueBox.isActive();
    }

    public void setActive(boolean active) {
        dialogueBox.setActive(active);
    }

    public DialogueBox getDialogueBox() {
        return dialogueBox;
    }

    public void startDialogue() {
        currentDialogue = dialogueManager.getDialogue("merchant1");
        dialogueBox.setDialogue(currentDialogue); // Устанавливаем текущий диалог в объекте DialogueBox
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
