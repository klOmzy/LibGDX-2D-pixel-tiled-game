package com.erloo.pixelgame.units.hostile;

import com.erloo.pixelgame.damage.Damager;

public class Enemy implements Damager {
    private int damage;

    public Enemy(int damage) {
        this.damage = damage;
    }

    @Override
    public int getDamage() {
        return damage;
    }
}