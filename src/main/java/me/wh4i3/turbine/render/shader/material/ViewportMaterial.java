package me.wh4i3.turbine.render.shader.material;

import me.wh4i3.turbine.Main;
import me.wh4i3.turbine.render.Window;
import me.wh4i3.turbine.render.shader.Shaders;
import me.wh4i3.turbine.render.shader.uniform.FloatUniform;
import me.wh4i3.turbine.render.shader.uniform.IntUniform;
import me.wh4i3.turbine.render.shader.uniform.TextureUniform;
import me.wh4i3.turbine.render.shader.uniform.Vector2fUniform;
import me.wh4i3.turbine.render.texture.Texture;
import org.joml.Vector2f;

import java.util.Map;

public class ViewportMaterial extends AbstractMaterial {
	public ViewportMaterial() {
		super(Shaders.instance().VIEWPORT, Map.ofEntries(
				Map.entry("u_Sampler", new IntUniform("u_Sampler", 0)),
				Map.entry("u_WindowSize", new Vector2fUniform("u_WindowSize", new Vector2f(0.0f))),
				Map.entry("u_ViewportSize", new Vector2fUniform("u_ViewportSize", new Vector2f(Window.VIEWPORT_WIDTH, Window.VIEWPORT_HEIGHT)))
		));
	}

	@Override
	public void update() {
		Vector2fUniform windowSize = (Vector2fUniform) this.uniforms().get("u_WindowSize");
		windowSize.set(new Vector2f(Window.current().viewportWidth, Window.current().viewportHeight));
	}
}
