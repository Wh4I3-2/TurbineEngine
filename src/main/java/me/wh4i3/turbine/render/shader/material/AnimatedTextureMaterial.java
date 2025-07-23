package me.wh4i3.turbine.render.shader.material;

import me.wh4i3.turbine.render.texture.Texture;
import me.wh4i3.turbine.render.shader.uniform.FloatUniform;
import me.wh4i3.turbine.render.shader.uniform.IntUniform;
import me.wh4i3.turbine.render.shader.Shaders;
import me.wh4i3.turbine.render.shader.uniform.TextureUniform;

import java.util.Map;

public class AnimatedTextureMaterial extends AbstractMaterial {
	public AnimatedTextureMaterial(Texture texture, int frames, float fps) {
		super(Shaders.instance().ANIMATED_TEXTURE, Map.ofEntries(
				Map.entry("u_Sampler", new TextureUniform("u_Sampler", texture)),
				Map.entry("u_Frames",  new IntUniform("u_Frames", frames)),
				Map.entry("u_FPS",     new FloatUniform("u_FPS", fps))
		));
	}

	@Override
	public void update() {
	}
}
