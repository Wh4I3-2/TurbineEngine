package me.wh4i3.turbine.render.shader.uniform;

import org.joml.Vector2f;

import static org.lwjgl.opengl.GL45C.glUniform2f;

public class Vector2fUniform extends ShaderUniform<Vector2f> {
	public Vector2fUniform(String name, Vector2f defaultValue) {
		super(name, defaultValue, ((location, value) -> {
			glUniform2f(location, value.x, value.y);
		}));
	}
}
