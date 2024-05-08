package com.erloo.pixelgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.erloo.pixelgame.damage.HealthBar;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.Input;

public class PixelGame extends ApplicationAdapter {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private SpriteBatch uiBatch; // Новый SpriteBatch для UI-элементов
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private Player player;
	private TextureAtlas playerAtlas;
	private float mapWidth;
	private float mapHeight;
	private float viewportWidth = 200;
	private float viewportHeight = 150;
	private Array<TiledMapTileLayer> collisionLayers;
	private float spawnX;
	private float spawnY;
	private HealthBar healthBar;
	private BitmapFont healthFont;
	private ShapeRenderer shapeRenderer;
	private BitmapFont deathFont;


	@Override
	public void create() {
		map = new TmxMapLoader().load("map.tmx");
		collisionLayers = new Array<>();
		for (MapLayer layer : map.getLayers()) {
			if (layer instanceof TiledMapTileLayer && layer.getProperties().containsKey("collision")) {
				boolean isCollisionLayer = (boolean) layer.getProperties().get("collision");
				if (isCollisionLayer) {
					collisionLayers.add((TiledMapTileLayer) layer);
				}
			}
		}
		if (collisionLayers.size == 0) {
			throw new RuntimeException("Collision layers not found");
		}

		camera = new OrthographicCamera(viewportWidth, viewportHeight);
		camera.position.set(viewportWidth, viewportHeight, 0);
		camera.update();

		batch = new SpriteBatch();
		uiBatch = new SpriteBatch(); // Инициализируем новый SpriteBatch

		// Устанавливаем размеры камеры в соответствии с размерами окна
		camera.setToOrtho(false, viewportWidth, viewportHeight);

		MapObject spawnPoint = map.getLayers().get("Spawn").getObjects().get("SpawnPoint");
		if (spawnPoint != null) {
			spawnX = spawnPoint.getProperties().get("x", Float.class);
			spawnY = spawnPoint.getProperties().get("y", Float.class);
		} else {
			throw new RuntimeException("Spawn point not found");
		}

		renderer = new OrthogonalTiledMapRenderer(map);

		playerAtlas = new TextureAtlas("player/player1.atlas"); // load the player atlas
		player = new Player(playerAtlas, collisionLayers, camera, spawnX, spawnY);

		mapWidth = map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class);
		mapHeight = map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class);

		// Создаем генератор шрифтов из файла TTF
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/SedanSC-Regular.ttf"));

		// Настраиваем параметры шрифта
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 16;
		parameter.color = Color.WHITE;

		// Генерируем BitmapFont из файла TTF
		healthFont = generator.generateFont(parameter);

		// Создаем новый FreeTypeFontParameter с большим размером шрифта для "YOU DIED!"
		FreeTypeFontParameter deathFontParameter = new FreeTypeFontParameter();
		deathFontParameter.size = 48; // Увеличиваем размер шрифта до 32
		deathFontParameter.color = Color.RED;

		// Генерируем новый BitmapFont для "YOU DIED!"
		deathFont = generator.generateFont(deathFontParameter);

		// Освобождаем ресурсы генератора шрифтов
		generator.dispose();

		shapeRenderer = new ShapeRenderer();
		healthBar = new HealthBar(10, 10, 200, 20, new Color(0.3f, 0.3f, 0.3f, 1), new Color(0.8f, 0.2f, 0.2f, 1), healthFont);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		player.update(Gdx.graphics.getDeltaTime(), mapWidth, mapHeight);
		player.centerCamera();

		float cameraX = player.getPosition().x;
		float cameraY = player.getPosition().y;

		if (cameraX < viewportWidth / 2) {
			cameraX = viewportWidth / 2;
		} else if (cameraX > mapWidth - viewportWidth / 2) {
			cameraX = mapWidth - viewportWidth / 2;
		}

		if (cameraY < viewportHeight / 2) {
			cameraY = viewportHeight / 2;
		} else if (cameraY > mapHeight - viewportHeight / 2) {
			cameraY = mapHeight - viewportHeight / 2;
		}

		camera.position.set(cameraX, cameraY, 0);
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		renderer.setView(camera);
		renderer.render();

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		healthBar.renderShape(shapeRenderer, player.getHealth(), player.getMaxHealth());
		shapeRenderer.end();
		// Проверка урона (Потом удалить)
		if (Gdx.input.isKeyJustPressed(Input.Keys.U)) {
			player.takeDamage(10);
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
			player.takeDamage(100);
		}

		batch.begin();
		player.render(batch);
		batch.end();

		uiBatch.begin();
		if (player.isDead()) {
			deathFont.setColor(Color.RED);
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			GlyphLayout layout = new GlyphLayout(deathFont, "YOU DIED!");
			deathFont.draw(uiBatch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, Gdx.graphics.getHeight() / 2);
		}
		else {
			healthBar.renderText(uiBatch, player.getHealth(), player.getMaxHealth());
		}
		uiBatch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		map.dispose();
		renderer.dispose();
		playerAtlas.dispose();
		healthFont.dispose();
		deathFont.dispose(); // Добавляем dispose для deathFont
	}
}