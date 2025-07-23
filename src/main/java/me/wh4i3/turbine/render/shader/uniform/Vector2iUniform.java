package me.wh4i3.turbine.render.shader.uniform;

import org.joml.Vector2i;

import static org.lwjgl.opengl.GL45C.glUniform2i;

public class Vector2iUniform extends ShaderUniform<Vector2i> {
	public Vector2iUniform(String name, Vector2i defaultValue) {
		super(name, defaultValue, ((location, value) -> {
			glUniform2i(location, value.x, value.y);
		}));
	}
}
