package me.wh4i3.turbine.gamedata.tilemap;

import me.wh4i3.turbine.render.shader.Shaders;
import me.wh4i3.turbine.render.shader.material.Material;
import me.wh4i3.turbine.render.shader.uniform.TextureUniform;
import me.wh4i3.turbine.render.shader.uniform.Vector2iUniform;
import me.wh4i3.turbine.render.texture.AtlasTexture;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;

public class TileSet {
	public Vector3f modulate;
	public List<AtlasTexture> textures;

	public List<Tile> tiles;

	public final Material material = new Material(Shaders.instance().TILE_MAP, Map.ofEntries(
			Map.entry("u_Tex0", new TextureUniform("u_Tex0", null)),
			Map.entry("u_GridSize0", new Vector2iUniform("u_GridSize0", null))
	));


}
