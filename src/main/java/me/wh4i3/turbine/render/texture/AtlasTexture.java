package me.wh4i3.turbine.render.texture;

import me.wh4i3.turbine.resource.ResourceKey;
import org.joml.Vector2i;

import static org.lwjgl.opengl.GL45C.*;

public class AtlasTexture extends Texture {
	private final Vector2i gridSize;

	public AtlasTexture(ResourceKey key, Vector2i gridSize, int filter) {
		super(key, filter);

		this.gridSize = gridSize;
	}

	public AtlasTexture(ResourceKey key, Vector2i gridSize) {
		this(key, gridSize, GL_NEAREST);
	}

	public Vector2i gridSize() {
		return new Vector2i(this.gridSize);
	}
}
