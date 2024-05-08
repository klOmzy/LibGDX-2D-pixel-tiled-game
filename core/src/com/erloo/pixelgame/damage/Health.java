package com.erloo.pixelgame.damage;

public class Health {
    private int currentHealth;
    private int maxHealth;
    private float regenerationTimer;

    public Health(int maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = Math.min(Math.max(0, currentHealth), maxHealth);
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void heal(int amount) {
        currentHealth = Math.min(currentHealth + amount, maxHealth);
    }

    public void takeDamage(int damage) {
        currentHealth = Math.max(0, currentHealth - damage);
        regenerationTimer = 10; // Сбрасываем таймер регенерации на 10 секунд
    }

    public void regenerate(float delta) {
        if (currentHealth < maxHealth) {
            regenerationTimer -= delta;
            if (regenerationTimer <= 0) { // Таймер ожидания регенерации истек
                currentHealth += 1; // Увеличиваем на 1 HP
                if (currentHealth > maxHealth) {
                    currentHealth = maxHealth;
                }
                regenerationTimer = 1; // Сбрасываем таймер регенерации на 1 секунду
            }
        }
    }
}

