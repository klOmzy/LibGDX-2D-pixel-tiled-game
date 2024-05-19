package com.erloo.pixelgame;

public class HealthPotion {
    private final int HEAL_AMOUNT = 50;
    private final int COST = 50;

    public void use(Player player) {
        player.heal(HEAL_AMOUNT);
    }
}

