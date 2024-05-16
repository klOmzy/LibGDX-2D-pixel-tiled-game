package com.erloo.pixelgame.dialogues;

import java.util.HashMap;

public class DialogueManager {
    private HashMap<String, Dialogue> dialogues;

    public DialogueManager() {
        dialogues = new HashMap<String, Dialogue>();
    }

    public void addDialogue(String id, Dialogue dialogue) {
        dialogues.put(id, dialogue);
    }

    public Dialogue getDialogue(String id) {
        return dialogues.get(id);
    }
}

