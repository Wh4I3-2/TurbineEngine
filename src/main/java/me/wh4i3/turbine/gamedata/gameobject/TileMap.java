package me.wh4i3.turbine.gamedata.gameobject;

import me.wh4i3.turbine.Main;
import me.wh4i3.turbine.gamedata.tilemap.Tile;
import me.wh4i3.turbine.gamedata.tilemap.TileMapChunk;
import me.wh4i3.turbine.gamedata.tilemap.TileSet;
import me.wh4i3.turbine.render.Renderer;
import me.wh4i3.turbine.render.Window;
import org.joml.*;

import java.lang.Math;
import java.util.*;

public abstract class TileMap extends GameObject {
	public Vector2f tileSize;
	public TileSet tileSet;
	public Vector2i chunkSize = new Vector2i(16);

	private Map<Vector2i, TileMapChunk> chunks = new HashMap<>();

	private Map<Vector4i, Tile> tilesToUpdate = new HashMap<>();

	public TileMap() {
		this.tileSet = new TileSet();
	}

	public Vector4f viewMatrixChunk() {
		Vector3f viewMatrix = new Vector3f(0.0f);
		viewMatrix = Renderer.instance().viewMatrix.getTranslation(viewMatrix);
		viewMatrix.mul(-1);
		viewMatrix.add((float) Window.VIEWPORT_WIDTH / 2.0f, (float) Window.VIEWPORT_HEIGHT / 2.0f, 0.0f);
		Vector2f chunkPixelSize = new Vector2f(tileSize.x * chunkSize.x, tileSize.y * chunkSize.y);
		viewMatrix = new Vector3f(viewMatrix.x / chunkPixelSize.x, viewMatrix.y / chunkPixelSize.y, viewMatrix.z);

		Vector2f rounded = new Vector2f((float)Math.floor(viewMatrix.x), (float)Math.floor(viewMatrix.y));
		Vector2f subChunk = new Vector2f(rounded).sub(viewMatrix.x, viewMatrix.y);

		return new Vector4f(rounded.x, rounded.y, subChunk.x, subChunk.y);
	}

	public void setTile(Vector2i cell, Tile tile) {
		Vector2f chunkFloatPos = new Vector2f(cell).div(new Vector2f(chunkSize));
		Vector2f roundedChunkFloatPos = new Vector2f(chunkFloatPos).round();
		Vector2i chunkPos = new Vector2i((int)roundedChunkFloatPos.x, (int)roundedChunkFloatPos.y);

		Vector2i localCell = new Vector2i(cell).sub(new Vector2i(chunkPos).mul(chunkSize));
		tilesToUpdate.put(new Vector4i(localCell.x, localCell.y, chunkPos.x, chunkPos.y), tile);
	}

	public Map<Vector2i, TileMapChunk> chunks() {
		return this.chunks;
	}

	@Override
	public void update() {
		for (Vector4i key : tilesToUpdate.keySet()) {
			Vector2i localCell = new Vector2i(key.x, key.y);
			Vector2i chunkPos = new Vector2i(key.z, key.w);
			Tile tile = tilesToUpdate.get(key);

			if (!chunks.containsKey(chunkPos)) {
				chunks.put(chunkPos, new TileMapChunk(this));
			}

			TileMapChunk chunk = chunks.get(chunkPos);
			chunk.setTileNoSync(localCell, tile);
			chunk.dataNotSynced = true;
		}
		this.tilesToUpdate.clear();
	}

	public abstract void enterEmptyChunk(Vector2i pos);

	@Override
	public void draw() {
		Vector4f viewMatrixChunk = viewMatrixChunk();
		Vector2i centerChunk = new Vector2i((int) viewMatrixChunk.x, (int) viewMatrixChunk.y);

		List<Vector2i> visibleChunks = getVisibleChunks(centerChunk);

		for (Vector2i chunkPos : visibleChunks) {
			TileMapChunk chunk = chunks.get(chunkPos);
			if (chunk == null) {
				enterEmptyChunk(chunkPos); // populate on demand
				continue;
			}

			Vector3f chunkWorldPos = new Vector3f(chunkPos.x * chunkSize.x * tileSize.x,
					chunkPos.y * chunkSize.y * tileSize.y,
					0.0f);
			chunk.draw(chunkWorldPos);
		}
	}

	private List<Vector2i> getVisibleChunks(Vector2i center) {
		int radius = 2;

		List<Vector2i> chunks = new ArrayList<>(9);

		for (int dx = -radius; dx <= radius; dx++) {
			for (int dy = -radius; dy <= radius; dy++) {
				chunks.add(new Vector2i(center.x + dx, center.y + dy));
			}
		}

		return chunks;
	}

}
