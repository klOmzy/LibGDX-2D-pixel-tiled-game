package com.erloo.pixelgame;

public class Coin {
    private int coins;

    public Coin() {
        this.coins = 0;
    }

    public void addCoins(int amount) {
        this.coins += amount;
    }

    public boolean removeCoins(int amount) {
        if (this.coins >= amount) {
            this.coins -= amount;
            return true;
        } else {
            return false;
        }
    }

    public int getCoins() {
        return this.coins;
    }
}

