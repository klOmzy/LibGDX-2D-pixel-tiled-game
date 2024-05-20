package com.erloo.pixelgame;

public class HealthPotion {
    private final int HEAL_AMOUNT = 100;
    public void use(Player player) {
        player.heal(HEAL_AMOUNT);
    }
}

