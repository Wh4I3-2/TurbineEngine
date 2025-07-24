package me.wh4i3.turbine.game;

import me.wh4i3.turbine.gamedata.gameobject.TileMap;
import me.wh4i3.turbine.gamedata.tilemap.Tile;
import me.wh4i3.turbine.gamedata.tilemap.TileMapChunk;
import me.wh4i3.turbine.render.texture.AtlasTexture;
import me.wh4i3.turbine.resource.ResourceKey;
import org.joml.*;
import org.joml.Math;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfiniteTileMap extends TileMap {
	Vector3f modulate;

	public InfiniteTileMap(int z, Vector3f modulate) {
		this.z = z;
		this.modulate = modulate;
	}

	@Override
	public void ready() {
		this.chunkSize = new Vector2i(64);
		this.tileSet.modulate = modulate;

		this.tileSize = new Vector2f(16.0f);
		this.tileSet.textures = List.of(
				new AtlasTexture(ResourceKey.withDefaultNamespace("smb.png"), new Vector2i(11))
		);
	}

	@Override
	public void enterEmptyChunk(Vector2i pos) {
		TileMapChunk chunk = new TileMapChunk(this);
		this.chunks().put(pos, chunk);

		Map<Vector2i, Tile> generated = new HashMap<>();

		for (int x = 0; x < this.chunkSize.x; x++) {
			for (int y = 0; y < this.chunkSize.y; y++) {
				Random random = new Random();

				if (random.nextFloat() > 0.5) {
					continue;
				}

				Vector2i localCell = new Vector2i(x, y);
				Vector2i atlasCoord = new Vector2i(org.joml.Math.round(random.nextFloat() * 10.0f), Math.round(random.nextFloat() * 10.0f)); // Just using x, y for visual diversity
				generated.put(localCell, new Tile(0, atlasCoord));
			}
		}

		chunk.setTiles(generated); // batch write
	}

	@Override
	public void physicsUpdate() {
	}
}
