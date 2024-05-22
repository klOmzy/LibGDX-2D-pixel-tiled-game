package com.erloo.pixelgame.dialogues;

import com.badlogic.gdx.utils.Array;

public class Dialogue {
    private String dialogueText;
    private Array<DialogueOption> options;
    private float dialogueVerticalOffset;
    private float optionsVerticalOffset;

    public Dialogue(String dialogueText, Array<DialogueOption> options, float dialogueVerticalOffset, float optionsVerticalOffset) {
        this.dialogueText = dialogueText;
        this.options = options;
        this.dialogueVerticalOffset = dialogueVerticalOffset;
        this.optionsVerticalOffset = optionsVerticalOffset;
    }

    public float getDialogueVerticalOffset() {
        return dialogueVerticalOffset;
    }
    public float getOptionsVerticalOffset() {
        return optionsVerticalOffset;
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

