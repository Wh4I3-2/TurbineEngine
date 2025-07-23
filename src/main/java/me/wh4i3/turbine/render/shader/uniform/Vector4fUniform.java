package me.wh4i3.turbine.render.shader.uniform;

import org.joml.Vector4f;

import static org.lwjgl.opengl.GL45C.glUniform4f;

public class Vector4fUniform extends ShaderUniform<Vector4f> {
	public Vector4fUniform(String name, Vector4f defaultValue) {
		super(name, defaultValue, ((location, value) -> {
			glUniform4f(location, value.x, value.y, value.z, value.w);
		}));
	}
}
