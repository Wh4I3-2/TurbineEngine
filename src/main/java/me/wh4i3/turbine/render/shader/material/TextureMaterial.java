package me.wh4i3.turbine.render.shader.material;

import me.wh4i3.turbine.render.shader.Shaders;
import me.wh4i3.turbine.render.shader.uniform.TextureUniform;
import me.wh4i3.turbine.render.texture.Texture;

import java.util.Map;

public class TextureMaterial extends AbstractMaterial {
	public TextureMaterial(Texture texture) {
		super(Shaders.instance().TEXTURE, Map.ofEntries(
				Map.entry("u_Sampler", new TextureUniform("u_Sampler", texture))
		));
	}

	public TextureMaterial setTexture(Texture texture) {
		if (this.uniforms() == null) {
			return this;
		}
		TextureUniform uniform = (TextureUniform) this.uniforms().get("u_Sampler");
		uniform.set(texture);
		return this;
	}

	@Override
	public void update() {
	}
}
