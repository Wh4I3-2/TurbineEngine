package me.wh4i3.turbine.gamedata.tilemap;

import org.joml.Vector2i;

public class Tile {
	public int textureSlot;
	public Vector2i atlasCoord;

	public Tile(int textureSlot, Vector2i atlasCoord) {
		this.textureSlot = textureSlot;
		this.atlasCoord = atlasCoord;
	}
}