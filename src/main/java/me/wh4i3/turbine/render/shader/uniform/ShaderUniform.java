package me.wh4i3.turbine.render.shader.uniform;

import me.wh4i3.turbine.render.shader.Shader;

public class ShaderUniform<T> {
	public interface ISetUniform<T> {
		void set(int location, T value);
	}

	private final String name;
	private T value;
	private ISetUniform<T> setUniformFunction;

	public ShaderUniform(String name, T defaultValue, ISetUniform<T> setUniformFunction) {
		this.name = name;
		this.value = defaultValue;
		this.setUniformFunction = setUniformFunction;
	}

	public T get() {
		return this.value;
	}

	public void set(T value) {
		this.value = value;
	}

	public void updateUniform(Shader shader) {
		if (!shader.uniforms().containsKey(this.name)) {
			return;
		}

		shader.bind();
		int location = shader.uniforms().get(this.name);
		this.setUniformFunction.set(location, this.value);
	}
}
