package com.erloo.pixelgame.dialogues;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.erloo.pixelgame.Player;

public class DialogueBox {
    private BitmapFont dialogFont;
    private Dialogue currentDialogue;
    private int selectedOption;
    private ShapeRenderer shapeRenderer;
    private Dialoguable dialoguable;
    private boolean isDialogueOpen;
    private boolean isActive;
    private Player player;

    public DialogueBox(BitmapFont dialogFont, Dialoguable dialoguable, Player player) {
        this.dialogFont = dialogFont;
        this.dialoguable = dialoguable;
        this.player = player;
        selectedOption = 0;
        shapeRenderer = new ShapeRenderer();
        FreeTypeFontGenerator.FreeTypeFontParameter dialog = new FreeTypeFontGenerator.FreeTypeFontParameter();
        dialog.size = 50;
        dialog.color = Color.RED;
    }

    public void setDialogue(Dialogue currentDialogue) {
        this.currentDialogue = currentDialogue;
    }

    public void render(SpriteBatch uiBatch ) {
        Texture dialogBackgroundTexture = new Texture(Gdx.files.internal("dialogbox.png"));
        TextureRegion dialogBackground = new TextureRegion(dialogBackgroundTexture);

//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(Color.DARK_GRAY);
//        shapeRenderer.rect((Gdx.graphics.getWidth() - 400) / 2, (Gdx.graphics.getHeight() - 200) / 2, 400, 200);
//        shapeRenderer.end();

        // Draw the dialogue box background
        uiBatch.draw(dialogBackground, (Gdx.graphics.getWidth() - 400) / 2, (Gdx.graphics.getHeight() - 200) / 2, 400, 200);

        // Draw the dialogue text
        if (currentDialogue != null) {
            dialogFont.setColor(Color.WHITE);
            String[] dialogueLines = currentDialogue.getDialogueText().split("\n");
            for (int i = 0; i < dialogueLines.length; i++) {
                dialogFont.draw(uiBatch, dialogueLines[i], (Gdx.graphics.getWidth() - 400) / 2 + 20, (Gdx.graphics.getHeight() - 200) / 2 + 180 - i * 20);
            }
        }

        // Draw the dialogue options
        if (currentDialogue != null && currentDialogue.getOptions() != null) { // Check for null values
            for (int i = 0; i < currentDialogue.getOptions().size; i++) { // Use currentDialogue.getOptions().size instead of options.size
                DialogueOption option = currentDialogue.getOptions().get(i);
                if (i == selectedOption) {
                    dialogFont.setColor(Color.YELLOW);
                } else {
                    dialogFont.setColor(Color.WHITE);
                }
                dialogFont.draw(uiBatch, option.getOptionText(), (Gdx.graphics.getWidth() - 400) / 2 + 20, (Gdx.graphics.getHeight() - 200) / 2 + 100 - i * 30);
            }
        }

        // Reset the font color
        dialogFont.setColor(Color.WHITE);

    }
    public int getSelectedOption() {
        return selectedOption;
    }
    public void close() {
        isDialogueOpen = false;
    }
    public boolean isDialogueOpen() {
        return isDialogueOpen;
    }
    public boolean isActive() {
        return isActive;
    }
    public void setSelectedOption(int selectedOption) {
        this.selectedOption = selectedOption;
    }
    public void setActive(boolean active) {
        isActive = active;
    }
    public void setDialogueOpen(boolean dialogueOpen) {
        isDialogueOpen = dialogueOpen;
    }
    public void handleInput() {
        player.setPlayerActive(false); // Добавлено

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedOption = (selectedOption + currentDialogue.getOptions().size - 1) % currentDialogue.getOptions().size;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedOption = (selectedOption + 1) % currentDialogue.getOptions().size;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            DialogueOption selectedOption = currentDialogue.getOptions().get(this.selectedOption);
            Dialogue nextDialogue = selectedOption.getNextDialogue();
            if (nextDialogue != null) {
                currentDialogue = nextDialogue;
                this.selectedOption = 0;
                if (!isActive && nextDialogue.getOptions().size > 0) { // Only set isActive to true if it is currently false and there is a valid selected option
                    isActive = true;
                }
            } else {
                setActive(false);
                player.setPlayerActive(true); // Добавлено
            }
        }
    }
}
