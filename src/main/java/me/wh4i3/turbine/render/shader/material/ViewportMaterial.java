package me.wh4i3.turbine.render.shader.material;

import me.wh4i3.turbine.Main;
import me.wh4i3.turbine.render.Renderer;
import me.wh4i3.turbine.render.Window;
import me.wh4i3.turbine.render.shader.Shaders;
import me.wh4i3.turbine.render.shader.uniform.*;
import me.wh4i3.turbine.render.texture.Texture;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.util.Map;

import static org.lwjgl.opengl.GL20C.glUniformMatrix4fv;

public class ViewportMaterial extends AbstractMaterial {
	public ViewportMaterial() {
		super(Shaders.instance().VIEWPORT, Map.ofEntries(
				Map.entry("u_Sampler", new IntUniform("u_Sampler", 0)),
				Map.entry("u_WindowSize", new Vector2fUniform("u_WindowSize", new Vector2f(0.0f))),
				Map.entry("u_ViewportSize", new Vector2fUniform("u_ViewportSize", new Vector2f(Window.VIEWPORT_WIDTH, Window.VIEWPORT_HEIGHT))),
				Map.entry("u_SubPixel", new Vector2fUniform("u_SubPixel", new Vector2f(0)))
		));
	}

	@Override
	public void update() {
		Vector2fUniform windowSize = (Vector2fUniform) this.uniforms().get("u_WindowSize");
		windowSize.set(new Vector2f(Window.current().viewportWidth, Window.current().viewportHeight));

		Vector2fUniform subPixel = (Vector2fUniform) this.uniforms().get("u_SubPixel");
		subPixel.set(new Vector2f(Renderer.instance().subPixelView));
	}
}
