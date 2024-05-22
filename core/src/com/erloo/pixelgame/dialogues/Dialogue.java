package com.erloo.pixelgame.dialogues;

import com.badlogic.gdx.utils.Array;

public class Dialogue {
    private String dialogueText;
    private Array<DialogueOption> options;
    private float dialogueHorizontalOffset;
    private float dialogueVerticalOffset;
    private float dialogueBoxHorizontalOffset;
    private float dialogueBoxVerticalOffset;
    private float optionsHorizontalOffset;
    private float optionsVerticalOffset;


    public Dialogue(String dialogueText, Array<DialogueOption> options, float dialogueHorizontalOffset, float dialogueVerticalOffset, float optionsHorizontalOffset, float optionsVerticalOffset , float dialogueBoxHorizontalOffset, float dialogueBoxVerticalOffset) {
        this.dialogueText = dialogueText;
        this.options = options;
        this.dialogueHorizontalOffset = dialogueHorizontalOffset;
        this.dialogueVerticalOffset = dialogueVerticalOffset;
        this.optionsHorizontalOffset = optionsHorizontalOffset;
        this.optionsVerticalOffset = optionsVerticalOffset;
        this.dialogueBoxHorizontalOffset = dialogueBoxHorizontalOffset;
        this.dialogueBoxVerticalOffset = dialogueBoxVerticalOffset;
    }
    public float getDialogueBoxHorizontalOffset() {
        return dialogueBoxHorizontalOffset;
    }
    public float getDialogueBoxVerticalOffset() {
        return dialogueBoxVerticalOffset;
    }
    public float getDialogueVerticalOffset() {
        return dialogueVerticalOffset;
    }
    public float getDialogueHorizontalOffset() {
        return dialogueHorizontalOffset;
    }
    public float getOptionsVerticalOffset() {
        return optionsVerticalOffset;
    }
    public float getOptionsHorizontalOffset() {
        return optionsHorizontalOffset;
    }
    public String getDialogueText() {
        return dialogueText;
    }

    public void setDialogueText(String dialogueText) {
        this.dialogueText = dialogueText;
    }

    public Array<DialogueOption> getOptions() {
        return options;
    }

    public void setOptions(Array<DialogueOption> options) {
        this.options = options;
    }
}

