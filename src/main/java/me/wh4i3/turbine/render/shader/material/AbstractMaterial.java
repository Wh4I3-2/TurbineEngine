package me.wh4i3.turbine.render.shader.material;

import me.wh4i3.turbine.render.shader.Shader;
import me.wh4i3.turbine.render.shader.uniform.ShaderUniform;
import org.joml.Vector3f;

import java.util.Map;

public abstract class AbstractMaterial {
	private final Shader shader;
	private final Map<String, ShaderUniform<?>> uniforms;

	public Vector3f modulate;

	public AbstractMaterial(Shader shader, Map<String, ShaderUniform<?>> uniforms, Vector3f modulate) {
		this.shader = shader;
		this.uniforms = uniforms;
		this.modulate = modulate;
	}

	public AbstractMaterial(Shader shader, Map<String, ShaderUniform<?>> uniforms) {
		this(shader, uniforms, new Vector3f(1.0f));
	}

	public void bind() {
		this.shader().bind();

		for (String key : this.uniforms().keySet()) {
			ShaderUniform<?> uniform = this.uniforms().get(key);
			uniform.updateUniform(this.shader());
		}
	};

	public abstract void update();

	public Shader shader() {
		return this.shader;
	}

	public Map<String, ShaderUniform<?>> uniforms() {
		return this.uniforms;
	}
}
