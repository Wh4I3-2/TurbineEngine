package me.wh4i3.turbine.render.shader;

import me.wh4i3.turbine.resource.ResourceKey;

public class Shaders {
	private static Shaders instance;

	public static Shaders instance() {
		if (instance == null) {
			instance = new Shaders();
		}
		return instance;
	}


	public final Shader BASIC = new Shader(
			ResourceKey.withDefaultNamespace("basic.vsh"),
			ResourceKey.withDefaultNamespace("basic.fsh")
	);
	public final Shader TEXTURE = new Shader(
			ResourceKey.withDefaultNamespace("texture.vsh"),
			ResourceKey.withDefaultNamespace("texture.fsh")
	);
	public final Shader ANIMATED_TEXTURE = new Shader(
			ResourceKey.withDefaultNamespace("animated_texture.vsh"),
			ResourceKey.withDefaultNamespace("texture.fsh")
	);
	public final Shader SHEET_TEXTURE = new Shader(
			ResourceKey.withDefaultNamespace("sheet_texture.vsh"),
			ResourceKey.withDefaultNamespace("texture.fsh")
	);
	public final Shader VIEWPORT = new Shader(
			ResourceKey.withDefaultNamespace("viewport.vsh"),
			ResourceKey.withDefaultNamespace("viewport.fsh")
	);
	public final Shader TILE_MAP = new Shader(
			ResourceKey.withDefaultNamespace("tile_map.vsh"),
			ResourceKey.withDefaultNamespace("tile_map.fsh")
	);
}
