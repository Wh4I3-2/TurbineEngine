package me.wh4i3.turbine.render.shader.uniform;

import org.lwjgl.opengl.GL45C;

public class FloatUniform extends ShaderUniform<Float> {
	public FloatUniform(String name, Float defaultValue) {
		super(name, defaultValue, (GL45C::glUniform1f));
	}
}
