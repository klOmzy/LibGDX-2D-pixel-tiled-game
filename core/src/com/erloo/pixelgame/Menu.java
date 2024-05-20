package com.erloo.pixelgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Menu {
    private SpriteBatch batch;
    private BitmapFont font;
    private String[] options;
    private int selectedIndex;
    private PixelGame.GameState state;
    private PixelGame game;

    public Menu(PixelGame game, String[] options, int selectedIndex) {
        this.options = options;
        this.selectedIndex = selectedIndex;
        this.game = game;
        batch = new SpriteBatch();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arial.ttf"));

        // Настраиваем параметры шрифта
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        parameter.color = Color.WHITE;

        // Генерируем BitmapFont из файла TTF
        font = generator.generateFont(parameter);
        generator.dispose();
    }

    public void render() {
        batch.begin();
        font.draw(batch, "The 2D Tiled-based pixel game", Gdx.graphics.getWidth() / 2 - 250, Gdx.graphics.getHeight() - 50);
        for (int i = 0; i < options.length; i++) {
            if (i == selectedIndex) {
                font.setColor(Color.RED);
            } else {
                font.setColor(Color.WHITE);
            }
            font.draw(batch, options[i], Gdx.graphics.getWidth() / 2 - 280, Gdx.graphics.getHeight() - 150 - i * 50);
        }
        font.setColor(Color.WHITE);
        batch.end();
    }

    public void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedIndex = (selectedIndex - 1 + options.length) % options.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedIndex = (selectedIndex + 1) % options.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            onOptionSelected(options[selectedIndex]);
        }
    }
    protected void onOptionSelected(String option) {
        switch (option) {
            case "Start Game":
                game.startGame();
                // Загрузка вашего уровня
                break;
            case "Resume":
                game.startGame();
                // Загрузка вашего уровня
                break;
            case "Exit":
                Gdx.app.exit();
                break;
        }
    }
    public void setMenuItemText(int index, String text) {
        options[index] = text;
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}

