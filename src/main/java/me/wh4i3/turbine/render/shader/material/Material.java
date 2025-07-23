package me.wh4i3.turbine.render.shader.material;

import me.wh4i3.turbine.render.shader.Shader;
import me.wh4i3.turbine.render.shader.uniform.ShaderUniform;
import org.joml.Vector3f;

import java.util.Map;

public class Material extends AbstractMaterial {
	public Material(Shader shader, Map<String, ShaderUniform<?>> uniforms, Vector3f modulate) {
		super(shader, uniforms, modulate);
	}

	public Material(Shader shader, Map<String, ShaderUniform<?>> uniforms) {
		super(shader, uniforms);
	}

	@Override
	public void update() {

	}
}
