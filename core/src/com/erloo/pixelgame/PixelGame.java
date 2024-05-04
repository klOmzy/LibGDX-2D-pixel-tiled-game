package com.erloo.pixelgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class PixelGame extends ApplicationAdapter {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private Player player;
	private Texture playerTexture;
	private float mapWidth;
	private float mapHeight;

	@Override
	public void create() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera(w, h);
		camera.position.set(w / 2, h / 2, 0);
		camera.update();

		batch = new SpriteBatch();

		map = new TmxMapLoader().load("map.tmx");
		renderer = new OrthogonalTiledMapRenderer(map);

		playerTexture = new Texture("player.png"); // load the player texture
		player = new Player(playerTexture);

		mapWidth = map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class);
		mapHeight = map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		player.update(Gdx.graphics.getDeltaTime());

		float cameraX = player.getPosition().x;
		float cameraY = player.getPosition().y;

		if (cameraX < camera.viewportWidth / 2) {
			cameraX = camera.viewportWidth / 2;
		} else if (cameraX > mapWidth - camera.viewportWidth / 2) {
			cameraX = mapWidth - camera.viewportWidth / 2;
		}

		if (cameraY < camera.viewportHeight / 2) {
			cameraY = camera.viewportHeight / 2;
		} else if (cameraY > mapHeight - camera.viewportHeight / 2) {
			cameraY = mapHeight - camera.viewportHeight / 2;
		}

		camera.position.set(cameraX, cameraY, 0);
		camera.update();

		renderer.setView(camera);
		renderer.render();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		player.render(batch);
		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		map.dispose();
		renderer.dispose();
	}
}
