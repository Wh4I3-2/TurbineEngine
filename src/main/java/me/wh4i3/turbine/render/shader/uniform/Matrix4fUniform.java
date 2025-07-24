package me.wh4i3.turbine.render.shader.uniform;

import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL45C.glUniformMatrix4fv;

public class Matrix4fUniform extends ShaderUniform<Matrix4f> {
	public Matrix4fUniform(String name, Matrix4f defaultValue) {
		super(name, defaultValue, ((location, value) -> {
			glUniformMatrix4fv(location, false, value.get(new float[16]));
		}));
	}
}
