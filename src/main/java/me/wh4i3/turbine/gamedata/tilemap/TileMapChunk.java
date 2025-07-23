package me.wh4i3.turbine.gamedata.tilemap;

import me.wh4i3.turbine.Main;
import me.wh4i3.turbine.gamedata.gameobject.TileMap;
import me.wh4i3.turbine.render.Buffer.IndexBuffer;
import me.wh4i3.turbine.render.Buffer.VertexArray;
import me.wh4i3.turbine.render.Buffer.VertexBuffer;
import me.wh4i3.turbine.render.Buffer.VertexBufferLayout;
import me.wh4i3.turbine.render.Renderer;
import me.wh4i3.turbine.render.Window;
import me.wh4i3.turbine.render.shader.uniform.TextureUniform;
import me.wh4i3.turbine.render.shader.uniform.Vector2iUniform;
import me.wh4i3.turbine.render.texture.AtlasTexture;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileMapChunk {
	public static final VertexBufferLayout VERTEX_BUFFER_LAYOUT;

	static {
		VERTEX_BUFFER_LAYOUT = new VertexBufferLayout();
		VERTEX_BUFFER_LAYOUT.pushFloat(2);
		VERTEX_BUFFER_LAYOUT.pushFloat(2);
		VERTEX_BUFFER_LAYOUT.pushFloat(1);
	}

	private final TileMap owner;

	private int highestLocation;
	private final Map<Vector2i, Integer> tileDataLocation = new HashMap<>();
	private final Map<Integer, float[]> vertexTileData = new HashMap<>();
	private final Map<Integer, int[]> indexTileData = new HashMap<>();

	private Map<Vector2i, Tile> tiles = new HashMap<>();

	private boolean dataNotSynced = true;

	private VertexArray vertexArray;
	private IndexBuffer indexBuffer;
	private VertexBuffer vertexBuffer;
	private float[] vertexData = new float[0];
	private int[] indexData = new int[0];

	private int vertexDataHash;
	private int indexDataHash;

	public TileMapChunk(TileMap owner) {
		this.owner = owner;
		this.vertexArray = new VertexArray();
	}

	public void setTile(Vector2i cell, Tile tile) {
		this.tiles.put(cell, tile);
		setTileData(cell, tile);
	}

	private void setTileData(Vector2i cell, Tile tile) {
		int location;
		if (tileDataLocation.containsKey(cell)) {
			location = tileDataLocation.get(cell);
		} else {
			location = ++highestLocation;
			tileDataLocation.put(cell, location);
		}

		int indexLocation = location * 4;
		indexTileData.put(location, new int[] {
				indexLocation,   indexLocation+1, indexLocation+2,
				indexLocation+2, indexLocation+3, indexLocation
		});

		AtlasTexture texture = this.owner.tileSet.textures.get(tile.textureSlot);

		Vector2f gridSize = new Vector2f(texture.gridSize());

		Vector2f normalPos = new Vector2f(tile.atlasCoord).div(gridSize);
		Vector2f atlasTileSize = new Vector2f(1.0f).div(gridSize);

		Vector2f tileSize = this.owner.tileSize;
		Vector2f offset = new Vector2f(cell).mul(tileSize);

		vertexTileData.put(location, new float[] {
				offset.x,              offset.y,              normalPos.x,                   normalPos.y,                   tile.textureSlot,
				offset.x + tileSize.x, offset.y,              normalPos.x + atlasTileSize.x, normalPos.y,                   tile.textureSlot,
				offset.x + tileSize.x, offset.y + tileSize.y, normalPos.x + atlasTileSize.x, normalPos.y + atlasTileSize.y, tile.textureSlot,
				offset.x,              offset.y + tileSize.y, normalPos.x,                   normalPos.y + atlasTileSize.y, tile.textureSlot,
		});

		this.dataNotSynced = true;
	}

	public float[] vertexData() {
		if (this.dataNotSynced) {
			syncData();
		}
		return this.vertexData;
	}

	public int[] indexData() {
		if (this.dataNotSynced) {
			syncData();
		}
		return this.indexData;
	}

	private boolean syncFinished = true;

	private void syncData() {
		this.dataNotSynced = false;

		new Thread(() -> {
			syncFinished = false;
			float[] vertexData = new float[highestLocation * 4 * 5];
			List<float[]> vertexDataPoints = this.vertexTileData.values().stream().toList();
			for (int i = 0; i < vertexDataPoints.size(); i++) {
				int id = i * 4 * 5;
				float[] data = vertexDataPoints.get(i);
				System.arraycopy(data, 0, vertexData, id, 4 * 5);
			}

			List<int[]> indexDataPoints = this.indexTileData.values().stream().toList();
			int[] indexData = new int[highestLocation * 6];
			for (int i = 0; i < indexDataPoints.size(); i++) {
				int id = i * 6;
				int[] data = indexDataPoints.get(i);
				System.arraycopy(data, 0, indexData, id, 6);
			}

			this.vertexData = vertexData;
			this.indexData = indexData;
			syncFinished = true;
		}).start();
	}

	public void draw(Vector3f pos) {
		if (this.owner == null) {
			Main.LOGGER.error("TileMapChunk has no Owner");
		}
		if (this.owner.tileSet == null) {
			Main.LOGGER.error("TileSet is null");
			return;
		}

		Renderer.instance().modelMatrix = new Matrix4f().translate(pos);

		if (this.owner.tileSet.modulate != null) {
			this.owner.tileSet.material.modulate = this.owner.tileSet.modulate;
		}

		for (int i = 0; i < this.owner.tileSet.textures.size(); i++) {
			AtlasTexture texture = this.owner.tileSet.textures.get(i);
			texture.bind(i);
			TextureUniform texUniform = (TextureUniform) this.owner.tileSet.material.uniforms().get("u_Tex" + i);
			texUniform.set(texture);
			Vector2iUniform gridSizeUniform = (Vector2iUniform) this.owner.tileSet.material.uniforms().get("u_GridSize" + i);
			gridSizeUniform.set(texture.gridSize());
		}

		int vertexHash = Arrays.hashCode(vertexData());
		int indexHash = Arrays.hashCode(indexData());

		if (this.vertexDataHash != vertexHash || this.indexDataHash != indexHash) {
			this.vertexDataHash = vertexHash;
			this.indexDataHash = indexHash;

			if (this.syncFinished) {
				this.vertexBuffer = new VertexBuffer(vertexData);
				this.vertexArray.addBuffer(this.vertexBuffer, VERTEX_BUFFER_LAYOUT);

				this.indexBuffer = new IndexBuffer(indexData);
			}
		}

		Renderer.instance().draw(this.vertexArray, this.indexBuffer, this.owner.tileSet.material);
	}
}
