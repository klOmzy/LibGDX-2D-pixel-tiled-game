package com.erloo.pixelgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class PixelGame extends ApplicationAdapter {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private Player player;
	private TextureAtlas playerAtlas;
	private float mapWidth;
	private float mapHeight;
	private float viewportWidth = 200;
	private float viewportHeight = 150;
	private TiledMapTileLayer collisionLayer;


	@Override
	public void create() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		map = new TmxMapLoader().load("map.tmx");
		collisionLayer = (TiledMapTileLayer) map.getLayers().get("CollisionLayer");

		camera = new OrthographicCamera(viewportWidth, viewportHeight);
		camera.position.set(viewportWidth / 2, viewportHeight / 2, 0);
		camera.update();

		batch = new SpriteBatch();


		renderer = new OrthogonalTiledMapRenderer(map);

		playerAtlas = new TextureAtlas("player/player.atlas"); // load the player atlas
		player = new Player(playerAtlas, collisionLayer, camera);

		mapWidth = map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class);
		mapHeight = map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class);
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
		playerAtlas.dispose();
	}
}