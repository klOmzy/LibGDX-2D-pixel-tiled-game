package com.erloo.pixelgame.units.hostile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.erloo.pixelgame.Player;
import com.erloo.pixelgame.damage.Damager;

public class Enemy implements Damager {
    protected int damage;
    protected boolean isDead;
    protected boolean isBlinking;
    protected float blinkTimer;
    protected float blinkDuration; // Длительность мигания (например, 0.1 секунды)
    protected float blinkInterval; // Интервал между миганиями (например, 0.1 секунды)
    protected boolean isInvulnerable;
    protected float invulnerabilityTimer;
    protected float invulnerabilityDuration;
    protected int health;

    protected Player player;
    protected boolean isMoving; // добавляем переменную isMoving
    public Enemy(int damage, Player player) {
        this.damage = damage;
        this.player = player;
    }
    public boolean isMoving() { // добавляем геттер для isMoving
        return isMoving;
    }

    public void setMoving(boolean moving) { // добавляем сеттер для isMoving
        isMoving = moving;
    }
    @Override
    public int getDamage() {
        return damage;
    }
    public void takeDamage(int damage) {
        if (!isInvulnerable) {
            health -= damage;
            isBlinking = true;
            blinkTimer = 0;
            isInvulnerable = true;
            invulnerabilityTimer = 0;
            System.out.println("Unit's HP: " + health);

            if (health <= 0) {
                reward();
                deathmessage();
                isDead = true;
            }
        }
    }
    public int getHealth() {
        return health;
    }
    public void reward(){
        int COIN_REWARD = 10;
        player.getCoins().addCoins(COIN_REWARD);
    }
    public void deathmessage(){
        String message = "Unit is dead";
        System.out.println(message);
    }
    public boolean isDead() {
        return isDead;
    }
    public void setInvulnerable(float delta){
        invulnerabilityDuration = 0.5f;
        if (isInvulnerable) {
            invulnerabilityTimer += delta;
            if (invulnerabilityTimer >= invulnerabilityDuration) {
                isInvulnerable = false;
            }
        }
    }
    public void blinking(SpriteBatch unitBatch){
        blinkDuration = 0.02f;
        blinkInterval = 0.1f;
        if (isBlinking) {
            blinkTimer += Gdx.graphics.getDeltaTime();
            if (blinkTimer > blinkDuration * 8) { // Умножим blinkDuration на количество миганий (в этом случае 5)
                isBlinking = false;
                // Сбросить цвет batch в исходное состояние
                unitBatch.setColor(Color.WHITE);
            } else if (Math.floor(blinkTimer / (blinkDuration + blinkInterval)) % 2 == 0) { // Проверим, следует ли отображать красный цвет или белый
                // Установить красный цвет для мигания
                unitBatch.setColor(Color.RED);
            } else {
                // Сбросить цвет batch в исходное состояние
                unitBatch.setColor(Color.WHITE);
            }
        } else {
            // Убедитесь, что цвет batch сброшен в исходное состояние, когда нет мигания
            unitBatch.setColor(Color.WHITE);
        }
    }
}