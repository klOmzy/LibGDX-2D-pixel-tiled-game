package com.erloo.pixelgame.units;

import com.erloo.pixelgame.damage.Damageable;

public abstract class NPC implements Damageable {
    protected boolean isDead;
    protected boolean isBlinking;
    protected float blinkTimer;
    protected float blinkDuration; // Длительность мигания (например, 0.1 секунды)
    protected float blinkInterval; // Интервал между миганиями (например, 0.1 секунды)
    protected boolean isInvulnerable;
    protected float invulnerabilityTimer;
    protected float invulnerabilityDuration;
    protected int health;
    // Add common properties and methods for NPCs.
    public void takeDamage(int damage) {
        if (!isInvulnerable) {
            health -= damage;
            isBlinking = true;
            blinkTimer = 0;
            isInvulnerable = true;
            invulnerabilityTimer = 0;
            if (health <= 0) {
                deathmessage();
                isDead = true;
            }
        }
    }
    public void deathmessage(){
        String message = "Unit is dead";
        System.out.println(message);
    }
    public boolean isDead() {
        return isDead;
    }
}
