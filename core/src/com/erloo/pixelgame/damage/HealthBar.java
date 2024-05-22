package com.erloo.pixelgame.damage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class HealthBar {
    private float x, y, width, height;
    private Color backgroundColor, foregroundColor;
    private BitmapFont font;

    public HealthBar(float x, float y, float width, float height, Color backgroundColor, Color foregroundColor, BitmapFont font) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
        this.font = font;
    }

    public void renderShape(ShapeRenderer shapeRenderer, int currentHealth, int maxHealth) {
        shapeRenderer.setColor(backgroundColor);
        shapeRenderer.rect(x, y, width, height);

        float healthPercentage = (float) currentHealth / maxHealth;
        float healthBarWidth = width * healthPercentage;

        shapeRenderer.setColor(foregroundColor);
        shapeRenderer.rect(x, y, healthBarWidth, height);
    }
    public void renderText(SpriteBatch batch, int currentHealth, int maxHealth) {
        font.setColor(Color.WHITE);
        font.draw(batch, currentHealth + " / " + maxHealth, x+ 10, y  + 16);
    }
    public void bossRenderText(SpriteBatch batch, int currentHealth, int maxHealth) {
        font.setColor(Color.WHITE);
        font.draw(batch, currentHealth + " / " + maxHealth, x+ 130, y  + 18);
    }
}
