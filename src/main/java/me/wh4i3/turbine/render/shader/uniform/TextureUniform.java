package me.wh4i3.turbine.render.shader.uniform;

import me.wh4i3.turbine.render.texture.Texture;

import static org.lwjgl.opengl.GL45C.glUniform1i;

public class TextureUniform extends ShaderUniform<Texture> {
	public TextureUniform(String name, Texture defaultValue) {
		super(name, defaultValue, (location, value) -> {
			value.bind();
			glUniform1i(location, 0);
		});
	}
}
