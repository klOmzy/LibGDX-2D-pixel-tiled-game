package com.erloo.pixelgame.dialogues;

public class DialogueOption {
    private String optionText;
    private Dialogue nextDialogue;
    private Runnable action; // Новое поле для хранения ссылки на метод
    public DialogueOption(String optionText, Dialogue nextDialogue, Runnable action) {
        this.optionText = optionText;
        this.nextDialogue = nextDialogue;
        this.action = action; // Инициализируем новое поле
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public Dialogue getNextDialogue() {
        return nextDialogue;
    }
    public Runnable getAction() { // Геттер для нового поля
        return action;
    }
    public void setNextDialogue(Dialogue nextDialogue) {
        this.nextDialogue = nextDialogue;
    }
}


