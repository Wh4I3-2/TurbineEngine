package me.wh4i3.turbine.render.shader.uniform;

import org.lwjgl.opengl.GL45C;

public class IntUniform extends ShaderUniform<Integer> {
	public IntUniform(String name, Integer defaultValue) {
		super(name, defaultValue, (GL45C::glUniform1i));
	}
}
