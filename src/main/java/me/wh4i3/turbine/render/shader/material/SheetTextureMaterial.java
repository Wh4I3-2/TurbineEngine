package me.wh4i3.turbine.render.shader.material;

import me.wh4i3.turbine.render.texture.Texture;
import me.wh4i3.turbine.render.shader.Shaders;
import me.wh4i3.turbine.render.shader.uniform.TextureUniform;
import me.wh4i3.turbine.render.shader.uniform.Vector2iUniform;
import org.joml.Vector2i;

import java.util.Map;

public class SheetTextureMaterial extends AbstractMaterial {
	public SheetTextureMaterial(Texture texture, Vector2i size, Vector2i cell) {
		super(Shaders.instance().SHEET_TEXTURE, Map.ofEntries(
				Map.entry("u_Sampler", new TextureUniform("u_Sampler", texture)),
				Map.entry("u_Size", new Vector2iUniform("u_Size", size)),
				Map.entry("u_Cell", new Vector2iUniform("u_Cell", cell))
		));
	}

	public SheetTextureMaterial setTexture(Texture texture) {
		if (this.uniforms() == null) {
			return this;
		}
		TextureUniform uniform = (TextureUniform) this.uniforms().get("u_Sampler");
		uniform.set(texture);
		return this;
	}

	public SheetTextureMaterial setSize(Vector2i size) {
		if (this.uniforms() == null) {
			return this;
		}
		Vector2iUniform uniform = (Vector2iUniform) this.uniforms().get("u_Size");
		uniform.set(size);
		return this;
	}

	public SheetTextureMaterial setCell(Vector2i cell) {
		if (this.uniforms() == null) {
			return this;
		}
		Vector2iUniform uniform = (Vector2iUniform) this.uniforms().get("u_Cell");
		uniform.set(cell);
		return this;
	}

	@Override
	public void update() {
	}
}
