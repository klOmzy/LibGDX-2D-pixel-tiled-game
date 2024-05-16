package com.erloo.pixelgame.dialogues;

import com.badlogic.gdx.utils.Array;

public class Dialogue {
    private String dialogueText;
    private Array<DialogueOption> options;

    public Dialogue(String dialogueText, Array<DialogueOption> options) {
        this.dialogueText = dialogueText;
        this.options = options;
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

